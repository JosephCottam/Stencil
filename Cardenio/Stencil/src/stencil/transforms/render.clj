(ns stencil.transform)

(defn clean-binds [program]
  "Reform bindings inside of render statements to have a simpler form."
  (letfn [(maybe-clean [policy]
            (let [[tag meta & bindings] policy]
              (if (= tag 'bind)
                `(~tag ~meta ~@(map full-drop bindings))
                policy)))]
  (match [program]
    [a :guard atom?] a
    [(['render id (m1 :guard meta?) target (m2 :guard meta?) type (m3 :guard meta?) & policies] :seq)]
      `(~'render ~id ~m1 ~target ~m2 ~type ~m3 ~@(map maybe-clean policies))
    :else (map clean-binds program))))
       

(defn lift-renders [program]
  "Lift all render statements out to top-level, provide explicit target argument."
  (letfn 
    [(sift1 [context program] 
       (match [program]
         [a :guard atom?] nil  
         [(['table name & rest] :seq)] (sift1 name rest)
         [(['render (m :guard meta?) & rest] :seq)] 
             `((~'render ~(gensym 'rend) ~m ~context (~'$meta) ~@rest))
         :else (reduce concat (map (partial sift1 context) program))))
     (justrenders [program] (remove nil? (sift1 '**NONE** program)))
     (simplify [program]
       (match [program]
         [a :guard atom?] a
         [(['table name & rest] :seq)] `(~'table ~name ~@(remove #(= 'render (first %)) rest))
         :else (map simplify program)))
     (split [program]
       (let [f (first program)]
         (cond
           (nil? f) (list '() '())
           (and (seq? f) (= 'table (first f))) (list '() program)
           :else (let [[before after] (split (rest program))]
                   (list (cons f before) after)))))]
    (let [reduced (simplify program)
          renders (justrenders program)
          [before after] (split reduced)]
      (concat before renders after))))


