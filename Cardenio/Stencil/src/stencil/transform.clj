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

(defn namingContext? [n] 
  (some #(= n %) '(stencil view table operator stream fields)))

(defn tag? [x]
  (some #(= x %) '($value)))

(load "transforms/dropComments")
(load "transforms/nestInfix")
(load "transforms/pulltowhen")
(load "transforms/tagElements")



(defn normalize 
  "tree -> tree: Transforms a parse-form tree to normal-form tree"
  [program] (-> program nestInfix pull->When tagElements))

