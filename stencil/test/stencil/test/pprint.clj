(ns stencil.test.pprint
  (:require [stencil.pprint :as pp])
  (:use [clojure.test]))

(deftest clean-metas
  (is (= (pp/clean-metas 'a) 'a))
  (is (= (pp/clean-metas '(a ($meta))) '(a)))
  (is (= (pp/clean-metas '(a (b ($meta) c ($meta)))) '(a (b c))))
  (is (= (pp/clean-metas '(a ($meta stuff))) '(a ($meta stuff))))
  (is (= (pp/clean-metas '(a ($meta stuff) (b ($meta stuff) c ($meta) d ($meta stuff)))) 
         '(a ($meta stuff) (b ($meta stuff) c d ($meta stuff))))))

(deftest nometas
  (is (= (pp/remove-metas 'a) 'a))
  (is (= (pp/remove-metas '(a ($meta))) '(a)))
  (is (= (pp/remove-metas '(a (b ($meta) c ($meta)))) '(a (b c))))
  (is (= (pp/remove-metas '(a ($meta stuff))) '(a)))
  (is (= (pp/remove-metas '(a ($meta stuff) (b ($meta stuff) c ($meta) d ($meta stuff)))) 
         '(a (b c d)))))

(deftest nometas
  (is (= (pp/reduce-metas 'a) 'a))
  (is (= (pp/reduce-metas '(a (b ($meta) c ($meta)))) '(a (b ($meta) c ($meta)))))
  (is (= (pp/reduce-metas '(a ($meta (.some 1)))) '(a ($meta))))
  (is (= (pp/reduce-metas '(a ($meta stuff (.some other)))) '(a ($meta stuff))))
  (is (= (pp/reduce-metas '(a ($meta stuff) (b ($meta stuff (.some 1)) c ($meta (.other 1)) d ($meta stuff))))
         '(a ($meta stuff) (b ($meta stuff) c ($meta) d ($meta stuff))))))
