(ns Stencil.test.transform
  (:require [stencil.transform :as t])
  (:use [clojure.test]))

(deftest infix->prefix 
  (is (= (t/infix->prefix '(a + b)) '(+ a b)))
  (is (= (t/infix->prefix '(+ a b)) '(+ a b)))
  (is (= (t/infix->prefix '(map +' ls)) '(map + ls)))
  (is (= (t/infix->prefix '(a plus' b)) '(plus a b)))
  (is (= (t/infix->prefix '(map plus ls)) '(map plus ls))))


(deftest normalizeLetShape
  (is (= (t/normalizeLetShape '(let (a $C b) (c $C d) (e $C f) (g $C (h i j))))
         '(let ((a) $C b) 
               ((c) $C d) 
               ((e) $C f) 
               ((g) $C (h i j))))))

(deftest defaultLetBody
  (is (= (t/defaultLetBody '(let ($C (a) b))) '(let ($C (a) b) ($ptuple '(a) a))))
  (is (= (t/defaultLetBody '(let ($C (a) b) (tuple a))) '(let ($C (a) b) (tuple a))))
  (is (= (t/defaultLetBody '(let ($C (a b) c))) '(let ($C (a b) c) ($ptuple '(a b) a b))))
  (is (= (t/defaultLetBody '(let ($C (a) b) ($C (c) d))) '(let ($C (a) b) ($C (c) d) ($ptuple '(a c) a c)))))

(deftest validateLetShape
  (is (= (t/validateLetShape '(let (a $C b)))) '(let (a $C b)))
  (is (= (t/validateLetShape '(let (a $C b) (d $C e) ($C f g))) '(let (a $C b) (d $C e) ($C f g))))
  (is (= (t/validateLetShape '(let (a $C b) (tuple a))) '(let (a $C b) (tuple a))))
  (is (= (t/validateLetShape '(let (d e f))) '(let (d e f))))
  (is (thrown? RuntimeException (t/validateLetShape '(let (a b c) (d e f)))))
  (is (thrown? RuntimeException (t/validateLetShape '(let (a $C c) (d e f) (h i j))))))


(deftest supplyMetas
  (is (= (t/supplyMetas '(a)) '(a ($meta))))
  (is (= (t/supplyMetas '(a ($meta))) '(a ($meta))))
  (is (= (t/supplyMetas '(a b c)) '(a ($meta) b ($meta) c ($meta))))
  (is (= (t/supplyMetas '(a ($meta) b c)) '(a ($meta) b ($meta) c ($meta))))
  (is (= (t/supplyMetas '(a b ($meta) c)) '(a ($meta) b ($meta) c ($meta))))
  (is (= (t/supplyMetas '(a b c ($meta))) '(a ($meta) b ($meta) c ($meta))))
  (is (= (t/supplyMetas '(a ($meta) b c ($meta))) '(a ($meta) b ($meta) c ($meta))))
  (is (= (t/supplyMetas '(a (b (c)))) '(a ($meta) (b ($meta) (c ($meta))))))
  (is (= (t/supplyMetas '(a (b c) d ($meta))) '(a ($meta) (b ($meta) c ($meta)) d ($meta))))
  (is (= (t/supplyMetas '(stencil test)) '(stencil test ($meta))))
  (is (= (t/supplyMetas '(let (a b) c)) '(let (a ($meta) b ($meta)) c ($meta)))))

(deftest metaTypes
  (is (= (t/metaTypes '($meta a)) '($meta (type a))))
  (is (= (t/metaTypes '($meta (type a))) '($meta (type a))))
  (is (= (t/metaTypes '($meta a (type b)))) '($meta a (type b)))
  (is (= (t/metaTypes '($meta (some a) (type b))) '($meta (some a) (type b))))
  (is (= (t/metaTypes '($meta a b))) '($meta (type a) b)))
  

(deftest cleanMetas
  (is (= (t/cleanMetas 'a) 'a))
  (is (= (t/cleanMetas '(a ($meta))) '(a)))
  (is (= (t/cleanMetas '(a (b ($meta) c ($meta)))) '(a (b c))))
  (is (= (t/cleanMetas '(a ($meta stuff))) '(a ($meta stuff))))
  (is (= (t/cleanMetas '(a ($meta stuff) (b ($meta stuff) c ($meta) d ($meta stuff)))) 
         '(a ($meta stuff) (b ($meta stuff) c d ($meta stuff))))))

(deftest ensureRuntime
  (is (= (t/ensureRuntimeImport '(stencil test (import picoRuntime))) 
         '(stencil test (import picoRuntime))))
  (is (= (t/ensureRuntimeImport '(stencil test)) 
         '(stencil test (import javaPico)))))

(deftest expr->fields
  (is (= (t/expr->fields '(ptuple '(a ($meta (type int))) 1))
         '(fields (a ($meta (default 0) (display "a")  (type int))))))
  (is (= (t/expr->fields '(ptuple '(a ($meta (type int)) b ($meta (type int)) c ($meta (type int))) 1 2 3))
         '(fields (a ($meta (default 0) (display "a")  (type int))
                   b ($meta (default 0) (display "b")  (type int)) 
                   c ($meta (default 0) (display "c")  (type int))))))
  (is (= (t/expr->fields '(let [a true] (ptuple '(a ($meta (type int))) 1)))
         '(fields (a ($meta (default 0) (display "a")  (type int)))))))

(deftest defaults->fields
  (is (= (t/defaults->fields '(table x ($meta) (fields a ($meta) b ($meta)))) 
         '(table x ($meta) (fields a ($meta) b ($meta)))))
  (is (= (t/defaults->fields '(table x ($meta) (fields a ($meta) b ($meta)) (defaults (a 0) (b 1))) )
         '(table x ($meta) (fields a ($meta (default 0)) b ($meta (default 1)))))))

(deftest test-binding-when
  (is (= (t/binding-when '()) '()))
  (is (= (t/binding-when '(stencil test)) '(stencil test)))
  (is (= (t/binding-when '(stencil test 
                         (stream input (fields x)) 
                         (table t (data (when+ (delta input) (items input) ($binding x) (let (x:x)))))))
         '(stencil test 
           (stream input (fields x)) 
           (table t (data (when+ (delta input) (items input) ($binding x) (let (x:x))))))))
  (is (= (t/binding-when '(stencil test 
                         (stream input (fields x)) 
                         (table t (data (when (delta input) (items input) (let (x:x)))))))
         '(stencil test 
           (stream input (fields x)) 
           (table t (data (when+ (delta input) (items input) ($binding x) (let (x:x)))))))))


