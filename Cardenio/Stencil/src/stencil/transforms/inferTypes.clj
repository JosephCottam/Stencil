(in-ns 'stencil.transform)

(defn infer-types [program]
  "All types are int right now...unless specifically stated."
  (letfn [(ensure-type [meta type]
            "Add the given type, if it is not already present"
            (let [meta (meta->map meta)]
              (map->meta 
                  (if (nil? (meta 'type))
                    (assoc meta 'type type)
                    meta))))
          (enforce-type [meta required]
            "Set the type to the passed one.  Throw an error if there is another type present."
            (let [meta (meta->map meta)
                  present (meta 'type)]
              (map->meta 
                (cond
                  (= present required) meta
                  (nil? present) (assoc meta 'type required)
                  :else (throw (RuntimeException. (str "Found type '" present "' when '" required "' was required.")))))))
          (context? [l] (or (= l 'stencil) (= l 'stream) (= l 'table) (= l 'import)))
          (policy? [p] (or (= p 'fields) (= p 'data) (= p 'render)))
          (type-binding [[vars expr]]
            (let [names (take-nth 2 vars)
                  metas (take-nth 2 (rest vars))]
                (list (interleave names (map infer-types metas)) (infer-types expr))))]
   (match [program]
     [(a :guard atom?)] a
     [(m :guard meta?)] (ensure-type m 'string)
     [([(context :guard context?) (name :guard symbol?) (m :guard meta?) & rest] :seq)]  
        `(~context ~name ~(enforce-type m context) ~@(map infer-types rest))
     [([(policy :guard policy?) (m :guard meta?) & rest] :seq)]  
        `(~policy ~(enforce-type m policy) ~@(map infer-types rest))
     [(['let bindings body] :seq)] 
       `(~'let ~(map type-binding bindings) ~(infer-types body))
     [([op (opm :guard meta?) & args] :seq)]
        `(~op ~(enforce-type opm 'fn) ~@(map infer-types args))
     :else (map infer-types program))))
