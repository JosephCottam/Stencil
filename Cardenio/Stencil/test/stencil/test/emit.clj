(ns stencil.test.emit
  (:require [stencil.emit :as e])
  (:use [clojure.test]))

(deftest default-for-type 
  (is (= (e/default-for-type 'int) 0))
  (is (= (e/default-for-type 'double) 0))
  (is (= (e/default-for-type 'float) 0))
  (is (= (e/default-for-type 'long) 0))
  (is (= (e/default-for-type 'string) "")))

(deftest expr->fields
  (is (= (e/expr->fields '(ptuple '(a ($meta ((type int)))) 1))
         '(fields (a ($meta ((default 0) (display "a")  (type int)))))))
  (is (= (e/expr->fields '(ptuple '(a ($meta ((type int))) b ($meta ((type int))) c ($meta ((type int)))) 1 2 3))
         '(fields (a ($meta ((default 0) (display "a")  (type int)))
                   b ($meta ((default 0) (display "b")  (type int))) 
                   c ($meta ((default 0) (display "c")  (type int)))))))
  (is (= (e/expr->fields '(let [a true] (ptuple '(a ($meta {type int})) 1)))
         '(fields (a ($meta ((default 0) (display "a")  (type int))))))))

