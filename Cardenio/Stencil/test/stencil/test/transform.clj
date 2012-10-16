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

(deftest test-defaultLetBody
  (is (= (defaultLetBody '(let ($C (a) b))) '(let ($C (a) b) ($ptuple '(a) a))))
  (is (= (defaultLetBody '(let ($C (a) b) (tuple a))) '(let ($C (a) b) (tuple a))))
  (is (= (defaultLetBody '(let ($C (a b) c))) '(let ($C (a b) c) ($ptuple '(a b) a b))))
  (is (= (defaultLetBody '(let ($C (a) b) ($C (c) d))) '(let ($C (a) b) ($C (c) d) ($ptuple '(a c) a c)))))

(deftest test-validateLetShape
  (is (= (validateLetShape '(let (a $C b)))) '(let (a $C b)))
  (is (= (validateLetShape '(let (a $C b) (d $C e) ($C f g))) '(let (a $C b) (d $C e) ($C f g))))
  (is (= (validateLetShape '(let (a $C b) (tuple a))) '(let (a $C b) (tuple a))))
  (is (= (validateLetShape '(let (d e f))) '(let (d e f))))
  (is (thrown? RuntimeException (validateLetShape '(let (a b c) (d e f)))))
  (is (thrown? RuntimeException (validateLetShape '(let (a $C c) (d e f) (h i j))))))

