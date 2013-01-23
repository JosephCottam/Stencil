(ns stencil.transform
  "Work with the 'fields' statements found throught the program.")

(defn stream-or-table? [v] (or (= 'stream v) (= 'table v)))

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
        (second-expr expr)    ;;acquire the fields statement
      ;`(~'fields (~'$meta) ~@(second-expr (second-expr expr)));;get rid of the operator and the quote
     :else (expr->fields saved (last expr)))))

(defn ensure-fields
  "Ensure there is a 'fields' policy in each table and stream.
   If there is not currently a 'fields' policy, generates on based on the first 'data' policy found.
   After execution, fields policy will include type and default value in its metadata."
  [program]
  (match [program]
    [a :guard atom?] a
    [([(tag :guard stream-or-table?) (name :guard symbol?) (meta :guard meta?) & policies] :seq)]
        (if (not (empty? (filter-tagged 'fields policies)))
          `(~tag ~name ~meta ~@policies)
          (let [data (first (filter-tagged 'data policies)) ;;TODO: Generalize to arbitrary number of datas.
                fields (expr->fields (full-drop data))]
            `(~tag ~name ~meta ~fields ~@policies)))
    :else (map ensure-fields program)))
      

;;;------------------------------------------------------------------------------------------------------------


(defn check-fields-cover-data [program]
  "Test if the 'fields' policy covers the names used in all of the 'data' policies.
   'fields' may include names NOT in a 'data', if that field also has a default value."
  (letfn [(covers [fields-policy]
            (let [fields (set (remove meta? (rest (first fields-policy))))]
              (fn [data] 
                (clojure.set/subset? fields (set (remove meta? (rest (expr->fields data))))))))]
    (match [program]
      [a :guard atom?] a
      [([(tag :guard stream-or-table?) (n :guard symbol?) (m :guard meta?) & policies] :seq)]
        (let [fields (filter-tagged 'fields policies)
              data   (filter-tagged 'data policies)]
          (if (every? #(= true %) (map (covers fields) data))
            `(~tag ~n ~m ~@(check-fields-cover-data policies))
              (throw (RuntimeException. (str "Fields statement does not cover data statement: " program)))))
      :else (map check-fields-cover-data program))))


(defn check-simple-fields [program]
  "Test if the 'fields' statements only have symbol entries (and metas).
   Throws exception on failure, or unchanged tree."
   (match [program]
     [a :guard atom?] a
     [(['fields & rest] :seq)]
       (if (every? #(or (symbol? %) (meta? %)) rest)
         program
         (throw (RuntimeException. (str "Fields statement with illegal entry: " program))))
     :else 
       (map check-simple-fields program)))


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
      (let [fields   (rest (first (filter-tagged 'fields policies)))
            source (lop->map (rest (first (filter-tagged policy-tag policies))))
            inner (map folder policies)
            reduced (filter-tagged (complement any=) (list policy-tag 'fields) policies)
            fields (cons 'fields (extendFields fields source))]
          `(~tag ~name ~meta ~fields ~@reduced))
      :else (map folder program)))))


(defn defaults->fields [program]
  "TODO: By moving the these items into the meta, they are assumed to be CONSTANTS.  
  Extend to handle statically evaluatable expressions."
  ((fold-into-fields 'defaults 'default) program))

(defn display->fields [program] ((fold-into-fields 'display 'display) program))



;;;------------------------------------------------------------------------------------------------------------
(defn normalize-fields [program]
  (letfn [(ensure-display [name meta] 
           (if (not (contains? meta 'display)) 
             (assoc meta 'display (str name))
             meta))]
    (match [program]
      [a :guard atom?] a
      [(['fields (fmeta :guard meta?) & defs] :seq)] 
        (let [names (take-nth 2 defs)
              metas (take-nth 2 (rest defs))
              metas (map #(map->meta (ensure-display %1 (meta->map %2))) names metas)]
          `(~'fields ~fmeta ~@(interleave names metas)))
      :else (map normalize-fields program))))




