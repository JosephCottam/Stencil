(ns stencil.test.utils
  "Utilities to help test stencil"
  (:require [stencil.core :as c])
  (:use [clojure.test]))

(defn freshDir [root] 
  (let [now (java.util.Date.) 
        rep (java.text.SimpleDateFormat.  "yyyyMMMdd--kk_mm")
        root (if (.endsWith root "/") root (str root "/")) 
        path (str root (.format rep now))]
    path))


(defn emit [emitter rsltRoot src rslt]
  "Run the emitter on the src program, write it to the result location and return the result."
  (let [program (emitter (c/compile src :file))
        rslt (java.io.File. rslt)
        parent (.getParentFile rslt)
        symTarget (java.io.File. (.getParent (java.io.File. rsltRoot)) "current")
        symSource (java.io.File. rsltRoot)]
    (.mkdirs parent)
    (if (.exists symTarget) (.delete symTarget))
    (java.nio.file.Files/createSymbolicLink (.toPath symTarget) (.toPath symSource) (make-array java.nio.file.attribute.FileAttribute 0))
    (spit rslt program)
    program))





