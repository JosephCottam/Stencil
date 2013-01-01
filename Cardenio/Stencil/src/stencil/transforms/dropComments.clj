(in-ns 'stencil.transform)

(defn comment? [e] (and (seq? e) (= 'comment (first e))))
(defn drop-comments 
  "Remove all Comment nodes"
  [program]
  (match [program]
    [(a :guard atom?)] a
    [(n :guard empty?)] n
    [([(tag :guard comment?) & rest] :seq)]
       (drop-comments rest)
    [([first  & rest] :seq)]
       (cons (drop-comments first) (drop-comments rest))))

