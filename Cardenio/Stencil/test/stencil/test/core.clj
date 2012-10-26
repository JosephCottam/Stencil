(ns Stencil.test.core
  (:require [stencil.core :as c])
  (:use [clojure.test]))

(deftest parseStencil
  (is (= (c/parseStencil "(a)") '(a)))
  (is (= (c/parseStencil "(stencil Test)") '(stencil Test)))
  (is (= (c/parseStencil "#(a,b,c)") '($tuple a b c)))
  (is (= (c/parseStencil "#(a b c)") '($tuple a b c)))
  (is (= (c/parseStencil "##(a b c)") '($ptuple a b c)))
  (is (= (c/parseStencil "##())") '($ptuple)))
  (is (= (c/parseStencil "(a : [int])") '(a ($meta int))))
  (is (= (c/parseStencil "(a : b)") '(a $C b)))
  (is (= (c/parseStencil "; some") '(comment " some")))
  (is (= (c/parseStencil ";* some then more*;")) '(comment " some then more"))
  (is (= (c/parseStencil "(;* some*; then more)")) '((comment " some") then more)))
