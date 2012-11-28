(in-ns 'stencil.transform)
;"Utilities for tree->tree transformations"

(defn lop->map [lop]
  "Converts a ((key val) ..) into a clojure dictionary"
  (zipmap (map first lop) (map second lop)))

(defn map->lop [m]
  "Converts a clojure dictionary to ((key value) ...) form"
  (if (= 0 (count m))
    '()
    (map list (keys m) (vals m))))

(defn any=
  "item, coll->bool: Is the item in the collection?"
  [item coll]
  (not (nil? (some (partial = item) coll))))

(defn value?
  "Items that are their own values."
  [x]
  (or (number? x) (string? x)))

(defn stencil-form?
  "Forms are NOT expressions...but they often contain them."
  [x] 
  (any= x '(facet import operator stencil table stream let)))

(defn atom? 
  "Items that are no longer divisible, includes forms"
  [x]
  (or (symbol? x) (value? x)))

(defn meta? [e]
  "Is this a meta expression?"
   (and (list? e) (= '$meta (first e))))

(defn empty-meta? [e]
  "Is this a meta-expression with no data?"
  (and (meta? e) (= 1 (count e))))

(defn meta-keys [m] (set (map first (rest m))))
(defn meta-vals [m] (map second (rest m)))
(defn map->meta [m] (cons '$meta (map->lop m)))
(defn meta->map 
  "Makes a map out of the pairs in a meta.  Bare values are dropped."
  [m] (lop->map (filter list? m))) 

(defn default-for-type [type]
  (case type
    (int long float double number) 0
    string ""
    (throw (RuntimeException. (str "Default not known for type " type)))))


