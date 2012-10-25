(ns Stencil.test.compile
  (:require [stencil.compile :as c])
  (:use [clojure.test]))


(deftest meta->map
  (is (= (c/meta->map '($meta)) {}))
  (is (= (c/meta->map '($meta ((a b) (c d)))) {'a 'b, 'c 'd})))

(deftest meta->map
  (is (= (c/map->meta {'a 'b, 'c 'd}) '($meta ((a b) (c d)))))
  (is (= (c/map->meta {}) '($meta))))

(deftest meta-vals
  (is (= (c/meta-vals '($meta ((a b) (c d) (e f)))) '(b d f)))
  (is (= (c/meta-vals '($meta)) '())))

(deftest meta-keys 
  (is (= (c/meta-keys '($meta ((a b) (c d) (e f)))) #{'a 'c 'e}))
  (is (= (c/meta-keys '($meta)) #{})))
