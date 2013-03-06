(ns stencil.test.emit
  (:require [stencil.emitters.bokeh :as bokeh])
  (:require [stencil.core :as c])
  (:use [clojure.test]))


(defn freshDir [root] 
  (let [now (java.util.Date.) 
        rep (java.text.SimpleDateFormat.  "yyyyMMMdd--kk_mm")
        root (if (.endsWith root "/") root (str root "/")) 
        path (str root (.format rep now))]
    path))

(def source "../tests")
(def target (freshDir "../testResults"))

(defn py-clean [p]
  "Attempts to make two superficially dis-similar python programs equal.
   Does so by removing trailing white-space and removing the numbers from things 
   that could-be gensyms (according to our gensym-convention of word-followed-by-underscore)."
  (-> p
    (lojure.string/replace #"(?m)(\s+)(\n)" "$2")
    (clojure.string/replace #"([\w&&]+?)_\d+(\W)" "$1$2")))

(defn emit [emitter src rslt]
  "Run the emitter on the src program, write it to the result location and return the result."
  (let [program (emitter (c/compile src :file))
        rslt (java.io.File. rslt)
        parent (.getParentFile rslt)
        symTarget (java.io.File. (.getParent (java.io.File. target)) "current")
        symSource (java.io.File. target)]
    (.mkdirs parent)
    (if (.exists symTarget) (.delete symTarget))
    (java.nio.file.Files/createSymbolicLink (.toPath symTarget) (.toPath symSource) (make-array java.nio.file.attribute.FileAttribute 0))
    (spit rslt program)
    program))

(defmethod assert-expr 'emit-pyeq? [msg form]
  `(let [emitter# ~(nth form 1)
         base# ~(nth form 2)
         src# (str source "/" base# ".stencil")
         ref# (str source "/" base# ".py")
         rslt# (str target "/" base# ".py")
         result# (.trim (emit emitter# src# rslt#))
         expected# (.trim (slurp ref#))]
     (if (= (py-clean result#) (py-clean expected#))
       (report {:type :pass :message ~msg, :expected ref# :actual rslt#})
       (report {:type :fail :message (str "Compile did not match --- " ~msg), :expected ref# :actual rslt#}))
     result#))


(deftest bokeh
    (is (emit-pyeq? bokeh/emit "bokeh/scatterplot-inline")
        "Scatterplot: One table, inline render")
    (is (emit-pyeq? bokeh/emit "bokeh/scatterplot-twoTable")
        "Scatterplot: Two tables, inline render")
    (is (emit-pyeq? bokeh/emit "bokeh/multiplot")
        "Scatterplot: Multiplot")
  (is (emit-pyeq? bokeh/emit "bokeh/glyphRender")
        "Glyph Renderer"))

