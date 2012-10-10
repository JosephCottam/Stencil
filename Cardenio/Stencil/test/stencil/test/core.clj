(ns Stencil.test.core
  (:use [stencil.core])
  (:use [clojure.test]))

(def ps parseStencil)

(deftest parsing 
  (is (= (ps "(a)") '(a)))
  (is (= (ps "(stencil Test)") '(stencil Test)))
  (is (= (ps "#(a,b,c)") '($tuple a b c)))
  (is (= (ps "#(a b c)") '($tuple a b c)))
  (is (= (ps "##(a b c)") '($ptuple a b c)))
  (is (= (ps "##())") '($ptuple)))
  (is (= (ps "(a : [int])") '(a (meta (int)))))
  (is (= (ps "(a : b)") '(a $C b)))
  (is (= (ps "; some") '(comment " some")))
  (is (= (ps ";* some then more*;")) '(comment " some then more"))
  (is (= (ps "(;* some*; then more)")) '((comment " some") then more))
)
