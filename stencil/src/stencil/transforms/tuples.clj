(ns stencil.transform)

(defn- t->p [t] (if (= 'tuple t) 'ptuple 'ptuples))
(defn- tuple? [a] (any= a '(tuple tuples)))

(defn tuple->ptuple [program]
  "A prototyped tuple is a tuple with named fields. 
   This pass converts tuples into prototyped tuples when possible."
  (match program
    (a :guard atom?) a
    ([(tag :guard tuple?) & (values :guard has-bind?)] :seq)
      (let [vars (take-nth 3 values)
            binds (take-nth 3 (drop 1 values))
            vals (take-nth 3 (drop 2 values))]
        (if (or (not-every? symbol? vars)
                (not-every? bind? binds)) (throw (RuntimeException. "Mal-formed prototyped-tuple")))
        `(~(t->p tag) (~'fields ~@vars) ~@vals))
    :else (map tuple->ptuple program)))


(defn align-ptuple [program]
  "When ptuple(s) produces the return value of a policy, 
     (1) checks that the ptuple has all the fields of the context
     (2) and that they are in the same order."
  (letfn [(ptuple? [op] (any= op '(ptuple ptuples)))
          (fields? [e] (= 'fields (first e)))
          (empty-meta [item] '($meta (type ***)))
          (default-lookup [fields] 
            (fn [item] (default-value item fields)))
          (extract [target current defaultfn]
            "target is the target order; current is the current ptuple name/values as a dictionary"
            (letfn [(get [item] (let [v (current item)]
                                  (if (nil? v)
                                    (defaultfn item)
                                    v)))]
              (map get target)))
          (aligner [context]
            (fn matcher [program]
              (match program
                (a :guard atom?) a
                (['table name (m :guard meta?) & policies] :seq)
                  (let [fields (first (filter-tagged 'fields policies))]
                    `(~'table ~name ~m ~@((aligner fields) policies)))
                ([(op :guard ptuple?) (m :guard meta?) (fields :guard fields?) & args] :seq)
                  (if (nil? context)
                    `(~op ~m ~fields ~@args)
                    (let [cnames (full-drop (remove meta? context))
                          fm (second fields)
                          fields (full-drop fields)
                          names (remove meta? fields)
                          values (zipmap names (remove meta? args))
                          ametas (zipmap names (filter meta? args))
                          pmetas (zipmap names (filter meta? fields))
                          
                          args (extract cnames values (default-lookup context))
                          ametas (extract cnames ametas empty-meta)
                          pmetas (extract cnames pmetas empty-meta)               
                          
                          fields `(~'fields ~fm  ~@(interleave cnames pmetas))
                          args (interleave args ametas)]
                      `(~op ~m ~fields ~@args)))
                :else (map matcher program))))]
    ((aligner nil) program)))



