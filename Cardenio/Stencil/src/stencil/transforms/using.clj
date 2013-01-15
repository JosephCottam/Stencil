(ns stencil.transform)

(defn arrow->using [program]
  "The arrow operator is the binary, infix version of using."
  (match [program]
    [(a :guard atom?)] a
    [(['-> e1 e2] :seq)] (list 'using  e1 e2)
    :else (map arrow->using program)))
    
(defn ensure-using-tuple [program]
  "Ensure that the first expression in a using statement produces a tuple."
  (letfn [(tuple-fn? [m]
            (let [type ((meta->map m) 'type)]
              (and (list? type) 
                   (= (first type) 'fn)
                   (= (count type) 3)
                   (let [[_ args rv] type]
                     (and (list? rv)
                          (= (first rv) 'tuple))))))
          (ensure-tuple [e] 
            (match [e]
              [(['let vals body] :seq)] (list 'let vals body)
              [([x (m :guard meta?) & rest] :seq)]
                 (if (tuple-fn? m)
                   e
                   (list 'tuple '($meta (type (fn (...) (tuple (...))))) e))
              :else (list 'tuple '($meta (type (fn (...) (tuple (...))))) e)))]
    (match [program]
      [(a :guard atom?)] a
      [(['using (m :guard meta?) e1 e2] :seq)]  
         (list 'using m (ensure-tuple e1) e2)
      :else (map ensure-using-tuple program))))

  


