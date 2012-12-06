(in-ns 'stencil.transform)

(def ^:dynamic *default-runtime* 'javaPico)
(defn ensure-runtime-import
   "Ensure that there is a runtime in the imports list."
   [program]
   (letfn
     [(has-runtime? [program]
       "Is some item imported a runtime?  
       TODO: Something more complex than look for an import with 'runtime' as a substring."
       (some 
           #(> (.indexOf (.toLowerCase (str (second %))) "runtime") -1)
           (filter #(and (list? %) (= 'import (first %))) program)))
      (add-runtime [defaultRuntime program]
        "Add the default runtime import to program."
        (concat (take 2 program) `((~'import ~defaultRuntime)) (drop 2 program)))]
   (if (has-runtime? program)
      program
      (add-runtime *default-runtime* program))))
