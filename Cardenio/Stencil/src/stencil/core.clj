(ns stencil.core
  (:refer-clojure :rename {compile clj-compile read clj-read}) 
  (:require [stencil.rparse :as parse])
  (:require stencil.compile))
          
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

(defn compile [filename] (stencil.compile/compile (read filename)))

