(in-ns 'stencil.transform)


(defn defaultInfix? [x] (and (symbol? x) (every? #(not (. Character isLetterOrDigit %)) (name x))))
(defn toggled? [x] (and (symbol? x) (= \' (last (name x)))))

(defn infix? [x]
  (or (= '$op-colon x) 
      (and (defaultInfix? x) (not (toggled? x)))
      (and (not (defaultInfix? x)) (toggled? x))))

(defn removeToggle [x]
  (if (toggled? x) 
    (symbol (apply str (butlast (name x))))
    x))

(defn meta? [x] (and (list? x) (= 'meta (first x))))

(defn- stripToggles [program]
  (match [program]
    [(a :guard toggled?)] (removeToggle a)
    [(a :guard atom?)] a
    :else (map stripToggles program)))

;;TODO: Look at haskell "fixity" declarations to make a more natural math system, add them to meta!
(defn- moveOps
  "Move infix operators to head-position. All infix operators are left-associative"
  [program]
  (match [program]
    [(a :guard atom?)] a
    [([lhs (op :guard infix?) rhs] :seq)] 
         `(~op ~(moveOps lhs) ~(moveOps rhs))
    [([lhs (meta :guard meta?) (op :guard infix?) rhs] :seq)]
         `(~op ~(moveOps lhs) ~meta ~(moveOps rhs))

    ;;Infix operators are variable-arity on the rhs, this allows 1+2-3  (see 2,-,3 on the rhs of +)
    [([lhs (op :guard infix?) & rhs] :seq)]
         `(~op ~(moveOps lhs) ~(moveOps rhs))
    [([lhs (meta :guard meta?) (op :guard infix?) & rhs] :seq)]
         `(~op ~(moveOps lhs) ~meta ~(moveOps rhs))
    :else (map moveOps program)))

(defn infix->prefix [program] (-> program moveOps stripToggles))

