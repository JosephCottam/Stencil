(ns stencil.test.core
  (:require [stencil.core :as c])
  (:require [stencil.transform :as t])
  (:require [clojure.walk])
  (:use [clojure.test]))

(deftest stencil-parse
  (is (= (c/parse "(a)") '(a)))
  (is (= (c/parse "(stencil Test)") '(stencil Test)))
  (is (= (c/parse "(a : b)") '(a $$ b)))
  (is (= (c/parse "#()") '(tuple)))
  (is (= (c/parse "##()") '(tuples)))
  (is (= (c/parse "#(a,b,c)") '(tuple a b c)))
  (is (= (c/parse "##(a b c)") '(tuples a b c)))
  (is (= (c/parse "#(a:1 b:2 c:3)") '(tuple a $$ 1 b $$ 2 c $$ 3)))
  (is (= (c/parse "##(a:1 b:2 c:3)") '(tuples a $$ 1 b $$ 2 c $$ 3)))
  (is (= (c/parse "(a {int})") '(a ($meta int))))
  (is (= (c/parse "[a]") '(tuple-ref a)))
  (is (= (c/parse "(a : b)") '(a $$ b)))
  (is (= (c/parse "; some") '(comment " some")))
  (is (= (c/parse ";* some then more*;")) '(comment " some then more"))
  (is (= (c/parse "(;* some*; then more)")) '((comment " some") then more)
  ))

(def root "../tests/data/")
(deftest stencil-read
  (is (list? (c/read (str root "geometry/simpleLines.stencil"))))
  (is (list? (c/read (str root "geometry/simpleLines.tstencil")))))


(defn clean [p] (t/drop-comments p))

(defn same-structure? [p1 p2]
  (cond
    (and (not (seq? p1)) (not (seq? p2))) true
    (or (not (seq? p1)) (not (seq? p2))) false
    (and (empty? p1) (empty? p2)) true
    (or (empty? p1) (empty? p2)) false
    :else
      (and (= (count p1) (count p2))
           (every? true? (map same-structure? p1 p2)))))

(deftest compile-stencil-structure
  (is (same-structure?
        (clean (c/compile (str root "geometry/simplelines.stencil") :file))
        (clean (c/compile (str root "geometry/simplelines.tstencil") :file))))
  (is (= (clean (c/compile (str root "geometry/simplelines.stencil") :file))
         (clean (c/compile (str root "geometry/simplelines.tstencil") :file)))))


