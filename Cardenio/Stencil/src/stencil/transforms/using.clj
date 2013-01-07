(in-ns 'stencil.transform)

(defn arrow->using
  "The arrow operator is the binary, infix version of using."
  [program]
  (match [program]
    [(a :guard atom?)] a
    [(['-> e1 e2] :seq)] (list 'using  e1 e2)
    :else (map arrow->using program)))
    

  


