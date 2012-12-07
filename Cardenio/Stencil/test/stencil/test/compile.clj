(ns stencil.test.compile
  (:require [stencil.core :as core])
  (:require [stencil.compile :as c])
  (:require [stencil.transform :as t])
  (:require [clojure.walk])
  (:use [clojure.test]))
  
(def root "../tests/data/")

(defn clean [p] (t/drop-comments p))

(defn same-structure? [p1 p2]
  (cond
    (and (empty? p1) (empty? p2)) true
    (or (empty? p1) (empty? p2)) false
    :else
      (and (= (count p1) (count p2))
           (every? true? (map same-structure? p1 p2)))))

(deftest compile-stencil-structure
  (is (same-structure?
        (clean (c/compile (core/read (str root "geometry/simplelines.stencil"))))
        (clean (c/compile (core/read (str root "geometry/simplelines.tstencil")))))))

(deftest compile-stencil
  (is (= (clean (c/compile (core/read (str root "geometry/simplelines.stencil"))))
         (clean (c/compile (core/read (str root "geometry/simplelines.tstencil")))))))
