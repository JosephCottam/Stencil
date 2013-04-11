(ns stencil.transform)

(defn gen-binds [bind-meta render-type source-fields existing]
  "Generate a binding statement given a render type and a list of fields.
   TODO: Care about the render type...right now it is just ignored and x/y/z/color are generated for"
  (if (empty? source-fields)
    (throw (parseException bind-meta "Cannot generate binds statement, no fields supplied in current context."))
    (let [render-fields (set '(x y z color))  ;;Lookup bind field based on render-type
          source-fields (set (remove meta? (rest source-fields)))
          existing-fields (set (map first existing))
          bind-fields (clojure.set/intersection source-fields render-fields)
          bind-fields (sort (clojure.set/difference bind-fields existing-fields))
          bindings (map #(list % '($meta (type fn)) % '($meta (type ***))) bind-fields) ;;TODO: the 'fn' type here is wrong...just saying it needs to be fixed 
          bindings (concat existing bindings)]
      (cons 'bind (cons bind-meta bindings)))))

(defn normalize-render-binds [program]
  "Fills in (bind auto) statements with the help of (table ... (fields ...)) statements.
  Assumes that the render is normalized."
  (letfn 
    [(auto-bind? [policy] (any= 'auto policy)) 
     (clean-bind [policy] 
       "Remove auto, bind and related metas from a bind policy"
       (cond
         (empty? policy) '()
         (= 'bind (first policy)) (clean-bind (full-drop policy))
         (= 'auto (first policy)) (clean-bind (full-drop policy))
         :else (list* (first policy) (clean-bind (rest policy)))))
     (clean-bindings [bindings]
       "Remove bind operator from bindings"
       (map (fn [[bind meta & rest]] rest) bindings))
     (prep-bind [[type fields] bind] 
       (let [bind-meta (second bind)
             cleaned (clean-bindings (clean-bind bind))]
         (if (auto-bind? bind)
           (gen-binds bind-meta type fields cleaned)
           (list* 'bind bind-meta cleaned))))
     (walker [tableBriefs]
       (fn helper [program]
         (match program
           (a :guard atom?) a
           (['render m0 id m1 source m2 type m3 & policies] :seq)
             (let [binds (->> policies
                           (filter-tagged 'bind)
                           (map (partial prep-bind (tableBriefs source))))
                   others (filter-tagged (complement =) 'bind policies)]
               `(~'render ~m0 ~id ~m1 ~source ~m2 ~type ~m3 ~@binds ~@others))
           :else (map helper program))))]
    (let [tables (filter-tagged 'table program)
          names (map (fn [[tag m0 name & rest]] name) tables)
          types (map (fn [[tag m0 name m1 type & rest]] type) tables)
          fields (map #(first (filter-tagged 'fields %)) tables)
          briefs (zipmap names (map list types fields))]
      ((walker briefs) program))))


(defn normalize-renders [program]
  "Ensure that every render statement has a name, source table and auto-binds are filled in."
  (letfn 
    [(gen-name [] (gensym 'rend_))
     (helper [table program]
       (match program
         (a :guard atom?) a
         (['table (m0 :guard meta?) name & policies] :seq) 
           (let [fields (first (filter-tagged 'fields policies))]
             `(~'table ~m0 ~name ~@(map (partial helper name) policies)))
         (['render (m0 :guard meta?) id (m1 :guard meta?) source (m2 :guard meta?) type (m3 :guard meta?) & policies] :seq)
           (let [id (if (= '_ id) (gen-name) id)]
             `(~'render ~m0 ~id ~m1 ~source ~m2 ~type ~m3 ~@policies))
         (['render (m0 :guard meta?) id (m1 :guard meta?) type (m3 :guard meta?) & policies] :seq) 
           (let [id (if (= '_ id) (gen-name) id)
                 table (if (nil? table) (throw (parseException m0 "Could not normalize render source, no containing table.")) table)]
             `(~'render ~m0 ~id ~m1 ~table (~'$meta (~'type ~'table)) ~type ~m3 ~@policies))
         (['render (m1 :guard meta?) type (m3 :guard meta?) & policies] :seq) 
           (let [id (gen-name)
                 table (if (nil? table) (throw (parseException m1 "Could not normalize render source, no containing table.")) table)
                 m0 '($meta (type render))]
             `(~'render ~m0 ~id ~m1 ~table (~'$meta (~'type ~'table)) ~type ~m3 ~@policies))
         :else (map (partial helper table) program)))]
    (helper nil program)))






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
