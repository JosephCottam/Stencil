(in-ns 'stencil.transform)

;; ------- Validate Let-----
(defn hasBind? [line] (and (list? line) (some (partial = '$C) line)))

(defn- validateLineShape
  [lines]
  (match [lines]
    [([line] :seq)] (list line) ;Last line may be body or binding... 
    [([(line :guard hasBind?) & rest] :seq)] 
         (cons line (validateLineShape rest))
    [([(line :guard (complement hasBind?)) & rest] :seq)] 
         (throw (RuntimeException. (str "Invalidate let shape: " line)))))

(defn validateLetShape
  "Let must have a binding on each line, except the last which may be a body instead"
  [program]
  (match [program]
    [(x :guard atom?)] x
    [(['let & letLines] :seq)]
        `(~'let ~@(map validateLetShape (validateLineShape letLines)))
    :else (map validateLetShape program)))
      

;; ------- Normalize shape ----

(defn- ensureList [a] (if (list? a) a (list a)))

(defn normalizeLetShape
  "Ensure that all let lines have same form.  Must run before infix->prefix"
  [program]
  (match [program]
    [(x :guard atom?)] x
    [(['let & letLines] :seq)] 
      (cons 'let (map (fn [[t & rst]] (cons (ensureList t) rst)) letLines))
    :else (map normalizeLetShape program)))


;; --------  Default Body --------

(defn makeBody
  [bindings]
    (let [names (distinct bindings)]
      `((~'$ptuple (~'quote ~names) ~@names))))


(defn ensureBody
  ([lines] (ensureBody lines '()))
  ([lines allVars]
   (match [lines]
     [([(['$C vars ops] :seq)] :seq)] 
         (cons `(~'$C ~vars ~ops) (makeBody (concat allVars vars)))
     [([(['$C vars ops] :seq) & rest] :seq)] 
         (cons `(~'$C ~vars ~ops) (ensureBody rest (concat allVars vars)))
     [(body :guard list?)] body
     :else (makeBody allVars))))


(defn defaultLetBody
  "Ensure that let's have a body.  Generate one if not.  
  Let's must have vars gathered in list format, operators must all be prefix"
  [program]
  (match [program]
    [(x :guard atom?)] x
    [(['let & parts] :seq)] `(~'let ~@(map defaultLetBody (ensureBody parts)))
    :else (map defaultLetBody program)))
