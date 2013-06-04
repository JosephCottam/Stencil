;;http://cemerick.com/2009/12/04/string-interpolation-in-clojure/
(ns stencil.emitters.bokeh
  (:use [stencil.util :exclude [any=]])
  (:use [stencil.emitters.sequitur])
  (:require [clojure.core.match :refer (match)])
  (:require [stencil.transform :as t])
  (:require [stencil.pprint])
  (import (org.stringtemplate.v4 ST STGroup STGroupFile)))

(load "bokeh-util")

(deftype Program [header tables view])
(deftype Header [name imports])

(deftype Table [name ofClass fields inits depends])
(deftype Depends [source fields expr])

(deftype View [name renders dataranges])
(deftype SimpleRender [simpleRender name source type binds fields])
(deftype GlyphRender [glyphRender name source type shape bindings guides])
(deftype Guide [name type parent target datarange args])
(deftype DataRange [name type source field])
 
;;I want default values...these "is<x>" fields should always be 'true'
(deftype When [isWhen trigger action])
(deftype Let [isLet bindings body])
(deftype Using [isUsing fields gen body])
(deftype Prim [isPrim val]) 
(deftype Do [isDo exprs])
(deftype Op [isOp op rands])
(deftype If [isIf test conseq alt])
(deftype List [isList items])

(deftype LetBinding [vars expr])
(deftype RenderBinding [field default expr])

(declare expr-atts)

(defn dmap [default f vals]
  (if (empty? vals)
    default
    (map f vals)))

(defn pyName [name]
  (symbol (.replaceAll (str name) "-|>|<|\\?|\\*" "_")))

(defn class-name [name] (str name "__"))
(defn data-range-name [field] (str "_" field "_dr_"))
(defn drop-quotes [value] (.trim (clojure.string/replace (.trim value) #"^\"|\"$" "")))

(defn bind-atts [[varset expr]] 
  (LetBinding. (map expr-atts varset) (expr-atts expr)))

(defn expr-atts [expr]
  (match expr
    (a :guard t/atom?) (Prim. true a)
    (['let bindings body] :seq) 
      (Let. true (map bind-atts bindings) (expr-atts body))
    (['do & exprs] :seq) (Do. true (map expr-atts exprs))
    (['list & exprs] :seq) (List. true (map expr-atts exprs))
    ([op & rands] :seq) (Op. true op (map expr-atts rands))
    ([if test conseq alt] :seq)
       (If. true 
            (expr-atts test) 
            (expr-atts conseq) 
            (if (empty? alt) false (expr-atts alt)))
    :else (throw (RuntimeException. "Unhandled expression: " expr))))

(defn init-atts [init] (expr-atts (second init)))

(defn depend-atts [when]
  (let [[tag trigger using] when  ;;Trigger is ignored, currenlty just happends on render
        [tag fields gen trans] using
        [tag source] gen] ;;Assumes that this is an "items" expression
    (Depends. source (rest fields) (expr-atts trans))))

(defn table-atts[[_ name & policies]]
  (let [fields (rest (first (find* 'fields policies)))
        datas (find* 'data policies)
        inits (dmap false init-atts (apply concat (map (partial find* 'init) datas)))
        depends (dmap false depend-atts (apply concat (map (partial find* 'when-) datas)))]
    (Table. name (class-name name) fields inits depends)))

(defn bind-subset [select from & opts]
  (letfn [(is [x] (any= (.field x) select))]
    (let [f (if (any= :not opts) (complement is) is)]
      (filter f from))))

(defn render-bind-atts [[target source]] 
  (let [src (if (symbol? source) (str "\"" source "\"") false)
        default (if (symbol? source) false source)]
  (RenderBinding. target default src)))

(defn guide-att [parent [tag target type args]]
    (Guide. (str target type) 
            type 
            parent 
            target 
            (data-range-name target) 
            (t/lop->map (rest args))))

(defn bokeh-plot-types [type]
  (case (.toLowerCase (str type))
    ("scatterplot" "scatter") 'scatter
    type))

(defn render-atts [[_ name source type & args]]
  (let [type (bokeh-plot-types type)]
    (cond
      (= type 'table) 
        (SimpleRender. true (pyName name) source type false (rest (first args)))
      (any= type '(scatter plot)) 
        (SimpleRender. true (pyName name) source type (map  #(map render-bind-atts (rest %)) args) false)
      (= type 'GlyphRenderer) 
        (let [bind (rest (select* 'bind args))
              renderBindings (map render-bind-atts (remove* 'shape bind))
              shape (drop-quotes (second (select* 'shape bind)))
              guides (find* 'guide args)
              guide-atts (map (partial guide-att source) guides)]
          (GlyphRender. true 
                        (pyName name)
                        source 
                        type 
                        shape
                        renderBindings
                        guide-atts))
      :else (throw (RuntimeException. (str "Unknown render type: " type))))))

(defn datarange-atts [render-def]
  (let [[tag name source & rest] render-def
        guides (find** 'guide render-def)
        guide-deps (map (fn [[tag field type args]] (list source field)) guides)
        guide-deps (distinct guide-deps)]
    (map (fn [[source field]] 
           (DataRange. (data-range-name field) "DataRange1d" source field)) 
         guide-deps)))

(defn view-atts [render-defs [_ name & renders]]
  (let [data-ranges (apply concat (map datarange-atts render-defs))
        render-defs (map render-atts render-defs)
        render-defs (zipmap (map #(.name %) render-defs) render-defs)
        renders (map render-defs renders)]
    (View. (pyName name) renders data-ranges)))

(defn import-atts [[_ package as items]] 
  {"package" package, "as" as, "items" items})
  
(defn as-atts [program]
  (let [name    (second program)
        view    (first (find* 'view program)) ;;TODO: Expand emitter to multiple views
        renders (find* 'render program)
        tables  (find* 'table program)
        imports (find* 'import program)
        runtime (first (find* 'runtime program))]
    (Program. (Header. (pyName name) (map import-atts imports))
              (map table-atts tables)
              (view-atts renders view))))


(defn emit-bokeh [atts]
  (let [url (clojure.java.io/resource "emitters/bokeh.stg")
        g (STGroupFile. url "us-ascii" \< \>)
        t (.getInstanceOf g "program")]
    (.render (.add t "def" atts))))

(defn emit [program]
    (-> 
      program 
      runtime 
      py-imports 
      dataTuple->store 
      title=>binds
      quote-strings 
      when->init 
      remove-empty-using 
      guide-args
      remove-metas
      as-atts
      emit-bokeh))

