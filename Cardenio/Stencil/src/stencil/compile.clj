(ns stencil.compile
  "Utilities for compiling.")

(defn lop->map [lop]
  "Converts a ((key val) ..) into a clojure dictionary"
  (zipmap (map first lop) (map second lop)))

(defn map->lop [m]
  "converts a clojure dictionary to ((key value) ...) form"
  (if (= 0 (count m))
    '()
    (map list (keys m) (vals m))))

(defn any=
  "item, coll->bool: Is the item in the collection?"
  [item coll]
  (some (partial = item) coll))

(defn value?
  "Items that are their own values."
  [x]
  (or (number? x) (string? x)))

(defn st-keyword?
  "Keywords that are NOT expressions"
  [x] 
  (any= x '(facet import operator stencil table stream)))

(defn atom? 
  "Items that are no longer divisible, but not keywords."
  [x]
  (and (or (symbol? x) (value? x)) (not (st-keyword? x))))



(defn meta? [e]
  "Is this a meta expression?"
   (and (list? e) (= '$meta (first e))))

(defn emptyMeta? [e]
  "Is this a meta-expression with no data?"
  (and (meta? e) (= 1 (count e))))

(defn meta->map [m] (lop->map (second m)))
(defn map->meta [m]
  (let [lop (map->lop m)]
    (if (= 0 (count lop))
      '($meta)
      `(~'$meta ~lop))))


(defn meta-keys [m] (set (map first (second m))))
(defn meta-vals [m] (map second (second m)))


(defn default-for-type [type]
  (case type
    (int long float double number) 0
    string ""
    (throw (RuntimeException. (str "Default not known for type " type)))))


