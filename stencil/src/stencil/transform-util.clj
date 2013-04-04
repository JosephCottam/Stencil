(ns stencil.transform)
;"Utilities for tree->tree transformations"

(defn filter-tagged
  "[test,] condition, list* -> list* Filter a list list-of-lists whose first item passes the test/condition pair 
  Test is invoked once for each policy in policy*.  Default test is '='"
  ([condition policies] (filter-tagged = condition policies))
  ([test condition policies] (filter #(and (seq? %) (test (first %) condition)) policies)))

(defn bind? [a] (= '$$ a))
(defn has-bind? [expr] 
  "Does this expression include a binding statement (syntactically denoted with an infix-colon)?"
  (and (seq? expr) (some bind? expr)))

(defn lop->map [lop]
  "Converts a ((key val) ..) into a clojure dictionary"
  (zipmap (map first lop) (map second lop)))

(defn map->lop [m]
  "Converts a clojure dictionary to ((key value) ...) form"
  (if (= 0 (count m))
    '()
    (map list (keys m) (vals m))))

(defn dissoc-lop [lop k]
  "Dissociate an item from a lop"
  (map->lop (dissoc (lop->map lop) k)))

(defn dissoc-tlop [lop k]
  (cons (first lop) (dissoc-lop (rest lop) k)))

(defn value?
  "Items that are their own values."
  [x]
  (or (= x true) (= x false) (number? x) (string? x)))

(defn atom? 
  "Items that are no longer divisible, includes forms"
  [x]
  (or (symbol? x) (value? x) (class? x)))

(defn meta-keys [m] (set (map first (rest m))))
(defn meta-vals [m] (map second (rest m)))
(defn map->meta [m] (cons '$meta (map->lop m)))
(defn meta->map [m]
  "Makes a map out of the pairs in a meta.  Bare values are dropped."
  (lop->map (filter seq? m))) 

(defn default-for-type [type]
  (case type
    (int long float double number) 0
    string ""
    nil nil
    (throw (RuntimeException. (str "Default not known for type " type)))))

(defn full-nth [expr n] (nth (remove meta? expr) n))

(defn full-drop 
  "Eliminate the first n things and (possibly) accompanying meta from a stencil expression."
  ([expr] (full-drop 1 expr))
  ([n expr]
   (cond
     (or (zero? n) (empty? expr)) expr
     (meta? (second expr)) (full-drop (- n 1) (drop 2 expr))
     :else (full-drop (- n 1) (drop 1 expr)))))

(defn second-expr 
  "Removes the first element and its meta-data"  
  [expr] (first (full-drop expr)))

(defn name-of [item]
  "Return the name of the current item." ;;TODO: Verifies that the item can and does actually be named.  (like contexts and policies can be, but expr's generally can't)
  (first (full-drop item)))

(defn split-preamble [program] 
  "Divide a program into the preable and body parts. Preamble ends after the imports.
   program -> (preamble, body)"
  (split-with 
    #(or (not (seq? %)) (= 'import (first %)) (meta? %))
    program))

(defn default-value [item fields]
  "Given a fields statement, return a default value for a specific field.
  TODO: Do type-based defaulting? (Or should having a meta.default entry be part of normalizing the fields statement?)"
  (let [fields (full-drop fields)
        metas (zipmap (take-nth 2 fields) (take-nth 2 (rest fields)))
        meta (meta->map (metas item))
        default (meta 'default)]
    (cond
      (empty? meta) (throw (RuntimeException. (str "Could not find entry for '" item "'")))
      (nil? default) (throw (RuntimeException. (str "No default provided for '" item "'")))
      :else default)))


(defn parseException
  "Produce an exception object (with location information when available)."
  ([msg] (RuntimeException. msg))
  ([expr msg] (parseException expr msg nil))
  ([expr msg ex]  
   (let [meta (meta->map (first (filter meta? expr)))
         location (if (or (nil? meta) (not (contains? meta '.line)))
                    "unknown location" 
                    (get meta '.line))]
     (RuntimeException. (str msg " (at line " location ")") ex))))

  


