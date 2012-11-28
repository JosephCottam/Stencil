(in-ns 'stencil.transform)

(defn- drop-all [program]
  (match [program]
    [(a :guard atom?)] a
    [(['comment & _] :seq)] '(::do-drop)
    :else (map drop-all program)))

(defn drop-comments 
  "Remove all Comment nodes"
  [program]
  (remove #(or (= % ::do-drop) (= % '(::do-drop))) (drop-all program)))
