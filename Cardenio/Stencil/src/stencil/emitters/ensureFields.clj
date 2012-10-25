(in-ns 'stencil.emit)


(defn stream-or-table? [v] (or (= 'stream v) (= 'table v)))

(defn filter-policies 
  "policy* -> policy*: Filter a list of policies to just the requested policy type"
  [policy policies] (filter #(= (first %) policy) policies))

(defn default-for-type [type]
  (case type
    (int long float double number) 0
    string ""
    (throw (RuntimeException. (str "Default not known for type " type)))))


(defn expr->fields [expr]
  (defn ensure-parts [name meta]
    (cond
      (not (contains? meta 'type))
        (throw (RuntimeException. "Type not found when required.")) 
      (not (contains? meta 'display))
        (recur name (assoc meta 'display (str name)))
      (not (contains? meta 'default))
        (recur name (assoc meta 'default (default-for-type (meta 'type))))
      :else meta))

  (defn decl->fields
    "A 'declaration' is a set of names and meta-data.  
    It is essentially syntactically REQUIRED meta-data, where true $meta statements are optional in the source syntax.
    A 'fields' statement is a list of field names, with meta-data for machine type, default value and display name."
    [decl]
    (let [names (take-nth 2 decl)
          metas (take-nth 2 (rest decl))
          metas (map (comp map->meta ensure-parts) names (map meta->map metas))]
      (list 'fields (interleave names metas))))

  (case (first expr)
    let (expr->fields (nth expr 2))    ;(let <bindings> <body>)...and the body has to return a tuple
    ptuple (decl->fields (second (second expr)))
    (throw (RuntimeException. (str "Could not find prototyped tuple in " expr)))))



(defn ensureFields
  "Ensure there is a 'fields' policy in each table and stream.
   If there is not currently a 'fields' policy, generates on based on the first 'data' policy found.
   After execution, fields policy will include type and default value in its metadata."
  [program]
  (match [program]
    [([(tag :guard stream-or-table?) (name :guard symbol?) (meta :guard meta?) & policies] :seq)]
      (let [fields (filter-policies 'fields policies)
            fields (if (empty? fields) 
                     (expr->fields (ffirst (filter-policies 'data policies)))
                     fields)]
        `(~tag ~name ~fields ~@policies))
    :else (map ensureFields program)))
      


(defn validateFields
  "Test if the 'fields' policy covers the names used in all of the 'data' policies.
   'fields' may include names NOT in a 'data', if that field also has a default value."
  [program]

  (defn covers [fields]
    (let [fields (set (keys fields))]
      (fn [data] 
        (clojure.set/subset? fields (meta-keys (expr->fields (ffirst data)))))))

  (match [program]
    [([(tag :guard stream-or-table?) (name :guard symbol?) (meta :guard meta?) & policies] :seq)]
      (let [fields (filter-policies 'fields policies)
            data   (filter-policies 'data policies)]
        (map (covers fields) data))
    :else (map validateFields program)))


