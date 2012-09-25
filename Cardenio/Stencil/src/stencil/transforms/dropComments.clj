(in-ns 'stencil.transforms)

(defn- dropAll [program]
  (match [program]
    [(a :guard atom?)] a
    [(['comment & _] :seq)] '(::do-drop)
    :else (map dropAll program)))

(defn dropComments 
  "Remove all Comment nodes"
  [program]
  (remove #(or (= % ::do-drop) (= % '(::do-drop))) (dropAll program)))
