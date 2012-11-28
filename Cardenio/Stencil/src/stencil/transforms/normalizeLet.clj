(in-ns 'stencil.transform)

;; ------- Validate Let-----
(defn hasBind? [line] (and (list? line) (any= '$C line)))

(defn- validate-line-shape
  [lines]
  (match [lines]
    [([line] :seq)] (list line) ;Last line may be body or binding... 
    [([(line :guard hasBind?) & rest] :seq)] 
         (cons line (validate-line-shape rest))
    [([(line :guard (complement hasBind?)) & rest] :seq)] 
         (throw (RuntimeException. (str "Invalidate let shape: " line)))))

(defn validate-let-shape
  "Let must have a binding on each line, except the last which may be a body instead"
  [program]
  (match [program]
    [(x :guard atom?)] x
    [(['let & letLines] :seq)]
        `(~'let ~@(map validate-let-shape (validate-line-shape letLines)))
    :else (map validate-let-shape program)))
      

;; ------- Normalize shape ----

(defn- list?! [a] (if (list? a) a (list a)))

(defn normalize-let-shape
  "Ensure that all let lines have same form.  Must run before infix->prefix"
  [program]
  (match [program]
    [(x :guard atom?)] x
    [(['let & letLines] :seq)] 
      (cons 'let (map (fn [[t & rst]] (cons (list?! t) rst)) letLines))
    :else (map normalize-let-shape program)))


;; --------  Default Body --------

(defn- make-body
  [bindings]
    (let [names (distinct bindings)]
      `((~'$ptuple (~'quote ~names) ~@names))))


(defn- ensure-body
  ([lines] (ensure-body lines '()))
  ([lines allVars]
   (match [lines]
     [([(['$C vars ops] :seq)] :seq)] 
         (cons `(~'$C ~vars ~ops) (make-body (concat allVars vars)))
     [([(['$C vars ops] :seq) & rest] :seq)] 
         (cons `(~'$C ~vars ~ops) (ensure-body rest (concat allVars vars)))
     [(body :guard list?)] body
     :else (make-body allVars))))


(defn default-let-body
  "Ensure that let's have a body.  Generate one if not.  
  Let's must have vars gathered in list format, operators must all be prefix"
  [program]
  (match [program]
    [(x :guard atom?)] x
    [(['let & parts] :seq)] `(~'let ~@(map default-let-body (ensure-body parts)))
    :else (map default-let-body program)))
