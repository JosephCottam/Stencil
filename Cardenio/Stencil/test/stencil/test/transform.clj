(ns stencil.test.transform
  (:require [stencil.transform :as t])
  (:use [clojure.test]))

(deftest drop-comments
  (is (= (t/drop-comments '(comment a))) nil)
  (is (= (t/drop-comments '(stuff (comment a)))) '(stuff))
  (is (= (t/drop-comments '(stuff (comment)))) '(stuff))
  (is (= (t/drop-comments '(stuff (deep (in (comment more)))))) '(stuff (deep (in))))
  (is (= (t/drop-comments (list '$meta java.lang.Long)) (list '$meta java.lang.Long))))

(deftest infix->prefix 
  (is (= (t/infix->prefix '(a + b)) '(+ a b)))
  (is (= (t/infix->prefix '(+ a b)) '(+ a b)))
  (is (= (t/infix->prefix '((a + b))) '((+ a b))))
  (is (= (t/infix->prefix '(map +' ls)) '(map + ls)))
  (is (= (t/infix->prefix '(a plus' b)) '(plus a b)))
  (is (= (t/infix->prefix '(map plus ls)) '(map plus ls)))
  (is (= (t/infix->prefix '((v) $C a)) '($C (v) a)))
  (is (= (t/infix->prefix '(a _)) '(a _)))
  (is (= (t/infix->prefix '(a -> b)) '(-> a b)))
  (is (= (t/infix->prefix '(let (((a) (a + b))) ())) '(let (((a) (+ a b))) ())))
  (is (= (t/infix->prefix '(let (((a) ($do 3 + 4))) ())) '(let (((a) (+ 3 4))) ())))
  (is (= (t/infix->prefix '(let ((a) ($do (a + b) -> (c))) ()))
         '(let ((a) (-> (+ a b) (c))) ()))))

(deftest arrow->using
  (is (= (t/arrow->using '(a b)) '(a b)))
  (is (= (t/arrow->using 'a) 'a))
  (is (= (t/arrow->using '(-> a b)) '(using a b)))
  (is (= (t/arrow->using '(-> (a b c) (d e f))) '(using (a b c) (d e f))))
  (is (= (t/arrow->using '(let (((a) (-> a b))) ())) 
         '(let (((a) (using a b))) ()))))

(deftest validate-let-shape
  (is (= (t/validate-let-shape '(let (a $C b)))) '(let (a $C b)))
  (is (= (t/validate-let-shape '(let (a $C b) (d $C e) ($C f g))) '(let (a $C b) (d $C e) ($C f g))))
  (is (= (t/validate-let-shape '(let (a $C b) (tuple a))) '(let (a $C b) (tuple a))))
  (is (= (t/validate-let-shape '(let (d e f))) '(let (d e f))))
  (is (thrown? RuntimeException (t/validate-let-shape '(let (a b c) (d e f)))))
  (is (thrown? RuntimeException (t/validate-let-shape '(let (a $C c) (d e f) (h i j))))))

(deftest normalize-let-shape
  (is (= (t/normalize-let-shape '(let ((a d) $C (b)) 4)) '(let (((a d) (b))) 4)))
  (is (= (t/normalize-let-shape '(let ((a d) $C (b)) (body))) '(let (((a d) (b))) (body))))
  (is (= (t/normalize-let-shape '(let ((a d) $C (b)))) '(let (((a d) (b))) ())))
  (is (= (t/normalize-let-shape '(let ((a d) $C b))) '(let (((a d) ($do b))) ())))
  (is (= (t/normalize-let-shape '(let ((a d) $C b))) '(let (((a d) ($do b))) ())))
  (is (= (t/normalize-let-shape '(let ((a ($meta)) $C b))) '(let (((a ($meta)) ($do b))) ())))
  (is (= (t/normalize-let-shape '(let (a ($meta) $C b))) '(let (((a ($meta)) ($do b))) ())))
  (is (= (t/normalize-let-shape '(let (a $C b))) '(let (((a) ($do b))) ())))
  (is (= (t/normalize-let-shape '(let ((a d) $C b))) '(let (((a d) ($do b))) ())))
  (is (= (t/normalize-let-shape '(let (a $C b ($meta)))) '(let (((a) ($do b ($meta)))) ())))
  (is (= (t/normalize-let-shape '(let (a $C b) (c $C d) (e $C f) (g $C (h i j))))
         '(let (((a) ($do b)) 
                ((c) ($do d)) 
                ((e) ($do f)) 
                ((g) (h i j)))
            ()))))

(deftest default-let-body
  (is (= (t/default-let-body '(let (((a) b)) ())) '(let (((a) b)) ($ptuple (fields a) a))))
  (is (= (t/default-let-body '(let (((a) b)) (tuple a))) '(let (((a) b)) (tuple a))))
  (is (= (t/default-let-body '(let (((a b) c)) ())) '(let (((a b) c)) ($ptuple (fields a b) a b))))
  (is (= (t/default-let-body '(let (((a) b) ((c) d)) ())) 
         '(let (((a) b) ((c) d)) ($ptuple (fields a c) a c))))
  (is (= (t/default-let-body '(let (((a ($meta)) b)) ())) 
         '(let (((a ($meta)) b)) ($ptuple (fields a ($meta)) a ($meta))))))

(deftest tie-metas 
  (is (= (t/tie-metas '(a)) '(a)))
  (is (= (t/tie-metas '(a a)) '(a a)))
  (is (= (t/tie-metas '(a ($meta))) '((a ($meta)))))
  (is (= (t/tie-metas '(stencil name ($meta))) '(stencil (name ($meta))))))

(deftest tie-and-untie-metas
  (is (let [p '(a)] (= (t/untie-metas (t/tie-metas p)) p)))
  (is (let [p '(stencil name ($meta))] (= (t/untie-metas (t/tie-metas p)) p))))

(deftest supply-metas
  (is (= (t/supply-metas '(a)) '(a ($meta))))
  (is (= (t/supply-metas '(a ($meta))) '(a ($meta))))
  (is (= (t/supply-metas '(a b c)) '(a ($meta) b ($meta) c ($meta))))
  (is (= (t/supply-metas '(a ($meta) b c)) '(a ($meta) b ($meta) c ($meta))))
  (is (= (t/supply-metas '(a b ($meta) c)) '(a ($meta) b ($meta) c ($meta))))
  (is (= (t/supply-metas '(a b c ($meta))) '(a ($meta) b ($meta) c ($meta))))
  (is (= (t/supply-metas '(a ($meta) b c ($meta))) '(a ($meta) b ($meta) c ($meta))))
  (is (= (t/supply-metas '(a (b (c)))) '(a ($meta) (b ($meta) (c ($meta))))))
  (is (= (t/supply-metas '(a (b c) d ($meta))) '(a ($meta) (b ($meta) c ($meta)) d ($meta))))
  (is (= (t/supply-metas '(fields)) '(fields ($meta))))
  (is (= (t/supply-metas '(stencil test)) '(stencil test ($meta))))
  (is (= (t/supply-metas '(let (a b) c)) '(let (a ($meta) b ($meta)) c ($meta)))))

(deftest meta-types
  (is (= (t/meta-types '($meta)) '($meta)) "identity 1")
  (is (= (t/meta-types '($meta (some a))) '($meta (some a))) "identity 2")
  (is (= (t/meta-types '($meta a)) '($meta (type a))))
  (is (= (t/meta-types '($meta (type a))) '($meta (type a))))
  (is (= (t/meta-types '($meta a (type b)))) '($meta a (type b)))
  (is (= (t/meta-types '($meta (some a) (type b))) '($meta (some a) (type b))))
  (is (= (t/meta-types '($meta a b))) '($meta (type a) b))
  (is (= (t/meta-types '(a ($meta a b))) '(a ($meta (type a) b)))))
  

(deftest clean-metas
  (is (= (t/clean-metas 'a) 'a))
  (is (= (t/clean-metas '(a ($meta))) '(a)))
  (is (= (t/clean-metas '(a (b ($meta) c ($meta)))) '(a (b c))))
  (is (= (t/clean-metas '(a ($meta stuff))) '(a ($meta stuff))))
  (is (= (t/clean-metas '(a ($meta stuff) (b ($meta stuff) c ($meta) d ($meta stuff)))) 
         '(a ($meta stuff) (b ($meta stuff) c d ($meta stuff))))))

(deftest ensure-runtime
  (is (= (t/ensure-runtime-import '(stencil test (import picoRuntime))) 
         '(stencil test (import picoRuntime))))
  (is (= (t/ensure-runtime-import '(table x)) '(table x)))
  (is (= (t/ensure-runtime-import '(stencil test)) 
         '(stencil test (import javaPico)))))

(deftest expr->fields
  (is (= (t/expr->fields '($ptuple ($meta) (fields a ($meta)) 1))
         '(fields a ($meta))))
  (is (= (t/expr->fields '($ptuple ($meta) (fields ($meta) a ($meta)) 1))
         '(fields ($meta) a ($meta))))
  (is (= (t/expr->fields '($ptuple ($meta) (fields ($meta) a ($meta (type int))) 1))
         '(fields ($meta) a ($meta  (type int)))))
  (is (= (t/expr->fields '($ptuple ($meta) (fields ($meta) 
                                             a ($meta (type int)) 
                                             b ($meta (type int)) 
                                             c ($meta (type int))) 1 2 3))
         '(fields ($meta) 
              a ($meta (type int))
              b ($meta (type int)) 
              c ($meta (type int)))))
  (is (= (t/expr->fields '(let [a ($meta) true ($meta)] 
                            ($ptuple ($meta) (fields ($meta) a ($meta (type int))) 1)))
         '(fields ($meta) a ($meta (type int))))))

(deftest normalize-fields
  (is (= (t/normalize-fields '(fields ($meta) a ($meta (display "aa")))) '(fields ($meta) a ($meta (display "aa")))))
  (is (= (t/normalize-fields '(fields ($meta) a ($meta))) '(fields ($meta) a ($meta (display "a")))))
  (is (= (t/normalize-fields '(fields ($meta) a ($meta) b ($meta) c ($meta)))
         '(fields ($meta) a ($meta (display "a")) b ($meta (display "b")) c ($meta (display "c")))))
  (is (= (t/normalize-fields '(fields ($meta) 
                                    a ($meta (type int)) 
                                    b ($meta (type double)) 
                                    c ($meta (display "cat"))))
         '(fields ($meta) 
                  a ($meta (display "a") (type int))
                  b ($meta (display "b") (type double)) 
                  c ($meta (display "cat"))))))

(deftest ensure-fields
  (is (= (t/ensure-fields '(stream x ($meta) (fields foo bar))) 
         '(stream x ($meta) (fields foo bar))))
  (is (= (t/ensure-fields '(table x ($meta) (fields foo bar))) 
         '(table x ($meta) (fields foo bar))))
  (is (= (t/ensure-fields 
           '(table x ($meta) 
                   (data (when ($meta) (pred) (gen) 
                           ($ptuples ($meta) (fields a ($meta (type int))) 0)))))
           '(table x ($meta) 
                   (fields a ($meta (type int)))
                   (data (when ($meta) (pred) (gen) ($ptuples ($meta) (fields a ($meta (type int))) 0))))))
  (is (= (t/ensure-fields 
          '(table x ($meta) 
            (data ($ptuple ($meta) (fields foo ($meta (type int)) bar ($meta (type int))) 1 2))))
          '(table x ($meta) 
            (fields 
                    foo ($meta (type int)) 
                    bar ($meta (type int)))
            (data ($ptuple ($meta) (fields foo ($meta (type int)) bar ($meta (type int))) 1 2)))))
  (is (= (t/ensure-fields 
           '(stream x ($meta) 
             (data ($meta) (when ($meta) ($init? ($meta)) () 
                            ($ptuple ($meta) (fields ($meta) foo ($meta (type int)) bar ($meta (type int))) 1 2)))))
           '(stream x ($meta) 
             (fields ($meta) 
                     foo ($meta (type int)) 
                     bar ($meta (type int)))
             (data ($meta) (when ($meta) ($init? ($meta)) () 
                            ($ptuple ($meta) (fields ($meta) foo ($meta (type int)) bar ($meta (type int))) 1 2)))))))


(deftest check-simple-fields
  (is (= (t/check-simple-fields '(fields a b c)) '(fields a b c)))
  (is (= (t/check-simple-fields '(fields a ($meta) b ($meta) c ($meta))) '(fields a ($meta) b ($meta) c ($meta))))
  (is (thrown? RuntimeException (str (t/check-simple-fields '(fields a ($meta) (+ a b)))))))

(deftest check-fields-cover-data
 (is (= (t/check-fields-cover-data
             '(stream x ($meta) 
                      (fields foo ($meta (default 0) (display "foo") (type int)) 
                              bar ($meta (default 0) (display "bar") (type int)))
                      (data ($meta) (when ($meta) ($init? ($meta)) () 
                                      ($ptuple ($meta) 
                                         (fields ($meta) foo ($meta (type int)) bar ($meta (type int))) 
                                         1 2)))))
       '(stream x ($meta) 
                      (fields foo ($meta (default 0) (display "foo") (type int)) 
                              bar ($meta (default 0) (display "bar") (type int)))
                      (data ($meta) (when ($meta) ($init? ($meta)) () 
                                      ($ptuple ($meta) 
                                         (fields ($meta) foo ($meta (type int)) bar ($meta (type int))) 
                                         1 2)))))) 
  (is (str (t/check-fields-cover-data
             '(stream x ($meta) 
                      (fields foo ($meta (default 0) (display "foo") (type int)) 
                              bar ($meta (default 0) (display "bar") (type int)))
                      (data ($meta) (when ($meta) ($init? ($meta)) () 
                                      ($ptuple ($meta) 
                                         (fields ($meta) foo ($meta (type int)) bar ($meta (type int))) 
                                         1 2))))))))
  
(deftest display->fields
  (is (= (t/display->fields '(table x ($meta) (fields a ($meta) b ($meta)))) 
         '(table x ($meta) (fields a ($meta) b ($meta)))))
  (is (= (t/display->fields '(table x ($meta) (fields a ($meta) b ($meta)) (display (a "AYE") (b "BEE"))) )
         '(table x ($meta) (fields a ($meta (display "AYE")) b ($meta (display "BEE")))))))

(deftest defaults->fields
  (is (= (t/defaults->fields '(table x ($meta) (fields a ($meta) b ($meta)))) 
         '(table x ($meta) (fields a ($meta) b ($meta)))))
  (is (= (t/defaults->fields '(table x ($meta) (fields a ($meta) b ($meta)) (defaults (a 0) (b 1))) )
         '(table x ($meta) (fields a ($meta (default 0)) b ($meta (default 1)))))))


(deftest init->when
  (is (= (t/init->when '(init (gen))) '(when ($init?) () (gen)))))


(deftest split-when
  (is (= (t/split-when '()) '()))
  (is (= (t/split-when '(stencil test)) '(stencil test)))
  (is (= (t/split-when '(stencil test 
                         (stream input (fields x ($meta))) 
                         (table t (data (when- (delta input) (using (fields x ($meta)) (items input) (let (x:x))))))))
         '(stencil test 
             (stream input (fields x ($meta))) 
             (table t (data (when- (delta input) (using (fields x ($meta)) (items input) (let (x:x)))))))))
  (is (= (t/split-when '(stencil test 
                         (stream input (fields x ($meta))) 
                         (table t (data (when ($meta) (delta input) (items ($meta) input ($meta)) (let (x:x)))))))
         '(stencil test 
             (stream input (fields x ($meta))) 
             (table t (data (when- ($meta)
                              (delta input) 
                              (using ($meta)
                                     (fields x ($meta)) 
                                     (items ($meta) input ($meta)) 
                                     (let (x:x)))))))))
  (is (thrown? RuntimeException
               ;;str forces evaluatuion to get the exection...maybe
               (str(t/split-when '(table plot ($meta)
                                   (fields ($meta) id ($meta (display "id")))
                                   (render ($meta) (text ($meta) "simpleLines_test.tuples" ($meta)))
                                   (data ($meta)
                                     (when ($meta)
                                       (onChange ($meta) values ($meta))
                                       (items ($meta) values ($meta))
                                       (let (((id ($meta)) ($do ($meta) v ($meta))))
                                         ($ptuple ($meta) (fields ($meta) (id ($meta))) id ($meta)))))))))))
               
(deftest infer-types
  (is (= (t/infer-types '(x ($meta (type fn)))) '(x ($meta (type fn)))))
  (is (= (t/infer-types '(f ($meta) x ($meta))) '(f ($meta (type fn)) x ($meta (type ***)))))
  (is (= (t/infer-types '(f ($meta) x ($meta) y ($meta) z ($meta))) 
         '(f ($meta (type fn)) x ($meta (type ***)) y ($meta (type ***)) z ($meta (type ***)))))
  (is (= (t/infer-types '(f ($meta) (x ($meta)))) 
         '(f ($meta (type fn)) (x ($meta (type fn))))))
  (is (= (t/infer-types (list 'f '($meta) 'x (list '$meta (list 'type nil))))
         '(f ($meta (type fn)) x ($meta (type ***)))))
  (is (= (t/infer-types '(f ($meta) a ($meta) b ($meta (type int))))
         '(f ($meta (type fn)) a ($meta (type ***)) b ($meta (type int)))))
  (is (= (t/infer-types (list 'f '($meta nil)))
         '(f ($meta (type fn)))))
  (is (= (t/infer-types '($ptuple ($meta) (fields ($meta) (x)) x ($meta)))
         '($ptuple ($meta (type (fn (...) (tuple (...))))) (fields ($meta (type fields)) (x)) x ($meta (type ***)))))
  (is (= (t/infer-types '(let (((x ($meta)) (do ($meta) (f ($meta))))) 
                           ($ptuple ($meta) (fields ($meta) (x)) x ($meta))))
         '(let (((x ($meta (type ***))) (do ($meta (type fn)) (f ($meta (type fn)))))) 
            ($ptuple ($meta (type (fn (...) (tuple (...))))) 
               (fields ($meta (type fields)) (x)) x ($meta (type ***))))))
  (is (= (t/infer-types '(let (((x ($meta) y ($meta)) (f ($meta)))) (g ($meta))))
         '(let (((x ($meta (type ***)) y ($meta (type ***))) (f ($meta (type fn))))) (g ($meta (type fn))))))
  (is (= (t/infer-types '(let (((x ($meta)) (f ($meta)))
                               ((y ($meta)) (g ($meta))))
                           (body ($meta))))
         '(let (((x ($meta (type ***))) (f ($meta (type fn))))
                ((y ($meta (type ***))) (g ($meta (type fn)))))
           (body ($meta (type fn))))))
  (is (thrown? RuntimeException (t/infer-types '(f ($meta (type int)) a)))))

(deftest ensure-using-tuple
  (is (= (t/ensure-using-tuple 
           '(using ($meta) (ptuple ($meta (type (fn (...) (tuple (...))))) (fields a) b) (x))) 
         '(using ($meta) (ptuple ($meta (type (fn (...) (tuple (...))))) (fields a) b) (x))))
  (is (= (t/ensure-using-tuple 
           '(using ($meta) (let (((x) ($do 1))) (ptuple ($meta (type (fn (...) (tuple (...))))) (fields a) b)) (x)))
         '(using ($meta) (let (((x) ($do 1))) (ptuple ($meta (type (fn (...) (tuple (...))))) (fields a) b)) (x))))
  (is (= (t/ensure-using-tuple '(using ($meta) b (x))) 
         '(using ($meta) (tuple ($meta (type (fn (...) (tuple (...))))) b) (x))))
  (is (= (t/ensure-using-tuple '(using ($meta) (b) (x))) 
         '(using ($meta) (tuple ($meta (type (fn (...) (tuple (...))))) (b)) (x)))))






