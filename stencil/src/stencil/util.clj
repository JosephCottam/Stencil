(ns stencil.util
  "Utility functions used in many places of the compiler.")

(defn any=
  "item, coll->bool: Is the item in the collection?"
  [item coll]
  (not (nil? (some (partial = item) coll))))


;;Meta processing tools ------------------------
;; Held here because pprint uses them and so does transform,
;; and no-circular-dependencies means that to use pprint in transform (useful for debugging)
;; these tools need to live outside of both

(defn meta? [e]
  "Is this a meta expression?"
   (and (seq? e) (= '$meta (first e))))

(defn empty-meta? [e]
  "Is this a meta-expression with no data?"
  (and (meta? e) (= 1 (count e))))

(defn remove-metas [program]
  "Remove all metas"
  (if (seq? program)
    (map remove-metas (remove meta? program))
    program))

(defn hidden? [item]
  (and (seq? item)
       (let [key (first item)] (.startsWith (str key) "."))))

(defn reduce-metas [program]
  (cond
    (meta? program) (remove hidden? program)
    (seq? program) (map reduce-metas program)
    :else program))
 

(defn clean-metas
  "Remove clojure-parser metadata from metas.
   Remove empty metas...mostly for pretty-printing"
  [program]
  (if (seq? program)
    (map clean-metas (remove empty-meta? program))
    program))
;;---------------------------------------

