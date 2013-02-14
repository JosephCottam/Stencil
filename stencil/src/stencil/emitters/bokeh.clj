;;http://cemerick.com/2009/12/04/string-interpolation-in-clojure/
(ns stencil.emitters.bokeh
  (:require [clojure.core.match :refer (match)])
  (:require [stencil.transform :as t])
  (:require [clojure.java.io :as io])
  (:require [stencil.pprint])
  (import (org.stringtemplate.v4 ST STGroup STGroupFile)))

(load "bokeh-util")

(deftype Table [name ofClass fields inits depends])
(deftype Depends [isDepend source fields expr])
(deftype View [name renders])
(deftype Render [name source type binds fields])
(deftype Header [name imports literal])
(deftype Import [package as items])
(deftype Program [header tables view])
(deftype Init [isInit expr])
 
;;I want default values...these "is<x>" fields should always be 'true'
(deftype When [isWhen trigger action])
(deftype Let [isLet bindings body])
(deftype Using [isUsing fields gen body])
(deftype Prim [isPrim val]) 
(deftype Do [isDo exprs])
(deftype Op [isOp op rands])
(deftype If [isIf test conseq alt])

(deftype LetBinding [vars expr])
(deftype RenderBinding [x y color])

(declare expr-atts)

(defn dmap [default f vals]
  (if (empty? vals)
    default
    (map f vals)))

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
(defn bind-atts [[varset expr]] 
  (LetBinding. (map expr-atts varset) (expr-atts expr)))

(defn expr-atts [expr]
  (match expr
    (a :guard t/atom?) (Prim. true (pyVal a))
    (['let bindings body] :seq) 
      (Let. true (map bind-atts bindings) (expr-atts body))
    (['$do & exprs] :seq) (Do. true (map expr-atts exprs))
    ([op & rands] :seq) (Op. true op (map expr-atts rands))
    ([if test conseq alt] :seq)
       (If. true 
            (expr-atts test) 
            (expr-atts conseq) 
            (if (empty? alt) false (expr-atts alt)))
    :else (throw (RuntimeException. "Unhandled expression: " expr))))

(defn init-atts [init] (Init. true (expr-atts (second init))))

(defn depend-atts [when]
  (let [[tag trigger using] when  ;;Trigger is ignored, currenlty just happends on render
        [tag fields gen trans] using
        [tag source] gen] ;;Assumes that this is an "items" expression
    (Depends. true source (t/full-drop fields) (expr-atts trans))))

(defn table-atts[table]
  (let [name (pyName (second table))
        fields (rest (drop-metas (first (t/filter-tagged 'fields table))))
        datas (t/filter-tagged 'data table)
        inits (dmap false init-atts (drop-metas (reduce cons (map (partial t/filter-tagged 'init) datas))))
        depends (dmap false depend-atts (drop-metas (reduce cons (map (partial t/filter-tagged 'when-) datas))))]
    (Table. name (class-name name) fields inits depends)))

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

(defn import-atts [[_ package _ as items]]
  (let [package (.substring (str package) 3)  ;;remove the 'py-'
        as      (if (empty? (t/full-drop as)) false (t/full-drop (remove t/meta? as)))
        items   (if (empty? (t/full-drop items)) false (t/full-drop (remove t/meta? items)))]
    (Import. package as items)))
  

(defn as-atts [program]
  (let [name (second program)
        view    (first (t/filter-tagged 'view program)) ;;TODO: Expand emitter to multiple views
        renders (t/filter-tagged 'render program)
        tables  (t/filter-tagged 'table program)
        imports (map import-atts (filter #(.startsWith (str (second %)) "py-") (t/filter-tagged 'import program)))
        runtime (runtime program)
        literal ((t/meta->map (nth runtime 2)) 'header)
        literal (if (nil? literal) false literal)]
    (Program. (Header. (pyName name) imports literal) 
              (map table-atts tables)
              (view-atts renders view))))


(defn emit-bokeh [template attlabel atts]
  (let [g (STGroupFile. "src/stencil/emitters/bokeh.stg")
        t (.getInstanceOf g template)]
    (.render (.add t attlabel atts))))

(defn emit [program]
  (emit-bokeh "program" "def" 
    (-> program  dataTuple->store when->init remove-empty-using as-atts)))



