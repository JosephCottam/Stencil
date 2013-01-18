;;http://cemerick.com/2009/12/04/string-interpolation-in-clojure/
(ns stencil.emitters.cdx
  (:require [clojure.core.match :refer (match)])
  (:require [stencil.transform :as t])
  (:require [clojure.java.io :as io])
  (import (org.stringtemplate.v4 ST STGroup STGroupFile)))

(defn drop-metas [program] 
  (cond
    (t/atom? program) program
    (empty? program) program
    (seq? program) (map drop-metas (remove t/meta? program))))

(defn pyName [name]
  (symbol (.replaceAll (str name) "-|>|<|\\?|\\*" "_")))

(defn render [atts]
  (let [g (STGroupFile. "src/stencil/emitters/cdx.stg")
        t (.getInstanceOf g "program")
        fix (fn [key] (if (string? key) key (subs (str key) 1)))
        atts (zipmap (map fix (keys atts)) (vals atts))
        i (reduce (fn [t [k v]] (.add t k v)) t atts)]
    (.render t)))

(defn table-atts[table]
  (let [name (pyName (second table))
        fields (rest (drop-metas (first (t/filter-policies 'fields table))))
        renderDef (drop-metas (first (t/filter-policies 'render table)))
        scatter (= "SCATTER" (.toUpperCase (str (second renderDef)))) 
        pairs (t/lop->map (rest (nth renderDef 2)))
        x (pairs 'x)
        y (pairs 'y)
        color (pairs 'color)]
    {:tableName name, :fields fields, :xatt x, :yatt y,
     :colorAtt color, :scatter scatter}))

(defn class-atts [program] 
  (let [runtime (first (filter #(and (seq? %) 
                                     (= 'import (first %)) 
                                     (> (.indexOf (.toUpperCase (str (second %))) "RUNTIME") -1))
                        program))
        harness ((t/meta->map (nth runtime 2)) 'harness)
        harness (and (not (nil? harness)) (= true harness))]
    {:className (pyName (second program)), :testharness harness}))

(defn cdx 
  ([file program] 
   (with-open [wrtr (io/writer file)]
     (.write wrtr (cdx program))))
  ([program]
    (let [class-atts (class-atts program)
          table-atts (table-atts (first (t/filter-policies 'table program)))
          atts (conj class-atts table-atts)]
      (render atts))))
