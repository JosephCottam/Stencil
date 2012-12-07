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
  "Ensure that all let lines have same form.  Removes the binding operator."
  [program]
  (letfn 
    [(normalize-line [l]
       (match [l]
         [([(t :guard seq?) & rest] :seq)] l   ;;Body or multi-var binding
         [([t (m :guard meta?) & rest] :seq)] `((~t ~m) ~@rest)  ;;Single variable binding w/meta
         [([t & rest] :seq)] `((~t) ~@rest)))   ;;Single variable binding w/o meta
     (divide-body [lines]
       (if (nil? (some '$C lines))
         (list lines '())
         (list (butlast lines) (last lines))))
     (reshape-binding [binding]
       (let [[vars op expr & meta] binding]
         (if (empty? meta)
           `(~vars ~expr)
           `(~vars (~'$do ~expr ~@meta)))))]
    (match [program]
      [(x :guard atom?)] x
      [(['let & lines] :seq)] 
        (let [[bindings body] (divide-body (map normalize-line lines))
              bindings (map reshape-binding bindings)]
          (list 'let bindings body))
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
        `(~'$ptuple (~'quote ~typed) ~@typed)))

     (ensure-body
       ([bindings body]
        (if (empty? body)
          (make-body (reduce concat (map first bindings)))
          body)))]

    (match [program]
      [(x :guard atom?)] x
      [(['let bindings body] :seq)]
        `(~'let ~(map default-let-body bindings) ~(default-let-body (ensure-body bindings body)))
      :else (map default-let-body program))))



