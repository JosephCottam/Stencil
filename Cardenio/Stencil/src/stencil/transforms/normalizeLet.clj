(in-ns 'stencil.transform)

;; ------- Validate Let-----
(defn hasBind? [line] (and (seq? line) (any= '$C line)))

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


(defn normalize-let-shape
  "Ensure that all let lines have same form.  Must run before infix->prefix"
  [program]
  (letfn 
    [(normalize-line [l]
       (match [l]
         [([(t :guard seq?) & rest] :seq)] l
         [([t (m :guard meta?) & rest] :seq)] `((~t ~m) ~@rest)
         [([t & rest] :seq)] `((~t) ~@rest)))]
    (match [program]
      [(x :guard atom?)] x
      [(['let & lines] :seq)] 
        (cons 'let (map normalize-line lines))
      :else (map normalize-let-shape program))))


;; --------  Default Body --------
(defn default-let-body
  "Ensure that let's have a body.  Generate one if not.  
  Let's must have vars gathered in list format, operators must all be prefix"
  [program]
  (letfn
    [(meta-map [bindings lastvar acc]
       (cond
         (empty? bindings) acc
         (meta? (first bindings)) (meta-map (rest bindings) nil (assoc acc lastvar (first bindings)))
         :else (meta-map (rest bindings) (first bindings) acc)))

     (blend [vars metas] (remove nil? (interleave vars (map metas vars))))

     (make-body [bindings]
       (let [names (distinct (remove meta? bindings))
             metas (meta-map bindings nil {})
             typed (blend names metas)]
        `((~'$ptuple (~'quote ~typed) ~@typed))))

     (ensure-body
       ([lines] (ensure-body lines '()))
       ([lines allVars]
        (match [lines]
          [([(['$C vars ops] :seq)] :seq)]
            (cons `(~'$C ~vars ~ops) (make-body (concat allVars vars)))
          [([(['$C vars ops] :seq) & rest] :seq)]
            (cons `(~'$C ~vars ~ops) (ensure-body rest (concat allVars vars)))
          [(body :guard seq?)] body
          :else (make-body allVars))))]

    (match [program]
      [(x :guard atom?)] x
      [(['let & parts] :seq)]
        `(~'let ~@(map default-let-body (ensure-body parts)))
      :else (map default-let-body program))))



