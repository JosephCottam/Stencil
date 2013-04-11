(ns stencil.emitters.vega
  (:use [stencil.util])
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
                (symbol? item) (list '$vega-field source item)
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
  (match program
    (a :guard t/atom?) a
    (['operator m0 name & policies] :seq)
      `(~'$vega-scale ~m0 ~name ~policies)
    :else (map scale-defs program)))


(defn guides [program]
  "Lift guide declarations out of their render statements, bringing relevant context with them.
  Gather all into one place."
  (letfn [(gather [program]
            (let [renders (t/filter-tagged 'render program)
                  guides (flatten (map #(t/filter-tagged 'guide %) renders))]
              guides))
          (delete [program]
            (match program
              (a :guard t/atom?) a
              (['render & rest] :seq) (cons 'render (remove #(and (seq? %) (= 'guide (first %))) rest))
              :else (map delete program)))
          (update [program guides]
            (let [types (map second (remove meta? guides))     ;;The type is "x" or "y" right now...will probably change in the future
                  scales (map #(symbol (str % "scale")) types) ;;HACK!!!!!! Relies on scales being named "xscale" for x-axis, etc REALLY NEED TO:Determine the scale that was used in source->data->field-binding
                  args (map drop 2 (remove meta? guides))
                  axes (map (fn[type scale args] `(~'axis (~'type `~type) (~'scale ~scale) ~@args)) types scales args)]
              (concat program (list 'guides axes))))]
    (update (delete program) (gather program))))
               
(defn top-level-defs [program]
  (let [view (first (t/filter-tagged 'view program))
        canvas (filter meta? (first (t/filter-tagged 'canvas view)))
        width (list 'width (second canvas))
        height (list 'height (nth canvas 2))
        pad (first (t/filter-tagged 'padding view))]
    (concat program (list width height pad)))) 

(defn pod [program]
  "Convert lists to dictionaries and lists."
  (match program
    (a :guard t/atom?) a
    (['ptuple fields values] :seq) (zipmap (rest fields) (pod values))
    ([(tag :guard symbol?) item] :seq) #{tag (pod item)}
    :else (map pod program)))

;(defn emit-vega [template]
;  (let [g (STGroupFile. "src/stencil/emitters/vega.stg")
;        t (.getInstanceOf g template)]
;    (.render (.add t "program" "def"))))

(defn emit-vega [program] (stencil.pprint/spp program) program)

(defn emit [program]
    (-> program 
      propogate-source
      scale-defs
;      scale-uses     Look at where the scale is used.  If "domain" is not defined, define it based on its use.
      guides         
      top-level-defs
      remove-metas
      pod
      emit-vega))
