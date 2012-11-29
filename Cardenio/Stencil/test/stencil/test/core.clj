(ns Stencil.test.core
  (:require [stencil.core :as c])
  (:use [clojure.test]))

(deftest stencil-parse
  (is (= (c/parse "(a)") '(a)))
  (is (= (c/parse "(stencil Test)") '(stencil Test)))
  (is (= (c/parse "#(a,b,c)") '($tuple a b c)))
  (is (= (c/parse "#(a b c)") '($tuple a b c)))
  (is (= (c/parse "##(a b c)") '($ptuple a b c)))
  (is (= (c/parse "##())") '($ptuple)))
  (is (= (c/parse "(a : {int})") '(a ($meta int))))
  (is (= (c/parse "[a]") '(tuple-ref a)))
  (is (= (c/parse "(a : b)") '(a $C b)))
  (is (= (c/parse "; some") '(comment " some")))
  (is (= (c/parse ";* some then more*;")) '(comment " some then more"))
  (is (= (c/parse "(;* some*; then more)")) '((comment " some") then more)
  ))

(def root "../tests/data/")
(deftest stencil-read
  (is (list? (c/read (str root "geometry/simpleLines.stencil"))))
  (is (list? (c/read (str root "geometry/simpleLines.tstencil")))))


