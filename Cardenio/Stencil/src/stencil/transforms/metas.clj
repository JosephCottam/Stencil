(in-ns 'stencil.transform)

(defn- atom-not-form? [a] (and (atom? a) (not (stencil-form? a))))

(defn supplyMetas
  "Ensure that there is a meta expression after every atom."
  [program]
  (match [program]
    [(a :guard atom-not-form?)] a
    [(a :guard atom?)] (list a '($meta))
    
    [(a :guard empty?)] a
    
    [([(a :guard atom?) (b :guard meta?) & tail] :seq)] 
       `(~a ~b ~@(supplyMetas tail))
    [([(a :guard atom-not-form?) & tail] :seq)] 
       `(~a (~'$meta) ~@(supplyMetas tail))
    [([(a :guard atom?) & tail] :seq)] 
       `(~a ~@(supplyMetas tail))
    [([a & tail] :seq)]
       (cons (supplyMetas a) (supplyMetas tail))))
  

(defn- hasType? [metas] (contains? (meta->map metas) 'type))

(defn- addType [metas]
  (let [[before after] (split-with list? (rest metas))
        [head & tail] after]
    (cons '$meta (concat before (cons (list 'type head) tail)))))

(defn metaTypes
  "Identify data types in meta statements.
  TODO: REMOVE THIS AND REPLACE WITH A MORE COMPLETE METADATA LABELING MECHANISM."
  [program]
  (match [program]
    [a :guard #(and (meta? %)  (hasType? %))] a
    [a :guard meta?] (addType a)
    :else (map metaTypes program)))


(defn cleanMetas
  "Remove empty metas...mostly for pretty-printing"
  [program]
  (if (list? program)
    (map cleanMetas (remove emptyMeta? program))
    program))


