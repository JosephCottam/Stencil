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
       "Move infix operators and related metas to head-position. All infix operators are left-associative
       TODO: Look at haskell 'fixity' declarations to make a more natural math system, add them to meta!"
       (match program
         (a :guard atom?) a
         (e :guard empty?) e
         ([(lhs :guard atom?) (m0 :guard meta?) (op :guard infix?) (m1 :guard meta?) (rhs :guard atom?) (m2 :guard meta?)] :seq)  ;;atomic left and atomic right
            (list op m1 lhs m0 rhs m1)
         ([(lhs :guard atom?) (m0 :guard meta?) (op :guard infix?) (m1 :guard meta?) rhs] :seq)  ;;atomic left and non-atomic right
            (list op m1 lhs m0 (moveOps rhs))
         ([lhs (op :guard infix?) (m1 :guard meta?) (rhs :guard atom?) (m2 :guard meta?)] :seq)  ;;non-atomic left and atomic right
            (list op m1 (moveOps lhs) rhs m2)
         ([lhs (op :guard infix?) (m1 :guard meta?) rhs] :seq)  ;;non-atomic left and non-atomic right
            (list op m1 (moveOps lhs) (moveOps rhs))

         ;;Some dos can be replaced with the infix operators.
         (['do (m :guard meta?) lhs (op :guard infix?) (m1 :guard meta?) rhs] :seq)
            (list op m1 (moveOps lhs) (moveOps rhs))
         (['do (m :guard meta?) (lhs :guard atom?) (m0 :guard meta?) (op :guard infix?) (m1 :guard meta?) rhs] :seq)
            (list op m1 lhs m0 (moveOps rhs))
         (['do (m :guard meta?) lhs (op :guard infix?) (m1 :guard meta?) (rhs :guard atom?) (m2 :guard meta?)] :seq)
            (list op m1 (moveOps lhs) (moveOps rhs) m2)
         (['do (m :guard meta?) (lhs :guard atom?) (m0 :guard meta?) (op :guard infix?) (m1 :guard meta?) (rhs :guard atom?) (m2 :guard meta?)] :seq)
            (list op m1 lhs m0 rhs m2)

         ;;Infix operators are variable-arity on the rhs, this allows 1+2-3  (see "2,-,3" on the rhs of +)
         ([(lhs :guard atom?) (m0 :guard meta?) (op :guard infix?) (m1 :guard meta?) & rhs] :seq)
            (list op m1 lhs m0 (moveOps rhs))
         ([lhs (op :guard infix?) (m1 :guard meta?) & rhs] :seq)
            (list op m1 (moveOps lhs) (moveOps rhs))
         :else (map moveOps program)))]
    (-> program moveOps stripToggles)))

