(ns stencil.transform
  "Tree transformation functions"
  (:use [clojure.core.match :only (match)])
  (:require clojure.pprint))

(defn value?
  "Items that are their own values."
  [x]
  (or (number? x) (string? x)))

(defn atom? 
  "Items that are no longer divisible."
  [x]
  (or (symbol? x) (value? x)))


;(load "transforms/dropComments")
(load "transforms/normalizeLet")
(load "transforms/infixToPrefix")
(load "transforms/pullTowhen")
(load "transforms/metas")

(defn spp [program] 
  "A pretty-printer for stencil."
  (cleanMetas (clojure.pprint/pprint program)))

(defn validateParse
  "tree->tree/error : Verifies that a parsed tree 'generally' correct after parsing.  
   This validation is run before normalization, simplifying normalization by removing many checks."
 [program] (-> program validateLetShape))

(defn normalize 
  "tree -> tree: Transforms a parse-form tree to normal-form tree"
  [program] (-> program normalizeLetShape infix->prefix defaultLetBody pull->when supplyMetas))

