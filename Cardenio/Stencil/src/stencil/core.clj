(ns stencil.core
   (:require stencil.rparse))

(defn -main [from to]
  (println "Compling from" from "to" to "(but not really)"))

;Directions for reges -- http://stackoverflow.com/questions/5695240/php-regex-to-ignore-escaped-quotes-within-quotes

(defn parseProgram [program]
  "string -> tree: Parses a stencil program from a string."
  (stencil.rparse/parseProgram program)) 

(defn readProgram 
  "filename -> tree: reads tree from specified file"
  [filename] 
    (let [f (new java.io.File filename)
          name (re-find #"[^.]*" (.getName f))]
    (parseProgram (str "(stencil" name (slurp filename) \n \)))))
      
(defn normalize 
  "tree -> tree: Transforms a parse-form tree to normal-form tree"
  [program] (-> program identity))

