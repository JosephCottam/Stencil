(ns Stencil.test.core
  (:use [stencil.core])
  (:use [clojure.test]))

(deftest parsing 
  (is (= (parseStencil "(a)") '(a)))
  (is (= (parseStencil "(stencil Test)") '(stencil Test)))
  (is (= (parseStencil "#(a,b,c)") '($tuple a b c)))
  (is (= (parseStencil "#(a b c)") '($tuple a b c)))
)
