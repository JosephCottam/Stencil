(ns Stencil.core
   (:use stencil.rparse :only [parse]))


;;Directions for reges -- http://stackoverflow.com/questions/5695240/php-regex-to-ignore-escaped-quotes-within-quotes

(defn readProgram [filename] 
  (let [raw (str \( (slurp filename) \n \))]
     (parse raw #"[\s,]+" #"(?:[^"\\]|\\.)*" "(" ")" nil)))
        
        

(xxread-string (str \( (slurp filename) \n \))))

(defn transform [program] (-> program identity))
