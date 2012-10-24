(ns stencil.core
  (:require stencil.rparse)
  (:require stencil.transform))

(defn -main [from to]
  (println "Compling from" from "to" to "(but not really...yet)"))

(defn compileStencil [program]
  (let [_ (stencil.transform/validateParse program)
        program (stencil.transform/normalize program)
        modules (stencil.transform/imports program)]
     program))

(defn parseStencil [program]
  "string -> tree: Parses a stencil program from a string."
  (stencil.rparse/parseProgram program))

(defn readStencil
  "filename -> tree: reads program from specified file, returns as parse tree"
  [filename] 
    (let [f (new java.io.File filename)
          name (re-find #"[^.]*" (.getName f))]
    (parseStencil (str "(stencil " name (slurp filename) "\n)" ))))

