(ns stencil.core
  (:gen-class)
  (:refer-clojure :rename {compile clj-compile read clj-read}) 
  (:require [stencil.parse :as parse])
  (:require [stencil.transform :as t])
  (:require [stencil.emitters.vega :as vega])
  (:require [stencil.emitters.bokeh :as bokeh])
  (:require [stencil.emitters.pico :as pico])
  (:require [clojure.java.io :as io]))



(defn parse [program]
  "string -> tree: Parses a stencil program from a string."
  (parse/parse-program program))

(defn maybe-wrap [name content]
  "To properly parse, a stencil program must be wrapped in in a 'stencil' context.
   This method wraps the content in a stencil context of the given name IF the content does not start with a stencil-context declaration."
  (if (re-find #"^\(stencil\s+\S+" content) content (str "(stencil " name "\n" content "\n)")))

(defn read [filename]
  "filename -> string: reads program from specified file.  Wraps in a 'stencil' block if not already present."
  (let [f (new java.io.File filename)
        name (re-find #"[^.]*" (.getName f))]
    (maybe-wrap name (slurp filename))))

(defn read-stdin []
  "string: Reads from std-in until EOF or null line is read."
  (let [b (java.io.BufferedReader. *in*)
        content (doall (take-while 
                         (fn [l] (and (not (= ";#EOF" l)) (not (nil? l))))
                         (repeatedly #(.readLine b))))]
    (maybe-wrap "Anonymous" (apply str content))))

(defn emit [program]
  (let [runtime (first (t/runtime program))
        runtime (.toUpperCase (str (t/full-nth runtime 1)))]
    (case runtime
       "BOKEHRUNTIME" (bokeh/emit program)
       "VEGARUNTIME" (vega/emit program)
       "JAVAPICORUNTIME" (pico/emit program)
      :else (throw (RuntimeException. "Could not find runtime import in program.")))))

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

(defmethod compile :filename [program & _] (compile (parse (read program))))
(defmethod compile :literal [program & _] (compile (parse program)))
(defmethod compile :parsed [program & _]
  (let [_ (t/validate program)
        program (t/normalize program)
        modules (t/imports program)
        program (t/prep-emit program)]
    program))

(defn -main [& args]
  (let [dict (zipmap (take-nth 2 args) (take-nth 2 (rest args)))
        in (if (nil? (dict "-in")) nil (dict "-in"))
        out (if (nil? (dict "-out")) nil (dict "-out"))
        com (if (nil? (dict "-c")) ";" (dict "-c"))]
    (if (not (nil? in)) (println com com com " Compiling from " in))
    (if (not (nil? out)) (println com com com "  Compiling to " out))
    (flush)
    (let [program (if (nil? in) (read-stdin) (read in))
          p (emit (compile program))]
      (if (nil? out) 
        (println p)
        (spit out p)))))


