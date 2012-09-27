(in-ns 'stencil.transform)

(defn- ensureList [a] (if (list? a) a (list a)))

(defn normalizeSet
  "Ensure that all set lines have same form.  Must run before infix->prefix"
  [program]
  (match [program]
    [(x :guard atom?)] x
    [(['set & setLines] :seq)] 
      (cons 'set (map (fn [[t & rst]] (cons (ensureList t) rst)) setLines))
    :else (map normalizeSet program)))
    


