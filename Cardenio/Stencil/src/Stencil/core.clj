(ns stencil.core)

(defn -main [from to]
  (println "Compling from" from "to" to "(but not really)"))

(defn readProgram 
  "filename -> tree: reads tree from specified file
   REQUIRES that all non-syntax colons be escaped.
   This is required because we ': isn't a valid symbol in Clojure
   and we use a regular expression to convert them to a valid symbol (namely, stBind)
   before reading the file contents into an s-expression."
  [filename] 
  (read-string 
    (clojure.string/replace (str \( (slurp filename) \n \)) #"[^\\\\]:" " stBind ")))

(defn normalize 
  "tree -> tree: Transforms a parse-form tree to normal-form tree"
  [program] (-> program identity))
