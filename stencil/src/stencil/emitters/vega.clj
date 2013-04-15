(ns stencil.emitters.vega
  (:use [stencil.util])
  (:require [clojure.data.json :as json])
  (:require [clojure.core.match :refer (match)])
  (:require [stencil.transform :as t])
  (:require [stencil.pprint])
  (import (org.stringtemplate.v4 ST STGroup STGroupFile)))



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
          (reform [[_ m0 name m1 & policies]] 
            (let [config (first (t/filter-tagged 'config policies))]
              (list* `(~'name ~name) (t/full-drop config))))]
    (concat (t/remove-tagged 'operator program) (list (list* 'scales (map reform (gather program)))))))

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
              (concat program (list (cons 'axes axes)))))]
    (update (delete program) (gather program))))
               
(defn top-level-defs [program]
  (let [view (first (t/filter-tagged 'view program))
        canvas (remove meta? (first (t/filter-tagged 'canvas view)))
        width (list 'width (second canvas))
        height (list 'height (nth canvas 2))
        pad (first (t/filter-tagged 'padding view))
        view (t/remove-tagged any= '(canvas padding) view)]
    (concat (t/remove-tagged 'view program) (list width height pad view)))) 


(defn select [tag ls] 
  (let [items (t/filter-tagged tag ls)]
    (if (> (count items) 1)
      (throw (RuntimeException. (str "More than one '" tag "' items in program.")))
      (first items))))

(defn remove-imports [program] (remove #(and (seq? %) (= (first %) 'import)) program))

(defn pod [program]
  "Convert lists to dictionaries and lists."
  (letfn [(string [s] (str "\"" s "\""))
          (ptuple->map [[_ fields & values]] (zipmap (map str (rest fields)) (pod values)))
          (tlop->map [tlop] (t/lop->map (rest tlop)))
          (pair->map [pair] {(first pair) (second pair)})
          (tlist->tlmap [tlist] 
            (let [label (first tlist)
                  maps (map t/lop->map (rest tlist))]
             {label maps}))]
    (let [axes   (tlist->tlmap (select 'axes program))
          scales (tlist->tlmap (select 'scales program))
          width (pair->map (select 'width program))
          height (pair->map (select 'height program)) ]
      (reduce into (list axes scales width height)))))
          
(defn json [program] (with-out-str (json/pprint program)))

(defn emit [program]
    (-> program 
      propogate-source
      scale-defs
      ;scale-uses     Look at where the scale is used.  If "domain" is not defined, define it based on its use.
      guides         
      top-level-defs
      remove-metas
      remove-imports
      ))
