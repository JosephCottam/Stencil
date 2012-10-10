(ns Stencil.test.transform
  (:use [stencil.transform])
  (:use [clojure.test]))

(deftest test-tag
 (is (= (normalize '(a)) '(($id a))))
 (is (= (normalize '(stencil program (table a))) '(stencil ($id program) (table ($id a)))))
 (is (= (normalize '(stencil program (table plot (data (range 0 1)))))
        '(stencil ($id program) (table ($id plot) (($policy data) (($op range) ($value 0) ($value 1))))))))

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
