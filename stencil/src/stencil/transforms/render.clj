(ns stencil.transform)

(defn gen-binds [bind-meta render-type source-fields existing]
  "Generate a binding statement given a render type and a list of fields.
   TODO: Care about the render type...right now it is just ignored and x/y/z/color are generated for"
;  (println "Existing: " existing) )

  (if (empty? source-fields)
    (throw (RuntimeException. "Cannot generate binds statement, no fields supplied for context."))
    (let [render-fields (set '(x y z color))  ;;Lookup bind field based on render-type
          source-fields (set (remove meta? (rest source-fields)))
          existing-fields (set (map first existing))
          bind-fields (clojure.set/intersection source-fields render-fields)
          bind-fields (sort (clojure.set/difference bind-fields existing-fields))
          bindings (map #(list % '($meta (type fn)) % '($meta (type ***))) bind-fields) ;;TODO: the 'fn' type here is wrong...just saying it needs to be fixed 
          bindings (concat existing bindings)]
      (cons 'bind (cons bind-meta bindings)))))

(defn normalize-renders [program]
  "Ensure that every render statement has a name, source data and auto-binds are filled in."
  (letfn 
    [(gen-name [] (gensym 'rend_))
     (auto-bind? [policy] (any= 'auto policy)) 
     (drop-bind-op [entry]
       (if (and (seq? entry) (= '$$ (first entry)))
         (full-drop entry)
         entry))
     (maybe-clean [policy]
       "Remove bind ops, if there is a 'bind' statement in this render statement."
       (let [[tag meta & bindings] policy]
         (if(= tag 'bind)
           `(~tag ~meta ~@(map drop-bind-op bindings))
           policy)))
     (prep-bind [fields bind] 
       (let [bind (maybe-clean bind)]
         (if (auto-bind? bind) 
           (gen-binds (second bind) type fields (rest (remove #(or (meta? %) (= % 'auto)) bind)))
           bind)))

     (helper [table fields program]
       (match program
         (a :guard atom?) a
         (['table name & policies] :seq) 
           (let [fields (first (filter-tagged 'fields policies))]
             `(~'table ~name ~@(helper name fields policies)))
         (['render id (m1 :guard meta?) source (m2 :guard meta?) type (m3 :guard meta?) & binds] :seq)
           (let [binds (map (partial prep-bind fields) binds)
                 id (if (= '_ id) (gen-name) id)]
             `(~'render ~id ~m1 ~source ~m2 ~type ~m3 ~@binds))
         (['render id (m1 :guard meta?) type (m3 :guard meta?) & binds] :seq) 
           (let [binds (map (partial prep-bind fields) binds)
                 id (if (= '_ id) (gen-name) id)
                 table (if (nil? table) (throw (RuntimeException. "Could not normalize render source, no containing table.")) table)]
             `(~'render ~id ~m1 ~table (~'$meta (~'type ~'table)) ~type ~m3 ~@binds))
         (['render (m1 :guard meta?) type (m3 :guard meta?) & binds] :seq) 
           (let [binds (map (partial prep-bind fields) binds)
                 id (gen-name)
                 table (if (nil? table) (throw (RuntimeException. "Could not normalize render source, no containing table.")) table)]
             `(~'render ~id ~m1 ~table (~'$meta (~'type ~'table)) ~type ~m3 ~@binds))
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
              (list (concat renderA renderB) (cons reducedA reducedB)))))]
    (let [[renders reduced] (sift program)
          [preamble body] (split-preamble reduced)]
      (concat preamble renders body))))
