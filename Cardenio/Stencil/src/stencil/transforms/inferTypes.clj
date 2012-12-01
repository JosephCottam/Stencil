(in-ns 'stencil.transform)

(defn infer-types [program]
  "All types are int right now...unless specifically stated."
  (letfn [(ensure-type [meta type]
            (let [meta (meta->map meta)]
              (meta->map
               (if (or (not (contains? meta 'type))
                       (nil? (meta 'type)))
                   (assoc meta 'type type)
                   meta))))]
   (match [program]
    [(a :guard atom?)] a
    [(m :guard meta?)] (ensure-type m 'int)
    :else (map infer-types program))))
