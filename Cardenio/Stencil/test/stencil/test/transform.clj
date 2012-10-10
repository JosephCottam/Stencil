(ns Stencil.test.transform
  (:use [stencil.transform])
  (:use [clojure.test]))

(deftest tag
 (is (= (normalize 'a) '($id a)))
 (is (= (normalize '(stencil program a)) '(stencil ($id program) ($id a))))
 (is (= (normalize '(stencil program (table plot (data (range 0 1)))))
        '(stencil ($id program) (table ($id plot) ($policy data (($op range) 0 1)))))))

(deftest liftInfix
  (is (= (normalize '(a + b)) '(($op +) ($id a) ($id b))))
  (is (= (normalize '(+ a b) '(($op +) ($id a) ($id b)))))
  (is (= (normalize '(map +' ls)) '(($op map) ($id +) ($id ls))))
  (is (= (normalize '(a plus' b) '(($op plus) ($id a) ($id b)))))
  (is (= (normalize '(map plus ls)) '(($op map) ($id plus) ($id ls)))))

