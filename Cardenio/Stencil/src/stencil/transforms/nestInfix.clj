(in-ns 'stencil.transform)

(defn infix? [x]
  (or (= '$op-colon x) (= '-> x) (and (symbol? x) (= \' (last (name x))))))

(defn infix->prefix [x]
  (if (and (symbol? x) (= \' (last (name x))))
    (symbol (apply str (butlast (name x))))
    x))

(defn nestInfix
  "Move stencil-bind operators to head-position."
  [program]
  (match [program]
    [(a :guard atom?)] a
    [([lhs (op :guard infix?) rhs] :seq)] 
         `(~(infix->prefix op) ~(nestInfix lhs) ~(nestInfix rhs))
    [([lhs '$op-colon & rhs] :seq)]   ;;Op-colon is special, since it is variable arity in the rhs
         `(~'$op-colon ~(nestInfix lhs) ~(nestInfix rhs))
    :else (map nestInfix program)))



