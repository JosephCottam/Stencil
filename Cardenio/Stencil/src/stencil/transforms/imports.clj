(in-ns 'stencil.transform)

(def *default-runtime* 'javaPico)
(defn ensureRuntimeImport
   "Ensure that there is a runtime in the imports list."
   [program]

   (defn hasRuntime? [program]
    "Is some item imported a runtime?  
    TODO: Something more complex than look for an import with 'runtime' as a substring."
    (some 
      #(> (.indexOf (.toLowerCase (str (second %))) "runtime") -1)
      (filter #(and (list? %) (= 'import (first %))) program)))

   (defn addRuntime [defaultRuntime program]
    "Add the default runtime import to program."
    (concat (take 2 program) `((~'import ~defaultRuntime)) (drop 2 program)))

   (if (hasRuntime? program)
      program
      (addRuntime *default-runtime* program)))
