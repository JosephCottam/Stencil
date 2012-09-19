(ns Stencil.core)

(defn readProgram [filename] (read-string (str \( (slurp filename) \n \))))

(defn transform [program] (-> program identity))
