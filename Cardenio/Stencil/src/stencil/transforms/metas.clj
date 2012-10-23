(in-ns 'stencil.transform)

(defn meta? [e]
  "Is this a meta expression?"
   (and (list? e) (= '$meta (first e))))

(defn emptyMeta? [e]
  "Is this a meta-expression with no data?"
  (and (meta? e) (= 1 (count e))))



(defn cleanMetas
  "Remove empty metas...mostly for pretty-printing"
  [program]
  (if (list? program)
    (map cleanMetas (remove emptyMeta? program))
    program))

(defn supplyMetas
  "Ensure that there is a meta expression after every atom."
  [program]
  (match [program]
    ;[a :guard atom?] (list a '($meta))   ;; How do you associate metadata with just a value?  An expression that just returns the value would be nice: ($ex 3 ($meta)).  Can be safely added and removed.
    [a :guard empty?] a
    [([(a :guard atom?)] :seq)] (list a '($meta))
    [([(a :guard atom?) (b :guard meta?) & tail] :seq)] 
       `(~a ~b ~@(supplyMetas tail))
    [([(a :guard atom?) & tail] :seq)] 
       `(~a (~'$meta) ~@(supplyMetas tail))
    [([a & tail] :seq)]
       (cons (supplyMetas a) (supplyMetas tail))))
  

(defn- hasType? [metas]
  (some (partial = 'type) (map #(if (list? %) (first %) %) (second metas))))

(defn- addType [metas]
  (let [[before after] (split-with list? (second metas))
        [head & tail] after]
    (list '$meta (concat before (cons (list 'type head) tail)))))

(defn metaTypes
  "Identify data types in meta statements."
  [program]
  (match [program]
    [a :guard #(and (meta? %)  (hasType? %))] a
    [a :guard meta?] (addType a)
    :else (map metaTypes program)))
