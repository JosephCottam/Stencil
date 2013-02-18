(ns stencil.emitters.bokeh
  "Preps a normal-form tree for emit to bokeh."
  (:require [clojure.core.match :refer (match)])
  (:require [stencil.transform :as t]))

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

(defn dataTuple->store [program]
  "Data statements eventually end in a ptuple or ptuples statement.
   Coverts those statements to a coresponding python function to store the tuple.
  TODO: Extend for 'operator' context and 'update'/'join'/'leave' policies.  Current code ASSUMES 'data' context only."
  (match program
    (a :guard t/atom?) a
    (['tuple (m :guard t/meta?) & args] :seq)
    `(~'self.datum ~m ~@args)
    (['ptuple (m :guard t/meta?) fields & args] :seq)
    `(~'self.datum ~m ~@args)
    (['tuples (m :guard t/meta?) & args] :seq)
    `(~'self.data ~m ~@args)
    (['ptuples (m :guard t/meta?) fields & args] :seq)
    `(~'self.data ~m ~@args)
    :else (map dataTuple->store program)))


(defn runtime? [item] 
  "Is the item a runtime import?"
  (and (seq? item) 
       (= 'import (first item))
       (> (.indexOf (.toUpperCase (str (second item))) "RUNTIME") -1)))

(defn runtime [program]
  (map #(if (runtime? %)
          (let [[_ package & rest] %]
            `(~'runtime ~package ~@rest))
          %)
       program))

(defn clean-to-false [item]
  (let [item (rest (remove t/meta? item))
        item (if (empty? item) false item)]
    item))

(defn py-imports [program]
  (let [imports (->> program 
                  (t/filter-tagged 'import)
                  (filter #(.startsWith (str (second %)) "py-"))
                  (map (fn [[i package m as items]] 
                         (let [package (.substring (str package) 3)
                               as (clean-to-false as) 
                               items (clean-to-false items)] 
                         (list 'import  package m as items)))))
        rest (t/filter-tagged #(not (= %1 %2)) 'import program)]
    `(~@(take 2 program) ~@imports ~@rest)))


                  

         




