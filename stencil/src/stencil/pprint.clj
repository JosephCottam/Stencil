(ns stencil.pprint
  (:require clojure.pprint))


;;Meta processing tools ------------------------
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

(defn any=
  "item, coll->bool: Is the item in the collection?"
  [item coll]
  (not (nil? (some (partial = item) coll))))

(defmulti pprint-stencil class)
(defmethod pprint-stencil :default [thing] (clojure.pprint/code-dispatch thing))
(defmethod pprint-stencil clojure.lang.ISeq [astencil]
  (clojure.pprint/pprint-logical-block :prefix "(" :suffix ")"
    (loop [astencil (seq astencil)]
      (when astencil
        (clojure.pprint/write-out (first astencil))
        (when-let [nxt (second astencil)]
          (.write ^java.io.Writer *out* " ")
          (if (meta? nxt)   ;;Reduces line-breaks after metas over standard pretty-printer
            (clojure.pprint/pprint-newline :fill)
            (clojure.pprint/pprint-newline :linear))
          (recur (next astencil)))))))

(defn spp [program & opts] 
  "A pretty-printer for stencil.
   Options--
     :showhiddenmeta -- Show hidden values inside metas
     :cleanmetas - Remove hidden values and remove empty metas
     :nometas -- Remove all metas
     :return - Return the original program"
  (let [preproc identity
        preproc (if (any= :nometas opts) (comp remove-metas preproc) preproc)
        preproc (if (any= :showhiddenmeta opts) preproc (comp reduce-metas preproc))
        preproc (if (any= :cleanmetas opts) (comp clean-metas preproc) preproc)]
    (clojure.pprint/with-pprint-dispatch 
      pprint-stencil 
      (clojure.pprint/pprint (preproc program)))
    (if (any= :return opts) program)))

(defn dspp [program & opts] 
  "Utility for printing in -> context."
    (apply spp program (cons :return opts)))

