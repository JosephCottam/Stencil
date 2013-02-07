(ns stencil.core
  (:refer-clojure :rename {compile clj-compile read clj-read}) 
  (:require [stencil.parse :as parse])
  (:require [stencil.transform :as t])
  (:require [stencil.emitters.bokeh :as bokeh])
  (:require [stencil.emitters.cdx :as cdx])
  (:require [stencil.emitters.pico :as pico])
  (:require [clojure.java.io :as io]))



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
  (let [runtime (first (t/runtime program))
        runtime (.toUpperCase (str (second runtime)))]
    (case runtime
       "BOKEHRUNTIME" (bokeh/emit program)
       "CDXRUNTIME" (cdx/emit program)
       "JAVAPICORUNTIME" (pico/emit program))))  

(defn- compile-dispatch [program & opts]
  (cond 
    (and (= String (class program))
         (some #(= :file %) opts))  :filename
    (= String (class program))      :literal
    :else  :parsed))

(defmulti compile
  "Compile a program.  May be passed as a tree or a string.  
  If a filename is passed, include keyword :file as an argument."
  compile-dispatch)

(defmethod compile :filename [program & _] (compile (read program)))
(defmethod compile :literal [program & _] (compile (parse program)))
(defmethod compile :parsed [program & _]
  (let [_ (t/validate program)
        program (t/normalize program)
        modules (t/imports program)
        program (t/prep-emit program)]
    program))


(defn -main [from & to]
  (println "Compling from" from)
  (let [p (emit (compile from))]
    (if (empty? to)
      (println p)
      (spit (first to) p)))
  (println "done!"))


