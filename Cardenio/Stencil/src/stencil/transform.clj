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

(defn spp [program] 
  "A pretty-printer for stencil."
  (clean-metas (clojure.pprint/pprint program)))

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
    ensure-fields validate-fields display->fields defaults->fields
    binding-when))

(defn prep-emit
  "tree -> tree: Lowers abstractions convenient during analysis, before emitters are called." 
  [program]
    (-> program drop-comments ))

(defn imports
  "tree -> modules: process the imports from the program"
  [program]
  {})

