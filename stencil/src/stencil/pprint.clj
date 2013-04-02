(ns stencil.pprint
  (:require [stencil.transform :as t])
  (:require clojure.pprint))

(defmulti pprint-stencil class)
(defmethod pprint-stencil :default [thing] (clojure.pprint/code-dispatch thing))
(defmethod pprint-stencil clojure.lang.ISeq [astencil]
  (clojure.pprint/pprint-logical-block :prefix "(" :suffix ")"
    (loop [astencil (seq astencil)]
      (when astencil
        (clojure.pprint/write-out (first astencil))
        (when-let [nxt (second astencil)]
          (.write ^java.io.Writer *out* " ")
          (if (t/meta? nxt)   ;;Reduces line-breaks after metas over standard pretty-printer
            (clojure.pprint/pprint-newline :fill)
            (clojure.pprint/pprint-newline :linear))
          (recur (next astencil)))))))

(defn spp [program & opts] 
  "A pretty-printer for stencil.
   Options-- 
     :nometas - Remove empty meta statements
     :return - Return the original program"
  (let [preproc identity
        preproc (if (t/any= :nometas opts) (comp t/clean-metas identity) preproc)]
    (clojure.pprint/with-pprint-dispatch 
      pprint-stencil 
      (clojure.pprint/pprint (preproc program)))
    (if (t/any= :return opts) program)))

(defn dspp [program & opts] (apply spp program (cons :return opts)))
