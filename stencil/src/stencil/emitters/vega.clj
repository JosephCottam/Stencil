(ns stencil.emitters.vega
  (:use [stencil.util])
  (:require [clojure.data.json :as json])
  (:require [clojure.core.match :refer (match)])
  (:require [stencil.transform :as t])
  (:require [stencil.pprint])
  (import (org.stringtemplate.v4 ST STGroup STGroupFile)))


(defn distinguish-unrendered-tables [program]
  "Mark tables that will not be rendered as 'data-table'
  (simplifies later steps)."
  (letfn [(retag [table tag] (cons tag (rest table)))
          (maybe-retag [targets table]
            (if (any= (nth table 2) targets)
              (retag table 'render-table)
              (retag table 'data-table)))]
    (let [renders (t/filter-tagged 'render program)
          tables  (t/filter-tagged 'table program)
          targets (map #(nth % 4) renders)
          tables (map (partial maybe-retag targets) tables)]
      (concat (t/remove-tagged 'table program) tables))))


(defn find-descendant [tag data]
  (let [items (t/filter-tagged tag data)]
    (cond
      (not (seq? data)) nil 
      (empty? items) (mapcat (partial find-descendant tag) data)
      :else (first items))))


(defn ptuples->lop [ptuple]
  (let [[tag fields & items] (remove-metas ptuple)
        fields (rest fields)
        items (map rest items)]
    (partition (count fields) (partition 2 (interleave (cycle fields) (apply interleave items))))))

(defn transform-data [table] 
  (let [data (t/filter-tagged 'data table)
        using (find-descendant 'using data)
        [tag _ fields _ load] using 
        loader (first load)]
    (case loader
      'ptuples (ptuples->lop load) 
      :else (throw (RuntimeException. (str "Loader not know: " loader))))))

(defn transform [table]
  (let [data (transform-data table)
        [_ _ name & rest] table]
    (list (list 'name name) (list 'values data))))

(defn transform-unrendered-tables [program]
    (concat (t/remove-tagged 'data-table program)
           (list (list 'data (map transform (t/filter-tagged 'data-table program))))))

(defn fold-rendered-table [program]
  "Merges the data statement of a table that is attached to a rendering
  into the render statement and deletes the table.
  Currently assumes that each table is only the target of ONE renderer."
  program)


(defn propogate-source [program]
  "Given a when statement, puts qualifies all references to source-values.
   Relies on the assumption that there are only simple transforms"
  (letfn [(source [gen] (second (remove meta? gen))) ;;assumes "(items ...)" or (delta ...) or similar; no compounds
          (labelRefs [source]
            (fn inner [item] 
              (cond
                (meta? item) item
                (symbol? item) (list 'vega-field source item)
                (seq? item) (let [[f m & args] item
                                  vals (map inner (remove meta? args))
                                  metas (filter meta? args)]
                              (list f m (interleave vals metas))))))
          (search [program]
            (match program
              (a :guard t/atom?) a
              (['when (m0 :guard meta?) trigger (m1 :guard meta?) gen (m2 :guard meta?) action] :seq)
                `(~'when ~m0 ~trigger ~m1 ~gen ~m2 ~(label (source gen) action))
              :else (map search program)))
          (label [source program]
            (match program
              (['let (m0 :guard meta?) bindings action] :seq)
                (let [vars (map first bindings)
                      vals (map (labelRefs source) (map second bindings))]
                  `(~'let ~m0 (interleave vars vals) (labelRefs source action)))))]
    (search program)))

(defn scale-defs [program]
  "Transform operator defs into scale definitions; gather all into one place"
  (letfn [(gather [program] (t/filter-tagged 'operator program))
          (clean [[bind m0 key m1 & val]] (list* key m1 val))
          (reform-domain [scale]
            (let [[_ _ domain & rest] (first (t/filter-tagged 'domain scale))
                  [data field] (clojure.string/split (str domain) #"\.")]
              (concat (t/remove-tagged 'domain scale) 
                     `((~'domain ((~'data ~(symbol data)) (~'field ~domain)))))))
          (reform [[_ m0 name m1 & policies]] 
            (let [config (first (t/filter-tagged 'config policies))]
              (list* `(~'name ~name) (reform-domain (t/full-drop config)))))]
    (concat (t/remove-tagged 'operator program) 
      (list (list 'scales (map reform (gather program)))))))

(defn guides [program]
  "Lift guide declarations out of their render statements, bringing relevant context with them.
  Gather all into one place."
  (letfn [(gather [program]
            (let [renders (t/filter-tagged 'render program)
                  guides (reduce concat (map #(t/filter-tagged 'guide %) renders))]
              guides))
          (delete [program]
            (match program
              (a :guard t/atom?) a
              (['render m0 id m1 source m2 type m3 & rest] :seq) 
                (list* 'render m0 id m1 source m2 type m3 (t/filter-tagged not= 'guide rest))
              :else (map delete program)))
          (update [program guides]
            (let [guides (map #(remove meta? %) guides)
                  types (map #(nth % 2) guides)     ;;The type is "x" or "y" right now...will probably change in the future
                  scales (map #(symbol (str % "scale")) types) ;;HACK!!!!!! Relies on scales being named "xscale" for x-axis, etc REALLY NEED TO:Determine the scale that was used in source->data->field-binding
                  args (map (partial drop 3) guides)
                  axes (map (fn[type scale args] `((~'type ~type) (~'scale ~scale) ~@args)) types scales args)]
              (concat program (list (list 'axes axes)))))]
    (update (delete program) (gather program))))

(defn ptuple->lop [[tag _ fields & vals]] 
  (let [f  (take-nth 2 (t/full-drop fields))
        fm (take-nth 2 (drop 1 (t/full-drop fields)))
        v  (take-nth 2 vals)
        vm (take-nth 2 (drop 1 vals))]
    (map list f fm v vm)))

(defn top-level-defs [program]
  (let [view (first (t/filter-tagged 'view program))
        canvas (remove meta? (first (t/filter-tagged 'canvas view)))
        width (list 'width (second canvas))
        height (list 'height (nth canvas 2))
        pad (list 'padding (ptuple->lop (nth (first (t/filter-tagged 'padding view)) 2)))
        view (t/remove-tagged any= '(canvas padding) view)]
    (concat (t/remove-tagged 'view program) (list width height pad view)))) 


(defn select [tag ls] 
  (let [items (t/filter-tagged tag ls)]
    (if (> (count items) 1)
      (throw (RuntimeException. (str "More than one '" tag "' items in program.")))
      (first items))))

(defn remove-imports [program] (remove #(and (seq? %) (= (first %) 'import)) program))

(defn pair? [item] (and (seq? item) (== 2 (count item))))
(defn lop? [item] (and (seq? item) (every? pair? item)
                       (every? t/atom? (map first item))))

(defn pod2 [program]
  (cond
    (t/atom? program) program
    (and (pair? program)
         (t/atom? (first program)))
      {(first program) (pod2 (second program))}
    (lop? program)
     (let [keys (map first program)
           vals (map pod2 (map second program))]
       (zipmap keys vals))
    :else (map pod2 program)))

(defn pod [program]
  ;;When everything works, this should be removed and pod2 should be called directly
  (let [axes (pod2 (select 'axes program))
        scales (pod2 (select 'scales program))
        width (pod2 (select 'width program))
        height (pod2 (select 'height program))
        padding (pod2 (select 'padding program))
        data-tables (pod2 (select 'data program))]
    (reduce into (list axes scales width height padding data-tables))))
          
(defn json [program] (with-out-str (json/pprint program)))
(defn medium [program]
  "Intermediate state to faciliate development."
  (-> program 
      propogate-source
      scale-defs
      ;scale-uses     Look at where the scale is used.  If "domain" is not defined, define it based on its use.
      guides         
      top-level-defs
      distinguish-unrendered-tables
      transform-unrendered-tables
      ))
   

(defn emit [program]
  (-> program
    medium
    remove-metas
    remove-imports
    pod 
    json))
