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
     (e :guard empty?) e
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
              (map->meta (assoc (meta->map m) '.line (am :line) '.col (am :column)))
              m))]
    (match program
      (a :guard atom?) (throw (RuntimeException. "Bare atom found after metas in all locations expected."))
      (e :guard empty?) nil
      ([(a :guard atom?) (m :guard meta?) & rest] :seq)
         `(~a ~(extend-meta m a) ~@(location-in-metas rest))
      ([e & rest] :seq)
         `(~(location-in-metas e) ~@(location-in-metas rest)))))
  

(defn meta-pairings [program]
  "Ensure that every meta value has a label and is formatted as list-of pairs.
  TODO: This might run into problems if meta's contain un-labeled sequences...
    part of that depends on what exactly the bind operator means (if its 'just an infix paranethesis' then we hav ea problem)."
  (letfn [(maybe-pair [ls i]
            (cond
              (empty? ls) ls
              (seq? (first ls)) (cons (first ls) (maybe-pair (rest ls) i))
              (= '$$ (second ls)) 
                 (cons (list (first ls) (nth ls 2)) (maybe-pair (drop 3 ls) i))
              :else (cons (list (symbol (str "$p" i)) (first ls))
                          (maybe-pair (rest ls) (+ 1 i)))))]
    (match program
      (a :guard atom?) a
      (['$meta & rest] :seq) (cons '$meta (maybe-pair rest 0))
      :else (map meta-pairings program))))


(defn meta-types
  "Identify data types in meta statements.
  TODO: REMOVE THIS AND REPLACE WITH A MORE COMPLETE METADATA LABELING MECHANISM."
  [program]
  (letfn 
    [(hasType? [metas] (and (seq? metas) (contains? (meta->map metas) 'type)))
     (addType [metas]  
       "If there is a $p0 entry, use it as the type."
       (let [meta (meta->map metas)
             p0 ('$p0 meta)
             meta (if (nil? p0) meta (dissoc (assoc meta 'type p0) '$p0))]
         (map->meta meta)))]
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

