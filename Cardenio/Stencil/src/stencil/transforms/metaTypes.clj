(in-ns 'stencil.transform)

(defn- hasType? [metas]
  (cond
    (null? metas) false
    (and (list? (first metas)) (= 'type (first (first metas)))) true
    :else (hasType? (rest metas))))

(defn- justTypes [metas]
  (if (hasType? metas)
       metas
       (match [program]
         [(['value val :guard symbol?] :seq)] (list 'type val)



  (match [program]

(defn metaTypes
  "Identify types in meta statements."
  [program]
  (match [program]
    [(['meta & rest] :seq) (list 'meta (map justTypes rest))
    :else (map metaTypes program)))
