(in-ns 'stencil.transform)

(defn form? [a] (some (partial = a) '(stencil stream view table op const let)))
(defn tagged? [a] (and (list? a) (tag? (first a))))
(defn tag? [a] (some (partial = a) '($policy $val)))
(defn tagged-atom? [a] (and (list? a) (tag? (first a)) (atom? (second a)) (empty? (drop 2 a))))
(defn policy-form? [a] (some (partial = a) '(stream view table op)))


(defn tag-elements 
  ([program] (tag-elements false program))
  ([tagPolicies? program]
   (match [program]
     [a :guard tagged-atom?] a
     [a :guard atom?] (list '$val a)
     [([(head :guard form?) & tail] :seq)] 
        (cons head (map (partial tag-elements (policy-form? head)) tail))
     [([(head :guard atom?) & tail] :seq)]
         (if tagPolicies?
           `((~'$policy ~head) ~@(map tag-elements tail))
           (cons (tag-elements head) (map tag-elements tail)))
     :else (map tag-elements program))))
