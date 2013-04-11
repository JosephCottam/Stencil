(ns stencil.test.bokeh
  (:require [stencil.emitters.bokeh :as bokeh])
  (:require [stencil.core :as c])
  (:require [stencil.transform :as t])
  (:use [clojure.test])
  (:use [stencil.test.utils]))


(def source "../tests/bokeh/")
(def target (freshDir "../testResults"))

(defn py-clean [p]
  "Attempts to make two superficially dis-similar python programs equal.
   Does so by removing trailing white-space and removing the numbers from things 
   that could-be gensyms (according to our gensym-convention of word-followed-by-underscore)."
  (-> p
    (clojure.string/replace #"(?m)(\s+)(\n)" "$2")
    (clojure.string/replace #"([\w&&]+?)_\d+(\W)" "$1$2")))

(defmethod assert-expr 'emit-pyeq? [msg form]
  `(let [emitter# ~(nth form 1)
         base# ~(nth form 2)
         src# (str source "/" base# ".stencil")
         ref# (str source "/" base# ".py")
         rslt# (str target "/" base# ".py")
         result# (.trim (emit emitter# target src# rslt#))
         expected# (.trim (slurp ref#))]
     (if (= (py-clean result#) (py-clean expected#))
       (report {:type :pass :message ~msg, :expected ref# :actual rslt#})
       (report {:type :fail :message (str "Compile did not match --- " ~msg), :expected ref# :actual rslt#}))
     result#))


(deftest bokeh
  (is (emit-pyeq? bokeh/emit "scatterplot-inline")
      "Scatterplot: One table, inline render")
  (is (emit-pyeq? bokeh/emit "scatterplot-twoTable")
      "Scatterplot: Two tables, inline render")
  (is (emit-pyeq? bokeh/emit "multiplot")
      "Scatterplot: Multiplot")
  (is (emit-pyeq? bokeh/emit "glyphRender")
      "Glyph Renderer"))
