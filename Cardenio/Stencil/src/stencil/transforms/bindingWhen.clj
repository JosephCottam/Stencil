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
  
    (defn find-prototype 
     "Search the program for the tuple source of the given name."
     [name path]
       (let [parent (parent path program)
             item  (first (filter #(and (seq? %) (= (second %) name)) parent))
             prototype (first (filter #(and (seq? %) (= 'prototype (first %))) item))]
          (if (nil? item) 
              (find-prototype name (butlast path))
              prototype)))

    (defn binds [generator path] 
     (match [generator]
      [(['delta source] :seq)] (find-prototype source path)
      [(['items source] :seq)] (find-prototype source path)
      :else (throw (RuntimeException. (str "Generator expression not recognized" generator)))))

   (defn- search [program upPath]
    (match [program]
     [(a :guard atom?)] a
     [(['when cond gen trans] :seq)]
     `(~'when+ ~cond ~gen ~(binds gen (reverse upPath)) ~trans)
     :else (map-indexed #(search %2 (cons %1 upPath)) program)))
   (search program '()))

