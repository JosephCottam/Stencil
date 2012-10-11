(ns Stencil.test.transform
  (:use [stencil.transform])
  (:use [clojure.test]))

(deftest test-tag
 (is (= (tag-elements 'a) '($val a)))
 (is (= (tag-elements '(a)) '(($val a))))
 (is (= (tag-elements '(stencil program)) '(stencil ($val program))))
 (is (= (tag-elements '(stencil program (table a))) '(stencil ($val program) (table ($val a)))))
 (is (= (tag-elements '(stencil program (table plot (data (range 0 1)))))
        '(stencil ($val program) (table ($val plot) (data (($val range) ($val 0) ($val 1))))))))

(deftest test-liftInfix
  (is (= (infix->prefix '(a + b)) '(+ a b)))
  (is (= (infix->prefix '(+ a b)) '(+ a b)))
  (is (= (infix->prefix '(map +' ls)) '(map + ls)))
  (is (= (infix->prefix '(a plus' b)) '(plus a b)))
  (is (= (infix->prefix '(map plus ls)) '(map plus ls))))


(deftest test-normalizeLetShape
  (is (= (normalizeLetShape '(let (a $C b) (c $C d) (e $C f) (g $C (h i j))))
         '(let ((a) $C b) 
               ((c) $C d) 
               ((e) $C f) 
               ((g) $C (h i j))))))
