(in-ns 'stencil.transform)

(defn stream-or-table? [v] (or (= 'stream v) (= 'table v)))

(defn filter-policies 
  "[test,] condition, policy* -> policy*: Filter a list of policies per the test and condition. 
  Test is invoked once for each policy in policy*.  Default test is '='"
  ([condition policies] (filter-policies = condition policies))
  ([test condition policies] (filter #(test (first %) condition) policies)))

(defn decl->fields
 "A 'declaration' is a set of names and meta-data.  
 It is essentially syntactically REQUIRED meta-data, where true $meta statements are optional in the source syntax.
 A 'fields' statement is a list of field names, with meta-data for machine type, default value and display name."
 [decl]
  (letfn [(ensure-display [name meta] 
           (if (not (contains? meta 'display)) 
             (assoc meta 'display (str name))
             meta))]
    (let [names (take-nth 2 decl)
          metas (take-nth 2 (rest decl))
          metas (map #(map->meta (ensure-display %1 (meta->map %2))) names metas)]
      (list 'fields (interleave names metas)))))

(defn expr->fields
  "Convert an expression to a list of fields.
   The first argument is used to produce error messages only."
  ([expr] (expr->fields expr expr))
  ([saved expr]
   (cond 
    (or (atom? expr) (empty? expr))  
       (throw (RuntimeException. (str "Could not find prototyped tuple in " (first saved) "...")))
    (or (= '$ptuples (first expr)) 
        (= '$ptuple (first expr)))
       (decl->fields (second-expr (second-expr expr)))  ;;get rid of the operator and the quote
    :else (expr->fields saved (last expr)))))

(defn ensure-fields
  "Ensure there is a 'fields' policy in each table and stream.
   If there is not currently a 'fields' policy, generates on based on the first 'data' policy found.
   After execution, fields policy will include type and default value in its metadata."
  [program]
  (match [program]
    [a :guard atom?] a
    [([(tag :guard stream-or-table?) (name :guard symbol?) (meta :guard meta?) & policies] :seq)]
        (if (not (empty? (filter-policies 'fields policies)))
          `(~tag ~name ~meta ~@policies)
          (let [data (first (filter-policies 'data policies)) ;;TODO: Generalize to arbitrary number of datas.
                fields (expr->fields (full-drop data))]
            `(~tag ~name ~meta ~fields ~@policies)))
    :else (map ensure-fields program)))
      

;;;------------------------------------------------------------------------------------------------------------
(defn covers [fields-policy]
 (let [fields (set (map first (full-drop (first fields-policy))))]
  (fn [data] 
     (clojure.set/subset? fields (map first (rest (expr->fields data)))))))

(defn validate-fields
  "Test if the 'fields' policy covers the names used in all of the 'data' policies.
   'fields' may include names NOT in a 'data', if that field also has a default value."
  [program]
  (match [program]
    [a :guard atom?] a
    [([(tag :guard stream-or-table?) (name :guard symbol?) (meta :guard meta?) & policies] :seq)]
      (let [fields (filter-policies 'fields policies)
            data   (filter-policies 'data policies)]
        (map (covers fields) data))
    :else (map validate-fields program)))

;;;------------------------------------------------------------------------------------------------------------
(defn fold-into-fields
  "Merge a policy statement into fields statement.  Assumes that there is a fields statement.
  If there is a conflict between elements already present and supplied defauts statement, the new  value wins."
  [policy-tag meta-tag]
  (letfn 
    [(extendWith [items]
      (fn [field meta]
       (if (contains? items field)
           (map->meta (assoc (meta->map meta) meta-tag (items field)))
            meta)))

     (extendFields [fields items]
      (if (or (= 0 (count fields)) (= 0 (count items)))
          fields
          (let [names (take-nth 2 fields)
                metas (take-nth 2 (rest fields))]
            (interleave names (map (extendWith items) names metas)))))]

  (fn folder [program]
    (match [program]
      [a :guard atom?] a 
      [([(tag :guard stream-or-table?) (name :guard symbol?) (meta :guard meta?) & policies] :seq)]
      (let [fields   (rest (first (filter-policies 'fields policies)))
            source (lop->map (rest (first (filter-policies policy-tag policies))))
            inner (map folder policies)
            reduced (filter-policies (complement any=) (list policy-tag 'fields) policies)
            fields (cons 'fields (extendFields fields source))]
          `(~tag ~name ~meta ~fields ~@reduced))
      :else (map folder program)))))


(defn defaults->fields [program]
  "TODO: By moving the these items into the meta, they are assumed to be CONSTANTS.  
  Extend to handle statically evaluatable expressions."
  ((fold-into-fields 'defaults 'default) program))

(defn display->fields [program] ((fold-into-fields 'display 'display) program))
