(in-ns 'stencil.transform)

(defn pull->When
  "Convert pull statements to when statements"
  [program]
  (match [program]
    [(a :guard atom?)] a
    [(['pull from conseq] :seq)] 
         (list 'when (list 'onChange from) (list 'items from) conseq)
    :else (map pullToWhen program)))



