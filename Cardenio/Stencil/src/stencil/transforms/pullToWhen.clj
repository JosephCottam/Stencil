(in-ns 'stencil.transform)

(defn pull->when
  "Convert pull statements to when statements"
  [program]
  (match [program]
    [(a :guard atom?)] a
    [(['pull from conseq] :seq)] 
         (list 'when (list 'onChange from) (list 'items from) conseq)
    :else (map pull->when program)))



