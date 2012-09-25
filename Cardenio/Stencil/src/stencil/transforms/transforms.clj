(ns stencil.transforms
  "Utility methods supporting transforms.")

(defn atom? [x] 
  (or (symbol? x) (number? x) (string? x)))
