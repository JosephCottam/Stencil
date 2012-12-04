(in-ns 'stencil.transform)

(defn drop-comments 
  "Remove all Comment nodes"
  [program]
  (remove #(or (= % 'comment) (= % '(comment))) program))
