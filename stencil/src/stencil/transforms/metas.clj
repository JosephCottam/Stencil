(ns stencil.transform
  "Manipulate metas *in the AST*.  
   Functions for working with metas outside the AST (e.g., as a dictionary) are found elsewhere.")


(defn- stencil-form?
  "Forms are NOT expressions...but they often contain them."
  [x] 
  (any= x '(facet import operator stencil table stream)))
(defn- atom-not-form? [a] (and (atom? a) (not (stencil-form? a))))
(defn- not-meta? [x] (not (meta? x)))

(defn ensure-metas [program] 
  "Ensure that there is a meta expression after every atom."
   (match program
     (e :guard empty?) nil
     ([(a :guard atom?) (m :guard meta?) & rest] :seq)
         `(~a ~m ~@(ensure-metas rest))
     ([(a :guard atom?) & rest] :seq)
         `(~a (~'$meta) ~@(ensure-metas rest))
     ([h & rest] :seq)
        `(~(ensure-metas h) ~@(ensure-metas rest))))

(defn location-in-metas [program]
  "Put line/column information from parsing into Stencil metas (pulled from Clojure metas)"
  (letfn [(extend-meta [m a]
            (if-let [am (meta a)]
              (map->meta (assoc (meta->map m) (symbol ".line") (am :line) (symbol ".col") (am :column)))
              m))]
    (match program
      (a :guard atom?) (throw (RuntimeException. "Bare atom found after metas in all locations expected."))
      (e :guard empty?) nil
      ([(a :guard atom?) (m :guard meta?) & rest] :seq)
         `(~a ~(extend-meta m a) ~@(location-in-metas rest))
      ([e & rest] :seq)
         `(~(location-in-metas e) ~@(location-in-metas rest)))))
  

(defn meta-pairings [program]
  (letfn [(maybe-pair [ls]
            (cond
              (or (empty? ls) (< (count ls) 3)) ls
              (= '$$ (second ls)) 
                 (cons (list (first ls) (nth ls 2)) (maybe-pair (drop 3 ls)))
              :else (cons (first ls) (maybe-pair (rest ls)))))]
    (match program
      (a :guard atom?) a
      (['$meta & rest] :seq) (cons '$meta (maybe-pair rest))
      :else (map meta-pairings program))))


(defn meta-types
  "Identify data types in meta statements.
  TODO: REMOVE THIS AND REPLACE WITH A MORE COMPLETE METADATA LABELING MECHANISM."
  [program]
  (letfn 
    [(hasType? [metas] (and (seq? metas) (contains? (meta->map metas) 'type)))
     (addType [metas] (let [[before after] (split-with seq? (rest metas))
                            [type & tail] after]
                        (if (nil? type) 
                          metas
                          (cons '$meta (concat before (cons (list 'type type) tail))))))]
    (match program
      (a :guard [meta? hasType?]) a
      (a :guard meta?) (addType a)
      (a :guard atom?) a
      :else (map meta-types program))))




(defn tie-metas [program]
  "Put a meta and its associated item in to a list together.  
   This is a utility for working with metas when the item is also required."
  (match program
    (a :guard atom?) a
    (b :guard empty?) nil 
    ([(a :guard atom?) (m :guard meta?) & rest] :seq)
       `((~a ~m) ~@(tie-metas rest))
    ([e & rest] :seq)
      `(~(tie-metas e) ~@(tie-metas rest))))


(defn untie-metas [program]
  "Undoes tie-metas."
  (match program
    (a :guard atom?) a
    (n :guard empty?) n
    ([([(a :guard atom?) (m :guard meta?)] :seq) & rest] :seq)
      `(~a ~m ~@(untie-metas rest))
    ([e & rest] :seq)
      `(~(untie-metas e) ~@(untie-metas rest))))

