(in-ns 'stencil.transform)

(defn- atom-not-form? [a] (and (atom? a) (not (stencil-form? a))))

(defn supply-metas
  "Ensure that there is a meta expression after every atom."
  [program]
  (match [program]
    [(a :guard atom-not-form?)] a
    [(a :guard atom?)] (list a '($meta))
    
    [(a :guard empty?)] a
    
    [([(a :guard atom?) (b :guard meta?) & tail] :seq)] 
       `(~a ~b ~@(supply-metas tail))
    [([(a :guard atom-not-form?) & tail] :seq)] 
       `(~a (~'$meta) ~@(supply-metas tail))
    [([(a :guard atom?) & tail] :seq)] 
       `(~a ~@(supply-metas tail))
    [([a & tail] :seq)]
       (cons (supply-metas a) (supply-metas tail))))
  


(defn meta-types
  "Identify data types in meta statements.
  TODO: REMOVE THIS AND REPLACE WITH A MORE COMPLETE METADATA LABELING MECHANISM."
  [program]
  (letfn 
    [(hasType? [metas] (contains? (meta->map metas) 'type))
     (addType [metas] (let [[before after] (split-with seq? (rest metas))
                            [type & tail] after]
                        (if (nil? type) 
                          metas
                          (cons '$meta (concat before (cons (list 'type type) tail))))))]
    (match [program]
      [a :guard #(and (meta? %) (hasType? %))] a
      [a :guard meta?] (addType a)
      [a :guard atom?] a
      :else (map meta-types program))))


(defn clean-metas
  "Remove empty metas...mostly for pretty-printing"
  [program]
  (if (seq? program)
    (map clean-metas (remove empty-meta? program))
    program))


