(in-ns 'stencil.transform)

(defn comment? [e] (and (seq? e) (= 'comment (first e))))
(defn drop-comments 
  "Remove all Comment nodes"
  [program]
  (match [program]
    [(a :guard atom?)] a
    [(c :guard comment?)] nil
    [([(first :guard comment?) & rest] :seq)]
       (drop-comments rest)
    [([first & rest] :seq)]
      (cons (drop-comments first) (map drop-comments rest))))
