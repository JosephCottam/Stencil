(ns stencil.transform
  "Manipulate imports.")

(def ^:dynamic *default-runtime* 'javaPicoRuntime)
(defn ensure-runtime-import [program] 
   "Ensure that there is a runtime in the imports list."
   (letfn
     [(has-runtime? [program]
       "Is some item imported a runtime?  
       TODO: Something more elegant than look for an import with 'runtime' as a substring."
       (some 
           #(> (.indexOf (.toUpperCase (str (second %))) "RUNTIME") -1)
           (filter #(and (seq? %) (= 'import (first %))) program)))
      (fragment? [program]
        "A fragment does not start with (stencil ...)"
        (not (= 'stencil (first program))))
      (add-runtime [defaultRuntime program]
        "Add the default runtime import to program."
        (concat (take 2 program) `((~'import ~defaultRuntime)) (drop 2 program)))]
   (if (or (has-runtime? program) (fragment? program))
      program
      (add-runtime *default-runtime* program))))
