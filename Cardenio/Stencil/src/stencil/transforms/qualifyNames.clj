(in-ns 'stencil.transform)

(defn namingContext? [n] 
  (contains? #{stencil view table operator stream fields} n))

(defn- qualify
  [prefix program]
  (match [program]
    [(s :guard symbol?)] (symbol (clojure.string/join "." (concat prefix s)))
    [(a :guard atom?)] a
    [([nc n & to] :seq :guard (namingContext? nc))] 
         (map (partial qualify (concat prefix n)) to)
    :else (map (partial qualify prefix) program)))

(defn qualifyNames
  "Replace names with fully qualified names"
  [program]
  (qualify '() program))
