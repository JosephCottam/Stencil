(ns stencil.transform
  "Manipulate the comments found in a program.")

(defn comment? [e] (and (seq? e) (= 'comment (first e))))
(defn drop-comments  [program]
  "Remove all Comment nodes"
  (match program
    (a :guard atom?) a
    (n :guard empty?) n
    ([(tag :guard comment?) & rest] :seq)
       (drop-comments rest)
    ([first  & rest] :seq)
       (cons (drop-comments first) (drop-comments rest))))

