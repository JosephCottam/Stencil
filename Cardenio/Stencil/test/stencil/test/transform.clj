(ns Stencil.test.transform
  (:use [stencil.transform])
  (:use [clojure.test]))

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


(deftest test-supplyMetas
  (is (= (supplyMetas '(a)) '(a ($meta))))
  (is (= (supplyMetas '(a ($meta))) '(a ($meta))))
  (is (= (supplyMetas '(a b c)) '(a ($meta) b ($meta) c ($meta))))
  (is (= (supplyMetas '(a ($meta) b c)) '(a ($meta) b ($meta) c ($meta))))
  (is (= (supplyMetas '(a b ($meta) c)) '(a ($meta) b ($meta) c ($meta))))
  (is (= (supplyMetas '(a b c ($meta))) '(a ($meta) b ($meta) c ($meta))))
  (is (= (supplyMetas '(a ($meta) b c ($meta))) '(a ($meta) b ($meta) c ($meta))))
  (is (= (supplyMetas '(a (b (c)))) '(a ($meta) (b ($meta) (c ($meta))))))
  (is (= (supplyMetas '(a (b c) d ($meta))) '(a ($meta) (b ($meta) c ($meta)) d ($meta)))))

(deftest test-metaTypes
  (is (= (metaTypes '($meta (a))) '($meta ((type a)))))
  (is (= (metaTypes '($meta ((type a)))) '($meta ((type a)))))
  (is (= (metaTypes '($meta (a (type b)))) '($meta (a (type b)))))
  (is (= (metaTypes '($meta ((some a) (type b)))) '($meta ((some a) (type b)))))
  (is (= (metaTypes '($meta (a b))) '($meta ((type a) b)))))
  

(deftest test-cleanMetas
  (is (= (cleanMetas 'a) 'a))
  (is (= (cleanMetas '(a ($meta))) '(a)))
  (is (= (cleanMetas '(a (b ($meta) c($meta)))) '(a (b c))))
  (is (= (cleanMetas '(a ($meta stuff))) '(a ($meta stuff))))
  (is (= (cleanMetas '(a ($meta stuff) (b ($meta stuff) c ($meta) d ($meta stuff)))) 
         '(a ($meta stuff) (b ($meta stuff) c d ($meta stuff))))))


(deftest test-ensureRuntime
  (is (= (ensureRuntimeImport '(stencil test (import picoRuntime))) 
         '(stencil test (import picoRuntime))))
  (is (= (ensureRuntimeImport '(stencil test)) 
         '(stencil test (import javaPico)))))


(deftest test-binding-when
  (is (= (binding-when '()) '()))
  (is (= (binding-when '(stencil test)) '(stencil test)))
  (is (= (binding-when '(stencil test 
                         (stream input (fields x)) 
                         (table t (data (when+ (delta input) (items input) ($binding x) (let (x:x)))))))
         '(stencil test 
           (stream input (fields x)) 
           (table t (data (when+ (delta input) (items input) ($binding x) (let (x:x))))))))
  (is (= (binding-when '(stencil test 
                         (stream input (fields x)) 
                         (table t (data (when (delta input) (items input) (let (x:x)))))))
         '(stencil test 
           (stream input (fields x)) 
           (table t (data (when+ (delta input) (items input) ($binding x) (let (x:x)))))))))
