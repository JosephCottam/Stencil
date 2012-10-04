(in-ns 'stencil.transform)

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

(defn gatherBindings
  [lines]
  (match [lines]
    [([(['$op-colon names body] :seq) & rest] :seq)]  (concat names (gatherBindings rest))
    :else '()))

(defn makeBody
  [lines]
    (let [names (distinct (gatherBindings lines))]
      `((~'$ptuple (~'quote ~names) ~@names))))


(defn ensureBody
  [lines]
  (match [(last lines)]
    [(['$op-colon vars ops] :seq)] (concat lines (makeBody lines))
    :else lines))


(defn defaultLetBody
  "Ensure that let's have a body.  Generate one if not.  
  Let's must have vars gathered in list format, operators must all be prefix"
  [program]
  (match [program]
    [(x :guard atom?)] x
    [(['let & parts] :seq)] `(~'let ~@(map defaultLetBody (ensureBody parts)))
    :else (map defaultLetBody program)))
