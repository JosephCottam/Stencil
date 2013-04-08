(ns stencil.transform)




(defn tuple->ptuple [program]
  "A prototyped tuple is a tuple with named fields. 
   This pass converts tuples into prototyped tuples.
   Names are either taken from bindings, var-names or positionally generated 
   (in that order of perference)"
  (letfn [(t->p [t] (if (= 'tuple t) 'ptuple 'ptuples))
          (tuple? [a] (any= a '(tuple tuples)))
          (reverse-interleave [n s] 
            "Converts a list into n-lists of every nth item each
            http://clojurecorner.blogspot.com/2012/11/reverse-interleave.html"
            (map
              (fn [i] (map first (partition-all n (drop i s))))
              (range 0 n)))
          (span [item ls]
            "Find the distance bewteen the first two occurances of item.
            If the item does not occur twice, returns the list length"
            (let [s (+ (.indexOf (drop (+ 1 (.indexOf ls item)) ls) item ) 1)
                  s (if (<= s 0) (count ls) s)]
              s))
          (gen-name [i item] (if (symbol? item) item (symbol (str "$" i))))]
    (match program
      (a :guard atom?) a
      ([(tag :guard tuple?) (m0 :guard meta?) & (values :guard has-bind?)] :seq)
        (let [arg-slice (span '$$ values)
              [vars varm binds bm vals valm] (reverse-interleave arg-slice values)
              ivals (if (empty? valm) vals (interleave vals valm))] ;;If vals are function applications, then there are no associated metas; thus conditional interleaving
          (if (or (not-every? symbol? vars)
                  (not-every? bind? binds)
                  (not (= (count values) 
                          (+ (count vars) (count varm) (count binds) (count bm) (count vals) (count valm)))))
            (throw (parseException program "Mal-formed prototyped-tuple"))
            `(~(t->p tag) ~m0 (~'fields (~'$meta) ~@(interleave vars varm)) ~@ivals)))
      ([(tag :guard tuple?) (m0 :guard meta?) & (values :guard (complement has-bind?))] :seq)
        (let [items (remove meta? values)
              metas (filter meta? values)
              names (map-indexed gen-name items)]
          `(~(t->p tag) ~m0 (~'fields (~'$meta) ~@(interleave names metas)) ~@values))
      :else (map tuple->ptuple program))))


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



