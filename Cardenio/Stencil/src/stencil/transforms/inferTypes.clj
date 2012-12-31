(in-ns 'stencil.transform)


(defn UNKNOWN  [] '***UNKNOWN***)

(defn simple-type [a]
            (if (symbol? a) 
              (UNKNOWN)
              (type a)))
          (defn ensure-type [meta type]
            "Add the given type, if it is not already present"
            (let [meta (meta->map meta)]
              (map->meta 
                  (if (nil? (meta 'type))
                    (assoc meta 'type type)
                    meta))))
          (defn enforce-type [meta required]
            "Set the type to the passed one.  Throw an error if there is another type present."
            (let [meta (meta->map meta)
                  present (meta 'type)]
              (map->meta 
                (cond
                  (= present required) meta
                  (nil? present) (assoc meta 'type required)
                  :else (throw (RuntimeException. (str "Found type '" present "' when '" required "' was required.")))))))
          (defn context? [l] (or (= l 'stencil) (= l 'stream) (= l 'table) (= l 'import)))
          (defn policy? [p] (or (= p 'fields) (= p 'data) (= p 'render)))


(defn infer [in-args program]
  (match [in-args program]
    [_ (a :guard atom?)] a
    [_ ([(context :guard context?) ([(name :guard symbol?) (m :guard meta?)] :seq) & rest] :seq)]  
       `(~context ~name ~(enforce-type m context) ~@(map (partial infer false) rest))
    [_ ([([(policy :guard policy?) (m :guard meta?)] :seq) & rest] :seq)]
       `(~policy ~(enforce-type m policy) ~@(map (partial infer false) rest))
    [_ (['let bindings body] :seq)] 
       (do (println "LET!!!")
         `(~'let ~(map (partial infer false) bindings) ~(infer false body)))
    [false ([([op (m :guard meta?)] :seq) & args] :seq)]
       `(~op ~(enforce-type m 'fn) ~@(map (partial infer true) args))
    [true ([(a :guard atom?) (m :guard meta?)] :seq)]
       (list a (ensure-type m (simple-type a)))
    :else (map (partial infer false) program)))

(defn infer-types [program]
  "A very simple type annotater (not really an inferencer) 
   Types are string right now...unless otherwise state or false from immediate context (non-string primitive, function calls, etc)."
    (->> program tie-metas (infer false) untie-metas))
