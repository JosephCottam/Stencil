(ns stencil.transforms
  "Tree transformation functions"
  (:use [clojure.core.match :only (match)]))

(defn atom? [x] 
  (or (symbol? x) (number? x) (string? x)))

(load "transforms/dropComments")
(load "transforms/nestbind")
