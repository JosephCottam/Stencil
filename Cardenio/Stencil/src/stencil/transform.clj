(ns stencil.transform
  "Tree transformation functions"
  (:use [clojure.core.match :only (match)])
  (:use stencil.compile)
  (:require clojure.pprint))

;(load "transforms/dropComments")
(load "transforms/normalizeLet")
(load "transforms/infixToPrefix")
(load "transforms/convertToWhen")
(load "transforms/metas")
(load "transforms/imports")
(load "transforms/bindingWhen")
(load "transforms/fields")

(defn spp [program] 
  "A pretty-printer for stencil."
  (cleanMetas (clojure.pprint/pprint program)))

(defn validateParse
  "tree->tree/error : Verifies that a parsed tree is correctly formed after parsing.  
   This validation is run before normalization, simplifying normalization by removing many checks."
 [program] (-> program validateLetShape))

(defn normalize 
  "tree -> tree: Transforms a parse-form tree to normal-form tree"
  [program] 
   (-> program 
    ensureRuntimeImport
    normalizeLetShape infix->prefix defaultLetBody 
    file->init pull->when init->when
    supplyMetas metaTypes
    ensureFields validateFields display->fields defaults->fields))

(defn imports
  "tree -> modules: process the imports from the program"
  [program]
  {})

