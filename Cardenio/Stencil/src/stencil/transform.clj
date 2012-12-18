(ns stencil.transform
  "Tree transformation functions"
  (:use [clojure.core.match :only (match)])
  (:require clojure.pprint))

(load "transform-util")
(load "transforms/dropComments")
(load "transforms/normalizeLet")
(load "transforms/infixToPrefix")
(load "transforms/convertToWhen")
(load "transforms/metas")
(load "transforms/imports")
(load "transforms/bindingWhen")
(load "transforms/fields")
(load "transforms/inferTypes")


(defn validate
  "tree->tree/error : Verifies that a parsed tree is correctly formed after parsing.  
   This validation is run before normalization, simplifying normalization by removing many checks."
 [program] (-> program validate-let-shape))

(defn normalize 
  "tree -> tree: Transforms a parse-form tree to normal-form tree"
  [program] 
   (-> program 
    ensure-runtime-import
    normalize-let-shape infix->prefix default-let-body 
    file->init pull->when init->when
    supply-metas meta-types
    ensure-fields display->fields defaults->fields normalize-fields
    binding-when infer-types))

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
          (if (meta? nxt) 
            (clojure.pprint/pprint-newline :fill)
            (clojure.pprint/pprint-newline :linear))
          (recur (next astencil)))))))

(defn spp [program] 
  "A pretty-printer for stencil."
  (clojure.pprint/with-pprint-dispatch pprint-stencil (clojure.pprint/pprint (clean-metas program))))


