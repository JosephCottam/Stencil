(in-ns 'stencil.transform)
   
(defn parent 
   "Get the parent of the item indicated in the path."
   [path program] 
   (reduce nth program (butlast path)))

(defn binding-when
  "Modifies the 'when' statement from its lexical form to one
   that includes information about the bindings.  
   This simplifies name resolution later. 
   To denote this change, 'when' becomes 'when+'."
   [program]
   (letfn 
    [(find-names
      ;"Search the program for the tuple source of the given name."
      [name path]
        (let [parent (parent path program)
              item  (first (filter #(and (seq? %) (= (second %) name)) parent))
              prototype (first (filter #(and (seq? %) (= 'fields (first %))) item))]
           (if (nil? item) 
               (find-names name (butlast path))
               prototype)))

    (binds [generator path] 
     (match [generator]
       [e :guard empty?] '(fields)
       [(['delta (m1 :guard meta?) source (m2 :guard meta?)] :seq)] (find-names source path)
       [(['items (m1 :guard meta?) source (m2 :guard meta?)] :seq)] (find-names source path)
      :else 
       (throw (RuntimeException. (str "Generator expression not recognized: " (first generator) "...")))))
    (search [program upPath]
     (match [program]
      [(a :guard atom?)] a
      [(m :guard meta?)] m
      [(['when (m1 :guard meta?) cond gen trans] :seq)]
      `(~'when+ ~m1 ~cond ~gen ~(binds gen (reverse upPath)) ~trans)
      :else (map-indexed #(search %2 (cons %1 upPath)) program)))]
   (search program '())))

