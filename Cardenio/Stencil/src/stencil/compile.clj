(ns stencil.compile
  "Utilities for compiling.")

(defn value?
  "Items that are their own values."
  [x]
  (or (number? x) (string? x)))

(defn st-keyword?
  "Keywords that are NOT expressions"
  [x] 
  (some (partial = x) '(facet import operator stencil table stream)))

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

(defn meta->map [m]
  "Converts a ($meta ((key val) ..)) into a clojure dictionary"
  (zipmap (map first (second m)) (map second (second m))))

(defn map->meta [m]
  "converts a clojure dictionary to ($meta ((key value) ...)) form"
  (if (= 0 (count m))
    '($meta)
    `(~'$meta ~(map list (keys m) (vals m)))))


(defn meta-keys [m] (set (map first (second m))))
(defn meta-vals [m] (map second (second m)))
