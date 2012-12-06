(ns stencil.test.compile
  (:require [stencil.core :as core])
  (:require [stencil.compile :as c])
  (:require [stencil.transform :as t])
  (:require [clojure.walk])
  (:use [clojure.test]))
  
(def root "../tests/data/")

(defn clean [p] (t/drop-comments p))

(deftest compile-stencil
  (is (= (clean (c/compile (core/read (str root "geometry/simplelines.stencil"))))
         (clean (c/compile (core/read (str root "geometry/simplelines.tstencil")))))))
