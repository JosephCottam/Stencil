(in-ns 'stencil.transform)

(defn binding-when
  "Modifies the 'when' statement from its lexical form to one
   that includes information about the bindings.  This simplifies
   scope tracking later. To denote this change, 'when' becomes 'when+'."
   [program]
   (defn findPrototype 
     "Search the program for the tuple source of the given name."
      [name]
      'PLACEHOLDER)

   (defn binds [generator] 'PLACEHOLDER)

   (match [program]
     [(a :guard atom?)] a
     [(['when cond gen trans] :seq)]
       `(~'when+ ~cond ~gen ~(binds gen) ~trans)
     :else (map binding-when program)))

