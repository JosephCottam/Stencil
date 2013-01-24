;;http://cemerick.com/2009/12/04/string-interpolation-in-clojure/
(ns stencil.emitters.cdx
  (:require [clojure.core.match :refer (match)])
  (:require [stencil.transform :as t])
  (:require [clojure.java.io :as io])
  (import (org.stringtemplate.v4 ST STGroup STGroupFile)))

(deftype Table [name fields data])
(deftype Render [name source x y color scatter])
(deftype Header [name debug])
(deftype Program [header tables renders])
(deftype Expr [op rands])
(deftype Atom [val atom])  ;;I want default values...atom should always be 'true'


(defn drop-metas [program] 
  (cond
    (t/atom? program) program
    (empty? program) program
    (seq? program) (map drop-metas (remove t/meta? program))))

(defn pyName [name]
  (symbol (.replaceAll (str name) "-|>|<|\\?|\\*" "_")))

(defn table-atts[table]
  (let [name (pyName (second table))
        fields (rest (drop-metas (first (t/filter-tagged 'fields table))))
        data (first (t/filter-tagged 'data table))]
    (Table. name fields "<data clause>")))

(defn render-atts [[_ name _ source _ type _ binds]]
  (let [scatter (= "SCATTER" (.toUpperCase (str type))) 
        pairs (t/lop->map (rest (drop-metas binds)))
        x (pairs 'x)
        y (pairs 'y)
        color (pairs 'color)]
    (Render. (pyName name) source x y color scatter)))

(defn as-atts [program]
  (let [name (second program)
        imports (t/filter-tagged 'import program)
        renders (t/filter-tagged 'render program)
        tables  (t/filter-tagged 'table program)
        runtime (first (filter #(> (.indexOf (.toUpperCase (str (second %))) "RUNTIME") -1) imports))
        debug ((t/meta->map (nth runtime 2)) 'debug)
        debug (and (not (nil? debug)) (= true debug))]
  (Program. (Header. (pyName name) debug) (map table-atts tables) (map render-atts renders))))


(defn emit-cdx [template attlabel atts]
  "Emit to the specified template from teh cdx group."
  (let [g (STGroupFile. "src/stencil/emitters/cdx.stg")
        t (.getInstanceOf g template)]
    (.render (.add t attlabel atts))))

(defn cdx 
  ([file program] 
   (with-open [wrtr (io/writer file)]
     (.write wrtr (cdx program))))
  ([program] (emit-cdx "program" "def" (as-atts program))))



