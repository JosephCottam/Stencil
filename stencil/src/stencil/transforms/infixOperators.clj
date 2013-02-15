(ns stencil.transform
  "Move infix operators into the prefix position, grouping arguments as appropriate.")

(defn defaultInfix? [x] 
  (and (symbol? x) 
       (not (= '_ x)) 
       (every? #(not (. Character isLetterOrDigit %)) (name x))))

(defn toggled? [x] (and (symbol? x) (= \' (last (name x)))))

(defn infix? [x]
  (or (and (defaultInfix? x) (not (toggled? x)))
      (and (not (defaultInfix? x)) (toggled? x))))
  
(defn infix->prefix [program]
  "Move all infix operators to prefix positions"
  (letfn 
    [(removeToggle [x]
       (if (toggled? x) 
         (symbol (apply str (butlast (name x))))
         x))
     (stripToggles [program]
       (match program
         (a :guard toggled?) (removeToggle a)
         (a :guard atom?) a
         :else (map stripToggles program)))
     (moveOps [program]
       "Move infix operators to head-position. All infix operators are left-associative
       TODO: Look at haskell 'fixity' declarations to make a more natural math system, add them to meta!"
       (match program
         (a :guard atom?) a
         ([lhs (op :guard infix?) rhs] :seq)
            (list op (moveOps lhs) (moveOps rhs))
         ([lhs (m :guard meta?) (op :guard infix?) rhs] :seq)
            (list op (moveOps lhs) m (moveOps rhs))
         ([lhs (op :guard infix?) rhs (m :guard meta?)] :seq)
            `(~op ~(moveOps lhs) (~'do ~(moveOps rhs) ~m))    ;;Add a synthetic expression to wrap together up the ops and meta-data
         ([lhs (m :guard meta?) (op :guard infix?) rhs (m2 :guard meta?)] :seq)
            `(~op ~(moveOps lhs) ~m (~'do ~(moveOps rhs) ~m2)) ;;Add a synthetic expression to wrap together up the ops and meta-data

         ;;Some dos can be replaced with the infix operators.
         (['do lhs (op :guard infix?) rhs] :seq)
            (list op (moveOps lhs) (moveOps rhs))
         (['do lhs (m :guard meta?) (op :guard infix?) rhs] :seq)
            (list op (moveOps lhs) m (moveOps rhs))
         (['do lhs (op :guard infix?) rhs (m :guard meta?)] :seq)
            `(~op ~(moveOps lhs) (~'do ~(moveOps rhs) ~m))    ;;Add a synthetic expression to wrap together up the ops and meta-data
         (['do lhs (m :guard meta?) (op :guard infix?) rhs (m2 :guard meta?)] :seq)
            `(~op ~(moveOps lhs) ~m (~'do ~(moveOps rhs) ~m2)) ;;Add a synthetic expression to wrap together up the ops and meta-data


         ;;Infix operators are variable-arity on the rhs, this allows 1+2-3  (see 2,-,3 on the rhs of +)
         ([lhs (op :guard infix?) & rhs] :seq)
            `(~op ~(moveOps lhs) ~(moveOps rhs))
         ([lhs (m :guard meta?) (op :guard infix?) & rhs] :seq)
            `(~op ~(moveOps lhs) ~m ~(moveOps rhs))
         :else (map moveOps program)))]
    (-> program moveOps stripToggles)))

