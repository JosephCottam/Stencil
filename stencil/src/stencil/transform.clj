(ns stencil.transform
  "Tree transformation functions"
  (:require [clojure.core.match :refer (match)]))

(load "transform-util")
(load "transforms/tuples")
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
    tuple->ptuple normalize-let-shape 
    infix->prefix arrow->using default-let-body
    file->init pull->when init->when
    meta-pairings supply-metas meta-types
    ensure-fields display->fields defaults->fields normalize-fields check-fields-cover-data
    align-ptuple
    normalize-renders gather-renders ensure-view
    split-when infer-types ensure-using-tuple))

(defn prep-emit
  "tree -> tree: Lowers abstractions convenient during analysis, before emitters are called." 
  [program]
    (-> program drop-comments ))

(defn imports
  "tree -> modules: process the imports from the program"
  [program]
  {})



