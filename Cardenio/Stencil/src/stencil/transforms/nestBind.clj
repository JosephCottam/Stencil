(in-ns 'stencil.transforms)

(defn nestBind
  "Move stencil-bind operators to head-position."
  [program]
  (match [program]
    [(a :guard atom?)] a
    [([target '$op-colon & to] :seq)] 
         (list '$op-colon (nestBind target) (nestBind to))
    :else (map nestBind program)))



