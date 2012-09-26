(in-ns 'stencil.transform)

(defn tagValueRoles
  "Tag id's, values, etc. to differentiate different ways a symbol can be interpreted.
   Also provides a target to fold meta-data into."
  [program]
  (match [program]
    [(t :guard tag? & rest) `(~t ~rest)
    [(v :guard (some-fn number? string? symbol?)] `(value ~v)
    [([binder :guard namingContext? id & rest] :seq)]
       (list binder `(id ~id) (tagValueRoles rest))
    [([op & args] :seq)] (list op (map tagValueRoles args))
    :else (map tagValueRoles program))))

