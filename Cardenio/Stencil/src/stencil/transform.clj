(ns stencil.transform
  "Tree transformation functions"
  (:use [clojure.core.match :only (match)]))

(defn atom? [x] 
  (or (symbol? x) (number? x) (string? x)))

(defn namingContext? [n] 
  (some #(= n %) '(stencil view table operator stream fields)))

(defn tag? [x]
  (some #(= x %) '($value)))

(load "transforms/dropComments")
(load "transforms/nestbind")
(load "transforms/pulltowhen")
(load "transforms/tagElements")



(defn normalize 
  "tree -> tree: Transforms a parse-form tree to normal-form tree"
  [program] (-> program nestBind pullToWhen tagElements))

