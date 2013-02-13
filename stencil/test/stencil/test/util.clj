(ns stencil.test.util
  (:require [stencil.transform :as t])
  (:use [clojure.test]))

(deftest default-for-type 
  (is (= (t/default-for-type 'int) 0))
  (is (= (t/default-for-type 'double) 0))
  (is (= (t/default-for-type 'float) 0))
  (is (= (t/default-for-type 'long) 0))
  (is (= (t/default-for-type 'string) "")))

(deftest meta->map
  (is (= (t/meta->map '($meta)) {}))
  (is (= (t/meta->map '($meta (a b) (c d))) {'a 'b, 'c 'd})))

(deftest map->meta
  (is (= (t/map->meta {'a 'b, 'c 'd}) '($meta (a b) (c d))))
  (is (= (t/map->meta {}) '($meta))))

(deftest meta-vals
  (is (= (t/meta-vals '($meta (a b) (c d) (e f))) '(b d f)))
  (is (= (t/meta-vals '($meta)) '())))

(deftest meta-keys 
  (is (= (t/meta-keys '($meta (a b) (c d) (e f))) #{'a 'c 'e}))
  (is (= (t/meta-keys '($meta)) #{})))

(deftest atom?
  (is (t/atom? 'a))
  (is (t/atom? "a"))
  (is (t/atom? 3))
  (is (t/atom? java.lang.Long))
  (is (t/atom? java.lang.Class))
  (is (not (t/atom? '(a b c)))))

(deftest default-value
  (is (= (t/default-value 'a '(fields ($meta) a ($meta (default 1)))) 1))
  (is (= (t/default-value 'b '(fields ($meta) a ($meta (default 1)) b ($meta (default 2)) c ($meta (default 3)))) 2))
  (is (= (t/default-value 'c '(fields ($meta) a ($meta (default 1)) b ($meta (default 2)) c ($meta (default 3)))) 3)))


(deftest filter-tagged
  (is (= (t/filter-tagged 'get '()) '()))
  (is (= (t/filter-tagged 'get '((not it) (not it))) '()))
  (is (= (t/filter-tagged 'get '((get it) (get it))) '((get it) (get it))))
  (is (= (t/filter-tagged 'get '((get it) (not it) (get it)))
         '((get it) (get it))))
  (is (= (t/filter-tagged 'get '((not it) (get it) (get it)))
         '((get it) (get it))))
  (is (= (t/filter-tagged 'get '((not it) (not it) (get it)))
         '((get it))))
  (is (= (t/filter-tagged 'get '((not it) (get it) (not it) (get it)))
         '((get it) (get it)))))


(deftest split-preamble
  (is (= (t/split-preamble '(stencil x)) '[(stencil x) ()]))
  (is (= (t/split-preamble '(stencil x (import))) '[(stencil x (import)) ()]))
  (is (= (t/split-preamble '(stencil x (import) (table))) '[(stencil x (import)) ((table))]))
  (is (= (t/split-preamble '(stencil x ($meta))) '[(stencil x ($meta)) ()]))
  (is (= (t/split-preamble '(stencil x ($meta) (import))) '[(stencil x ($meta) (import)) ()]))
  (is (= (t/split-preamble '(stencil x ($meta) (import) (table))) '[(stencil x ($meta) (import)) ((table))])))
         
