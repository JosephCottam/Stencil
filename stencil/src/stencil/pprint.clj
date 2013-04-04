(ns stencil.pprint
  (:use stencil.util)
  (:require clojure.pprint))

(defmulti pprint-stencil class)
(defmethod pprint-stencil :default [thing] (clojure.pprint/code-dispatch thing))
(defmethod pprint-stencil clojure.lang.ISeq [astencil]
  (clojure.pprint/pprint-logical-block :prefix "(" :suffix ")"
    (loop [astencil (seq astencil)]
      (when astencil
        (clojure.pprint/write-out (first astencil))
        (if (not (nil? (second astencil)))
          (let [nxt (second astencil)]
            (.write ^java.io.Writer *out* " ")
            (if (meta? nxt)   ;;Reduces line-breaks after metas over standard pretty-printer
              (clojure.pprint/pprint-newline :fill)
              (clojure.pprint/pprint-newline :linear))
            (recur (next astencil))))))))

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

