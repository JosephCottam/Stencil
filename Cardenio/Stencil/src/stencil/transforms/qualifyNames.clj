(in-ns 'stencil.transform)

(defn- nameContext? [n] 
  (some #(= n %) '(stencil plot table operator stream)))

(defn- qualify
  [prefix program]
  (match [program]
    [(s :guard symbol?)] (symbol (clojure.string/join "." (concat prefix s)))
    [(a :guard atom?)] a
    [([nc n & to] :seq :guard (nameContext? nc))] 
         (map (partial qualify (concat prefix n)) to)
    :else (map (partial qualify prefix) program)))

(defn qualifyNames
  "Replace names with fully qualified names"
  [program]
  (qualify '() program))
