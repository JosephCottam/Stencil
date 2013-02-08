(ns stencil.test.emit
  (:require [stencil.emitters.bokeh :as bokeh])
  (:require [stencil.emitters.cdx :as cdx])
  (:require [stencil.core :as c])
  (:use [clojure.test]))


(defn freshDir [root] 
  (let [now (java.util.Date.) 
        rep (java.text.SimpleDateFormat.  "yyyyMMMdd--kk_mm")
        root (if (.endsWith root "/") root (str root "/")) 
        path (str root (.format rep now))
        dir (java.io.File. path)]
    path))

(def source "../tests/")
(def target (freshDir "../testResults/"))

(defn emit [emitter src rslt]
  "Run the emitter on the src program, write it to the result location and return the result."
  (let [program (emitter (c/compile src :file))
        rslt (java.io.File. rslt)]
    (.mkdirs (.getParentFile rslt))
    (spit rslt program)
    program))

(defmethod assert-expr 'emit-eq? [msg form]
  `(let [emitter# ~(nth form 1)
         base# ~(nth form 2)
         src# (str source "/" base# ".stencil")
         rslt# (str target "/" base# ".py")
         ref# (str source "/" base# ".py")
         result# (.trim (emit emitter# src# rslt#))
         expected# (.trim (slurp ref#))]
     (if (= result# expected#) 
       (report {:type :pass :message ~msg, :expected ref# :actual rslt#})
       (report {:type :fail :message (str "Compile did not match --- " ~msg), :expected ref# :actual rslt#}))
     result#))

(deftest cdx
    (is (emit-eq? cdx/emit "cdx/scatterplot-inline")
        "Scatterplot: One table, inline render")
    (is (emit-eq? cdx/emit "cdx/scatterplot-twoTable")
        "Scatterplot: Two tables, inline render"))

(deftest bokeh
    (is (emit-eq? bokeh/emit "bokeh/scatterplot-inline")
        "Scatterplot: One table, inline render")
    (is (emit-eq? bokeh/emit "bokeh/scatterplot-twoTable")
        "Scatterplot: Two tables, inline render")
    (is (emit-eq? bokeh/emit "bokeh/multiplot")
        "Scatterplot: Multiplot"))

