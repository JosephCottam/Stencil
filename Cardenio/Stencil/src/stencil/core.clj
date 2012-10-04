(ns stencil.core
  (:require stencil.rparse)
  (:require stencil.transform)
  (:require clojure.pprint))

(defn -main [from to]
  (println "Compling from" from "to" to "(but not really...yet)"))

(defn compileStencil [program]
  (-> program stencil.transform/normalize))

(defn parseStencil [program]
  "string -> tree: Parses a stencil program from a string."
  (stencil.rparse/parseProgram program))

(defn readStencil
  "filename -> tree: reads program from specified file, returns as parse tree"
  [filename] 
    (let [f (new java.io.File filename)
          name (re-find #"[^.]*" (.getName f))]
    (parseStencil (str "(stencil " name (slurp filename) "\n)" ))))

(defn spp [program] (clojure.pprint/pprint program))
