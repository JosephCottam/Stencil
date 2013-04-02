(ns stencil.transform) 

(defn parent [path program] 
   "Get the parent of the item indicated in the path."
   (reduce nth program (butlast path)))

(defn split-when
  "Takes the generator out of the when statement and puts it into a using statement.
   The using statement has a fields entry derived from generator.
   With the generator removed, 'when' is replaced by 'when-'."
   [program]
   (letfn 
    [(find-names
      ;"Search the program for the tuple source of the given name."
      [name path]
        (let [parent (parent path program)
              item  (first (filter #(and (seq? %) (= (name-of %) name)) parent))
              prototype (first (filter #(and (seq? %) (= 'fields (first %))) item))]
          (cond
            (nil? path) (throw (RuntimeException. "Prototype source not found in path"))
            (nil? item) (find-names name (butlast path))
            :else prototype)))

    (binds [generator path] 
     (match generator
       (e :guard empty?) '(fields ($meta))
       (['delta (m1 :guard meta?) source (m2 :guard meta?)] :seq) (find-names source path)
       (['items (m1 :guard meta?) source (m2 :guard meta?)] :seq) (find-names source path)
      :else 
       (throw (RuntimeException. (str "Generator expression not recognized: " (first generator) "...")))))
    (search [program upPath]
     (match program
      (a :guard atom?) a
      (m :guard meta?) m
      (['when (m1 :guard meta?) cond gen body] :seq)
         `(~'when- ~m1 ~cond (~'using (~'$meta) ~(binds gen (reverse upPath)) ~gen ~body))
      :else (map-indexed #(search %2 (cons %1 upPath)) program)))]
   (search program '())))
