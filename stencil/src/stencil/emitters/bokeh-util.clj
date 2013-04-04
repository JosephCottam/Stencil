(ns stencil.emitters.bokeh
  "Preps a normal-form tree for emit to bokeh."
  (:use stencil.util)
  (:require [clojure.core.match :refer (match)])
  (:require [stencil.transform :as t :refer (atom? full-drop filter-tagged)]))

(defn when->init [program]
  "Takes when clauses predicated on init and covnerts them to init clauses.
   This largely undoes an earlier transformation, but removes many special cases in-between."
  (match program
    (a :guard atom?) a
    (['when- (m1 :guard meta?) (['$init? (m2 :guard meta?)] :seq) gen] :seq) (list 'init gen)
    :else (map when->init program)))


(defn remove-empty-using [program]
  "Removes 'using' statements which bind no fields"
  (match program
    (a :guard atom?) a
    (['using (m1 :guard meta?) (['fields (m2 :guard meta?)] :seq) (gen :guard empty?) body] :seq) body
    :else (map remove-empty-using program)))

(defn dataTuple->store [program]
  "Data statements eventually end in a ptuple or ptuples statement.
   Coverts those statements to a coresponding python function to store the tuple.
  TODO: Extend for 'operator' context and 'update'/'join'/'leave' policies.  Current code ASSUMES 'data' context only."
  (match program
    (a :guard atom?) a
    (['tuple (m :guard meta?) & args] :seq)
    `(~'self.datum ~m ~@args)
    (['ptuple (m :guard meta?) fields & args] :seq)
    `(~'self.datum ~m ~@args)
    (['tuples (m :guard meta?) & args] :seq)
    `(~'self.data ~m ~@args)
    (['ptuples (m :guard meta?) fields & args] :seq)
    `(~'self.data ~m ~@args)
    :else (map dataTuple->store program)))


(defn runtime? [item] 
  "Is the item a runtime import?"
  (and (seq? item) 
       (= 'import (first item))
       (> (.indexOf (.toUpperCase (str (full-drop item))) "RUNTIME") -1)))

(defn runtime [program]
  (map #(if (runtime? %)
          (let [[_ package & rest] %]
            `(~'runtime ~package ~@rest))
          %)
       program))

(defn clean-to-false [item]
  "Removes metadata items and returns false if the item is then empty"
  (let [item (rest (remove meta? item))
        item (if (empty? item) false item)]
    item))

(defn py-imports [program]
  (let [imports (->> program 
                  (t/filter-tagged 'import)
                  (filter #(.startsWith (str (nth % 2)) "py-"))
                  (map (fn [[_ _ package & policies]] 
                         (let [package (symbol (.substring (str package) 3))
                               as (clean-to-false (first (filter-tagged 'as policies)))
                               items (clean-to-false (first (filter-tagged 'items policies)))
                               items (if (and items (any= 'ALL items)) '(*) items)]
                         (list 'import package as items)))))
        rest (filter-tagged #(not (= %1 %2)) 'import (drop 4 program))]
    `(~@(take 4 program) ~@imports ~@rest)))

(defn guide-args [program]
  (match program
    (a :guard atom?) a
    (['guide (m0 :guard meta?) (target :guard symbol?) (m1 :guard meta?) (type :guard symbol?) (m2 :guard meta?)] :seq)
      (let [args (t/dissoc-tlop (reduce-metas m2) 'type)
            args (cons 'args (rest args))]
      `(~'guide ~m0 ~target ~m1 ~type ~m2 ~(interpose '($meta) args)))
    :else (map guide-args program)))

(defn quote-strings [program]
  "Places quotation marks around strings."
  (match program
    (s :guard string?) (str "\"" s "\"")
    (a :guard atom?) a
    :else (map quote-strings program)))

(defn tagged-dictionary [item]
  "Converts nested lists of tag/item sets into nested dictionaries
  '(type tag value) --> {is<type>: true, tag : value}
  '(tag x y) --> {tag : (x y)}
  '(tag1 x (tag2 y)) --> {tag :x, tag2: y}"
  ;;TODO: IMPLEMENT...might replace a lot of boilerplate in the emitter if done right
  item)   



