(ns stencil.core
  (:require stencil.rparse)
  (:require stencil.transform))

(defn -main [from to]
  (println "Compling from" from "to" to "(but not really...yet)"))

(defn compile-stencil [program]
  (let [_ (stencil.transform/validate program)
        program (stencil.transform/normalize program)
        modules (stencil.transform/imports program)]
     program))

(defn parse-stencil [program]
  "string -> tree: Parses a stencil program from a string."
  (stencil.rparse/parse-program program))

(defn read-stencil
  "filename -> tree: reads program from specified file, returns as parse tree"
  [filename] 
    (let [f (new java.io.File filename)
          name (re-find #"[^.]*" (.getName f))]
    (parse-stencil (str "(stencil " name (slurp filename) "\n)" ))))

