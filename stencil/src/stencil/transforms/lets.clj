(ns stencil.transform)

;; ------- Validate Let-----

(defn- validate-line-shape [lines]
  (match lines
    ([line] :seq) (list line) ;Last line may be body or binding... 
    ([(line :guard has-bind?) & rest] :seq) 
         (cons line (validate-line-shape rest))
    ([(line :guard (complement has-bind?)) & rest] :seq) 
         (throw (RuntimeException. (str "Invalidate let shape: " line)))))

(defn validate-let-shape [program]
  "Let must have a binding on each line, except the last which may be a body instead"
  (match program
    (x :guard atom?) x
    (['let & letLines] :seq)
        `(~'let ~@(map validate-let-shape (validate-line-shape letLines)))
    :else (map validate-let-shape program)))
      

;; ------- Normalize shape ----


(defn normalize-let-shape [program]
  "Ensure that all let lines have same form.  Removes the binding operator."
  (letfn 
    [(normalize-line [l]
       (match l
         (a :guard atom?) a
         ([(t :guard seq?) & rest] :seq) l   ;;Multi-var binding
         ([t (m :guard meta?) & rest] :seq) `((~t ~m) ~@rest)  ;;Single variable binding w/meta
         ([t & rest] :seq) `((~t) ~@rest)))   ;;Single variable binding w/o meta
     (divide-body [lines]
       "Give back a pair (bindings, body)"
       (let [maybe-body (last lines)]
         (if (or (atom? maybe-body) (not (any= '$$ maybe-body)))
           (list (butlast lines) maybe-body)
           (list lines '()))))
     (reshape-binding [binding]
       "Provide relevant do-statement so even values look like function applications in the bindings"
       (let [[vars op expr & meta] binding]
         (cond
           (not (empty? meta)) `(~vars (~'do ~expr ~@meta))
           (atom? expr) `(~vars (~'do ~expr))
           :else `(~vars ~expr))))]
    (match program
      (x :guard atom?) x
      (['let & lines] :seq) 
        (let [[bindings body] (divide-body lines)
              bindings (map (comp reshape-binding normalize-line) bindings)
              body (if (atom? body) (list 'do body) body)]
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
        `(~'ptuple (~'fields ~@typed) ~@typed)))

     (ensure-body
       ([bindings body]
        (if (empty? body)
          (make-body (reduce concat (map first bindings)))
          body)))]

    (match program
      (x :guard atom?) x
      (['let bindings body] :seq)
        `(~'let ~(map default-let-body bindings) ~(default-let-body (ensure-body bindings body)))
      :else (map default-let-body program))))



