(ns stencil.test.util
  (:require [stencil.util :as u])
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


(deftest remove-tagged 
  (is (= (t/remove-tagged 'kill '((kill 1) (keep 2) (kill 3) (keep 4)))
         '((keep 2) (keep 4))))
  (is (= (t/remove-tagged 'kill '(keep keep keep (kill 1) (kill 2) (keep 3) (kill 4)))
         '(keep keep keep (keep 3))))
  (is (= (t/remove-tagged u/any= '(kill remove extra) '(keep keep (kill 1) (remove 1) (stay 2) (keep 3) (remove 4)))
         '(keep keep (stay 2) (keep 3)))
      "Custom comparison operator"))

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

(deftest clean-metas
  (is (= (u/clean-metas 'a) 'a))
  (is (= (u/clean-metas '(a ($meta))) '(a)))
  (is (= (u/clean-metas '(a (b ($meta) c ($meta)))) '(a (b c))))
  (is (= (u/clean-metas '(a ($meta stuff))) '(a ($meta stuff))))
  (is (= (u/clean-metas '(a ($meta stuff) (b ($meta stuff) c ($meta) d ($meta stuff)))) 
         '(a ($meta stuff) (b ($meta stuff) c d ($meta stuff))))))

(deftest remove-metas
  (is (= (u/remove-metas 'a) 'a))
  (is (= (u/remove-metas '(a ($meta))) '(a)))
  (is (= (u/remove-metas '(a (b ($meta) c ($meta)))) '(a (b c))))
  (is (= (u/remove-metas '(a ($meta stuff))) '(a)))
  (is (= (u/remove-metas '(a ($meta stuff) (b ($meta stuff) c ($meta) d ($meta stuff)))) 
         '(a (b c d)))))

(deftest reduce-metas
  (is (= (u/reduce-metas 'a) 'a))
  (is (= (u/reduce-metas '(a (b ($meta) c ($meta)))) '(a (b ($meta) c ($meta)))))
  (is (= (u/reduce-metas '(a ($meta (.some 1)))) '(a ($meta))))
  (is (= (u/reduce-metas '(a ($meta stuff (.some other)))) '(a ($meta stuff))))
  (is (= (u/reduce-metas '(a ($meta stuff) (b ($meta stuff (.some 1)) c ($meta (.other 1)) d ($meta stuff))))
         '(a ($meta stuff) (b ($meta stuff) c ($meta) d ($meta stuff))))))
         
