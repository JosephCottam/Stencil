(ns stencil.emitters.bokeh
  "Preps a normal-form tree for emit to bokeh."
  (:require [clojure.core.match :refer (match)])
  (:require [stencil.transform :as t]))

(defn runtime [program]
  (let [imports (t/filter-tagged 'import program)
        runtime (first (filter #(> (.indexOf (.toUpperCase (str (second %))) "RUNTIME") -1) imports))]
    runtime))

(defn when->init [program]
  "Takes when clauses predicated on init and covnerts them to init clauses.
   This largely undoes an earlier transformation, but removes many special cases in-between."
  (match program
    (a :guard t/atom?) a
    (['when- (m1 :guard t/meta?) (['$init? (m2 :guard t/meta?)] :seq) gen] :seq) (list 'init gen)
    :else (map when->init program)))


(defn remove-empty-using [program]
  "Removes 'using' statements which bind no fields"
  (match program
    (a :guard t/atom?) a
    (['using (m1 :guard t/meta?) (['fields (m2 :guard t/meta?)] :seq) (gen :guard empty?) body] :seq) body
    :else (map remove-empty-using program)))









