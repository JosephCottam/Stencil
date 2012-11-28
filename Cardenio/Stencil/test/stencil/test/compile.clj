(ns Stencil.test.compile
  (:require [stencil.core :as core])
  (:require [stencil.compile :as c])
  (:use [clojure.test]))

(def root "../tests/data/")
(deftest compile-stencil
  (is (= (c/compile (core/read (str root "geometry/simpleLines.stencil")))
         (c/compile (core/read (str root "geometry/simpleLines.tstencil"))))))
