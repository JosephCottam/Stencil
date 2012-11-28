(ns Stencil.test.core
  (:require [stencil.core :as c])
  (:use [clojure.test]))

(deftest parse-stencil
  (is (= (c/parse-stencil "(a)") '(a)))
  (is (= (c/parse-stencil "(stencil Test)") '(stencil Test)))
  (is (= (c/parse-stencil "#(a,b,c)") '($tuple a b c)))
  (is (= (c/parse-stencil "#(a b c)") '($tuple a b c)))
  (is (= (c/parse-stencil "##(a b c)") '($ptuple a b c)))
  (is (= (c/parse-stencil "##())") '($ptuple)))
  (is (= (c/parse-stencil "(a : {int})") '(a ($meta int))))
  (is (= (c/parse-stencil "[a]") '(tuple-ref a)))
  (is (= (c/parse-stencil "(a : b)") '(a $C b)))
  (is (= (c/parse-stencil "; some") '(comment " some")))
  (is (= (c/parse-stencil ";* some then more*;")) '(comment " some then more"))
  (is (= (c/parse-stencil "(;* some*; then more)")) '((comment " some") then more)
  ))
