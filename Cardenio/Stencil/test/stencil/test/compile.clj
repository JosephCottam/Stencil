(ns stencil.test.compile
  (:require [stencil.core :as core])
  (:require [stencil.compile :as c])
  (:require [clojure.walk])
  (:use [clojure.test]))
  
(def root "../tests/data/")

(deftest compile-stencil
  (is (= (c/compile (core/read (str root "geometry/simplelines.stencil")))
         (c/compile (core/read (str root "geometry/simplelines.tstencil"))))))
