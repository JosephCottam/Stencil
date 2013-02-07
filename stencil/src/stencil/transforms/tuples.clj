(ns stencil.transform)

(defn tuple->ptuple [program]
  "A prototyped tuple is a tuple with named fields. 
   This pass converts tuples into prototyped tuples when possible."
  (match program
    (a :guard atom?) a
    (['tuple & (values :guard has-bind?)] :seq)
      (let [vars (take-nth 3 values)
            binds (take-nth 3 (drop 1 values))
            vals (take-nth 3 (drop 2 values))]
        (if (or (not-every? symbol? vars)
                (not-every? bind? binds)) (throw (RuntimeException. "Mal-formed prototyped-tuple")))
        `(~'ptuple (~'fields ~@vars) ~@vals))
    :else (map tuple->ptuple program)))

                
                



