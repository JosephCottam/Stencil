(ns stencil.transform
  "Tree transformation functions"
  (:require [clojure.core.match :refer (match)])
  (:require clojure.pprint))

(load "transform-util")
(load "transforms/comments")
(load "transforms/lets")
(load "transforms/infixOperators")
(load "transforms/convertToWhen")
(load "transforms/metas")
(load "transforms/imports")
(load "transforms/whens")
(load "transforms/fields")
(load "transforms/using")
(load "transforms/render")
(load "transforms/inferTypes")
(load "transforms/views")

(defn validate
  "tree->tree/error : Verifies that a parsed tree is correctly formed after parsing.  
   This validation is run before normalization, simplifying normalization by removing many checks."
 [program] (-> program validate-let-shape check-simple-fields))

(defn normalize 
  "tree -> tree: Transforms a parse-form tree to normal-form tree"
  [program] 
   (-> program 
    ensure-runtime-import
    normalize-let-shape infix->prefix arrow->using default-let-body
    file->init pull->when init->when
    meta-pairings supply-metas meta-types
    ensure-fields display->fields defaults->fields normalize-fields check-fields-cover-data
    normalize-renders gather-renders
    split-when infer-types ensure-using-tuple))

(defn prep-emit
  "tree -> tree: Lowers abstractions convenient during analysis, before emitters are called." 
  [program]
    (-> program drop-comments ))

(defn imports
  "tree -> modules: process the imports from the program"
  [program]
  {})



;;;;---------------------------------------------------------------------------------------
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
  "A pretty-printer for stencil."
  (let [preproc identity
        preproc (if (any= :metas opts) (comp clean-metas identity) preproc)]
    (clojure.pprint/with-pprint-dispatch 
      pprint-stencil 
      (clojure.pprint/pprint (preproc program)))))


