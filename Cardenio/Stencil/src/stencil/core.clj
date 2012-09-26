(ns stencil.core
  (:require stencil.rparse)
  (:require stencil.transform))

(defn -main [from to]
  (println "Compling from" from "to" to "(but not really)"))

;Directions for reges -- http://stackoverflow.com/questions/5695240/php-regex-to-ignore-escaped-quotes-within-quotes

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
