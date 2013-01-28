(ns stencil.core
  (:refer-clojure :rename {compile clj-compile read clj-read}) 
  (:require [stencil.parse :as parse])
  (:require [stencil.transform :as t])
  ;(:require [stencil.emitters.bokeh :as bokeh])
  (:require [stencil.emitters.cdx :as cdx])
  (:require [stencil.emitters.pico :as pico])) 


(defn -main [from to]
  (println "Compling from" from "to" to "(but not really...yet)"))

(defn parse [program]
  "string -> tree: Parses a stencil program from a string."
  (parse/parse-program program))

(defn read
  "filename -> tree: reads program from specified file, returns as parse tree"
  [filename] 
    (let [f (new java.io.File filename)
          name (re-find #"[^.]*" (.getName f))]
    (parse (str "(stencil " name (slurp filename) "\n)" ))))


(defn emit [program]
  (let [runtime (t/runtime program)
        runtime (.toUpperCase (str (second runtime)))]
    (case runtime
       ;"BOKEHRUNTIME" (bokeh/emit program)
       "CDXRUNTIME" (cdx/cdx program)
       "JAVAPICORUNTIME" (pico/emit program))))  

;;TODO: Convert to mulit-method with input and output file options
(defn compile-program [program]
  (let [_ (t/validate program)
        _ (println program)
        program (t/normalize program)
        modules (t/imports program)
        program (t/prep-emit program)]
    program))
(defn compile [filename] (compile-program (read filename)))
