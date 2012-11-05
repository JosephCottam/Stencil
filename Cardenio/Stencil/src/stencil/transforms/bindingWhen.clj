(in-ns 'stencil.transform)

(defn binding-when
  "Modifies the 'when' statement from its lexical form to one
   that includes information about the bindings.  This simplifies
   scope tracking later. To denote this change, 'when' becomes 'when+'."
   [program]

   (defn findPrototype 
     "Search the program for the tuple source of the given name.
      TODO: There is problem with this...it assuems a fla' namespace,
            but stencil programs define non-flat namespaces."
      [name]
      'PLACEHOLDER)

   (defn binds [generator] 
      (match [generator]
        [(['delta source] :seq)] (findPrototype source)
        [(['items source] :seq)] (findPrototype source)

        :else (throw (RuntimeException. (str "Generator expression not recognized" generator))))
   'PLACEHOLDER)

   (match [program]
     [(a :guard atom?)] a
     [(['when cond gen trans] :seq)]
       `(~'when+ ~cond ~gen ~(binds gen) ~trans)
     :else (map binding-when program)))

