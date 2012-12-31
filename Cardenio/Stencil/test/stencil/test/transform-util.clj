(in-ns 'stencil.test.transform)

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
  (is (atom? 'a))
  (is (atom? "a"))
  (is (atom? 3))
  (is (atom? java.lang.Long))
  (is (atom? java.lang.Class))
  (is (not (atom? (list a b c)))))
