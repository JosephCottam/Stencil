(ns stencil.transform
  "Tree transformation functions"
  (:use [clojure.core.match :only (match)]))

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
(load "transforms/tagElements")



(defn normalize 
  "tree -> tree: Transforms a parse-form tree to normal-form tree"
  [program] (-> program normalizeLetShape infix->prefix defaultLetBody pull->when tag-elements))

