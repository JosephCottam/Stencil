;;http://cemerick.com/2009/12/04/string-interpolation-in-clojure/
(ns stencil.emitters.bokeh
  (:require [clojure.core.match :refer (match)])
  (:require [stencil.transform :as t])
  (:require [clojure.java.io :as io])
  (import (org.stringtemplate.v4 ST STGroup STGroupFile)))

(deftype Table [name ofClass fields depends])
(deftype Depends [source fields expr])
(deftype View [name renders])
(deftype Render [name source type binds fields])
(deftype Header [name debug])
(deftype Program [header tables view])

(deftype When [isWhen trigger action])
(deftype Let [isLet bindings body])
(deftype Using [isUsing fields gen body])
(deftype Prim [isPrim val])  ;;I want default values...atom should always be 'true'
(deftype Do [isDo exprs])
(deftype Op [isOp op rands])
(deftype If [isIf test conseq alt])

(deftype LetBinding [vars expr])
(deftype RenderBinding [x y color])

(declare expr-att)

(defn drop-metas [program] 
  (cond
    (t/atom? program) program
    (empty? program) program
    (seq? program) (map drop-metas (remove t/meta? program))))

(defn pyName [name]
  (symbol (.replaceAll (str name) "-|>|<|\\?|\\*" "_")))
(defn pyVal [val]
  (cond
    (string? val) (str "\"" val "\"")
    :else val))

(defn class-name [name] (str name "__"))
(defn bind-att [[varset expr]] 
  (LetBinding. (map expr-att varset) (expr-att expr)))

(defn expr-att [expr]
  (match expr
    (a :guard t/atom?) (Prim. true (pyVal a))
    (['let bindings body] :seq) 
      (Let. true (map bind-att bindings) (expr-att body))
    ([do & exprs] :seq) (Do. true (map expr-att exprs))
    ([op & rands] :seq) (Op. true op (map expr-att rands))
    ([if test conseq alt] :seq)
       (If. true 
            (expr-att test) 
            (expr-att conseq) 
            (if (empty? alt) false (expr-att alt)))
    :else (throw (RuntimeException. "Unandled expression: " expr))))

(defn data-atts [data]
  (let [[tag when] (drop-metas data)
        [tag trigger using] when
        ;;Currently ignoring the trigger...assume everything is on init
        [tag fields gen trans] using
        [tag source] gen] ;;Assumes that this is an "items" expression
    (Depends. source (t/full-drop fields) (expr-att trans))))

(defn table-atts[table]
  (let [name (pyName (second table))
        fields (rest (drop-metas (first (t/filter-tagged 'fields table))))
        data (t/filter-tagged 'data table)
        external (empty? data)]
    (if external
      (Table. name (class-name name) fields false)
      (Table. name (class-name name) fields (map data-atts data)))))

(defn render-bind-atts [bind]
  (let [pairs (t/lop->map (rest (drop-metas bind)))
        x (pairs 'x)
        y (pairs 'y)
        color (pairs 'color)]
    (RenderBinding. x y color)))

(defn render-atts [[_ name _ source _ type _ & args]]
  (if (= 'table type)
    (Render. (pyName name) source type false (rest (first (drop-metas args))))
    (Render. (pyName name) source type (map render-bind-atts args) false)))

(defn view-atts [render-defs [_ name _ & renders]]
  (let [render-defs (map render-atts render-defs)
        render-defs (zipmap (map #(.name %) render-defs) render-defs)
        renders (map render-defs (drop-metas renders))]
  (View. (pyName name) renders)))
 
(defn as-atts [program]
  (let [name (second program)
        imports (t/filter-tagged 'import program)
        view   (first (t/filter-tagged 'view program)) ;;TODO: Expand emitter to multiple views
        renders (t/filter-tagged 'render program)
        tables  (t/filter-tagged 'table program)
        runtime (first (filter #(> (.indexOf (.toUpperCase (str (second %))) "RUNTIME") -1) imports))
        debug ((t/meta->map (nth runtime 2)) 'debug)
        debug (and (not (nil? debug)) (= true debug))]
    (Program. (Header. (pyName name) debug) (map table-atts tables) (view-atts renders view))))


(defn emit-bokeh [template attlabel atts]
  (let [g (STGroupFile. "src/stencil/emitters/bokeh.stg")
        t (.getInstanceOf g template)]
    (.render (.add t attlabel atts))))

(defn emit [program]
  (emit-bokeh "program" "def" (as-atts program)))



