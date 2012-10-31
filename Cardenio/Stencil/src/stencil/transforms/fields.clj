(in-ns 'stencil.transform)

(defn stream-or-table? [v] (or (= 'stream v) (= 'table v)))

(defn filter-policies 
  "[test,] condition, policy* -> policy*: Filter a list of policies per the test and condition. 
  Test is invoked once for each policy in policy*.  Default test is '='"
  ([condition policies] (filter-policies = condition policies))
  ([test condition policies] (filter #(test (first %) condition) policies)))

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
    [a :guard atom?] a
    [([(tag :guard stream-or-table?) (name :guard symbol?) (meta :guard meta?) & policies] :seq)]
      (let [fields (filter-policies 'fields policies)
            fields (if (empty? fields) 
                     (expr->fields (ffirst (filter-policies 'data policies)))
                     fields)]
        `(~tag ~name ~fields ~meta ~@policies))
    :else (map ensureFields program)))
      

;;;------------------------------------------------------------------------------------------------------------
(defn validateFields
  "Test if the 'fields' policy covers the names used in all of the 'data' policies.
   'fields' may include names NOT in a 'data', if that field also has a default value."
  [program]

  (defn covers [fields]
    (let [fields (set (keys fields))]
      (fn [data] 
        (clojure.set/subset? fields (meta-keys (expr->fields (ffirst data)))))))

  (match [program]
    [a :guard atom?] a
    [([(tag :guard stream-or-table?) (name :guard symbol?) (meta :guard meta?) & policies] :seq)]
      (let [fields (filter-policies 'fields policies)
            data   (filter-policies 'data policies)]
        (map (covers fields) data))
    :else (map validateFields program)))

;;;------------------------------------------------------------------------------------------------------------
(defn defaults->fields
  "Merge defaults statement into fields statement.  Assumes that there is a fields statement.
  If there is a conflict between defaults already present and supplied defauts statement, supplied statement wins.
  TODO: By moving the defaults into the meta, they are assumed to be CONSTANTS.  
  Extend to handle statically evaluatable expressions."
  [program]

  (defn extendWith [defaults]
    (fn [field meta]
      (if (contains? defaults field)
        (map->meta (assoc (meta->map meta) 'default (defaults field)))
         meta)))

  (defn extendFields [fields defaults]
    (if (or (= 0 (count fields)) (= 0 (count defaults)))
      fields
      (let [names (take-nth 2 fields)
            metas (take-nth 2 (rest fields))]
        (interleave names (map (extendWith defaults) names metas)))))
  
  (match [program]
    [a :guard atom?] a 
    [([(tag :guard stream-or-table?) (name :guard symbol?) (meta :guard meta?) & policies] :seq)]
      (let [fields   (rest (first (filter-policies 'fields policies)))
            defaults (lop->map (rest (first (filter-policies 'defaults policies))))]
          (let [inner (map defaults->fields policies)
                reduced (filter-policies (complement any=) '(defaults fields) policies)
                fields (cons 'fields (extendFields fields defaults))]
          `(~tag ~name ~meta ~fields ~@reduced)))
    :else (map defaults->fields program)))


