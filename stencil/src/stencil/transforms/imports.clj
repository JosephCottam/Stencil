(ns stencil.transform
  "Manipulate imports.")

(def ^:dynamic *default-runtime* 'BokehRuntime)

(defn normalize-imports [program]
  (match program
    (a :guard atom?) a
    (['import package (m :guard meta?) & rest] :seq)
       (let [as (filter-tagged 'as rest)
             _  (if (> (count as) 1) (throw (RuntimeException. (str "Import statement with " (count as) " 'as' clauses when only one is allowed."))))
             as (if (empty? as) '(as) (first as))
             items (filter-tagged 'items rest)
             _  (if (> (count items) 1) (throw (RuntimeException. "Import statement with more than one 'items' clause.")))
             items (if (empty? items) '(items) (first items))]
         `(~'import ~package ~m ~as ~items))
    :else (map normalize-imports program)))

(defn runtime [program]
  "What is the runtime being imported?
  TODO: Something more elegant than look for an import with 'runtime' as a substring."
  (filter
    #(> (.indexOf (.toUpperCase (str (first (full-drop %)))) "RUNTIME") -1)
    (filter-tagged 'import program)))

(defn ensure-runtime-import [program] 
   "Ensure that there is a runtime in the imports list."
   (letfn
     [(has-runtime? [program] (not (empty? (runtime program))))
      (fragment? [program]
        "A fragment does not start with (stencil ...)"
        (not (= 'stencil (first program))))
      (add-runtime [defaultRuntime program]
        "Add the default runtime import to program."
        (concat (take 2 program) `((~'import ~defaultRuntime)) (drop 2 program)))]
   (if (or (has-runtime? program) (fragment? program))
      program
      (add-runtime *default-runtime* program))))
