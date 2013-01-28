(ns stencil.transform)

(defn gen-binds [bind-meta render-type fields]
  "Generate a binding statement given a render type and a list of fields.
   TODO: Care about the render type...right now it is just ignored."
  (if (empty? fields)
    (throw (RuntimeException. "Cannot generate binds statement, no fields supplied for context."))
    (let [render-fields (set '(x y z color))
          fields (set (remove meta? (rest fields)))
          bind-fields (sort (clojure.set/intersection fields render-fields))
          bindings (map #(list % '($meta (type fn)) % '($meta (type ***))) bind-fields)]
      (cons 'bind (cons bind-meta bindings)))))

(defn normalize-renders [program]
  "Ensure that every render statement has a name, source data and auto-binds are filled in."
  (letfn 
    [(gen-name [] (gensym 'rend))
     (auto-bind? [policy] (= (remove meta? policy) '(bind auto)))
     (drop-bind-op [entry]
       (if (= '$C (first entry))
         (full-drop entry)
         entry))
     (maybe-clean [policy]
            (let [[tag meta & bindings] policy]
              (if (= tag 'bind)
                `(~tag ~meta ~@(map drop-bind-op bindings))
                policy)))
     (helper [table fields program]
       (match program
         (a :guard atom?) a
         (['table name & rest] :seq) 
           (let [fields (first (filter-tagged 'fields rest))]
             `(~'table ~name ~@(helper name fields rest)))
         (['render id (m1 :guard meta?) source (m2 :guard meta?) type (m3 :guard meta?) & bind] :seq)
           (let [bind (first bind)
                 bind (if (auto-bind? bind) (gen-binds (second bind) type fields) bind)
                 bind (maybe-clean bind)
                 id (if (= '_ id) (gen-name) id)]
             (list 'render id m1 source m2 type m3 bind))
         (['render id (m1 :guard meta?) type (m3 :guard meta?) & bind] :seq) 
           (let [bind (first bind)
                 bind (if (auto-bind? bind) (gen-binds (second bind) type fields) bind)
                 bind (maybe-clean bind)
                 id (if (= '_ id) (gen-name) id)
                 table (if (nil? table) (throw (RuntimeException. "Could not normalize render source, no containing table.")) table)]
             (list 'render id m1 table '($meta (type ***)) type m3 bind))
         (['render (m1 :guard meta?) type (m3 :guard meta?) & bind] :seq) 
           (let [bind (first bind)
                 bind (if (auto-bind? bind) (gen-binds (second bind) type fields) bind)
                 bind (maybe-clean bind)
                 table (if (nil? table) (throw (RuntimeException. "Could not normalize render source, no containing table.")) table)]
             (list 'render (gen-name) m1 table '($meta (type ***)) type m3 bind))
         :else (map (partial helper table fields) program)))]
    (helper nil nil program)))






;;-------------------------------------------------------------------------------------------------------------------

(defn gather-renders [program]
  "Lift all render statements out to top-level"
  (letfn 
    [(render? [p] (and (seq? p) (= 'render (first p))))
     (sift [program]
       (match program
         (a :guard atom?) (list '() a)
         (e :guard empty?) (list '() '())
         ([(r :guard render?) & after] :seq)
            (let [[renders reduced] (sift after)]
              (list (cons r renders) reduced))
         :else 
            (let [[renderA reducedA] (sift (first program))
                  [renderB reducedB] (sift (rest program))]
              (list (concat renderA renderB) (cons reducedA reducedB)))))
     (split [program]
       (let [f (first program)]
         (cond
           (nil? f) (list '() '())
           (and (seq? f) (= 'table (first f))) (list '() program)
           :else (let [[before after] (split (rest program))]
                   (list (cons f before) after)))))]
    (let [[renders reduced] (sift program)
          [before after] (split reduced)]
      (concat before renders after))))
