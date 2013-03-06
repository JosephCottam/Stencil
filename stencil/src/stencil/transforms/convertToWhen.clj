(ns stencil.transform
  "Convert the standard trigger statements into when statements.")

(defn pull->when [program] 
  "Convert pull statements to when statements"
  (match program
    (a :guard atom?) a
    (['pull from conseq] :seq)
         (list 'when (list 'onChange from) (list 'items from) conseq)
    :else (map pull->when program)))

(defn init->when [program] 
  "Convert init statements to when statements."
  (match program
    (a :guard atom?) a
    (['init gen] :seq)
      `(~'when (~'$init?) () ~gen)
    :else (map init->when program)))


(defn file->init [program] 
  "Convert a 'file' statement that is in a 'data' policy to an init statement"
  (letfn [(default-preproc [fields] 
            (list 'let 
                  (map-indexed (fn [i name] (list '$$ name `(~'tuple-ref ~'$source ~i))) fields) 
                  (list '$ptuple fields fields)))]

    (match program
      (a :guard atom?) a
      ['data (['file filename meta1 parser meta2] :seq)] 
         `(~'when ~'init (~'file ~filename ~meta1 ~parser ~meta2) ~(default-preproc '(PLACEHOLDER)))
      ['data (['file filename meta1 parser meta2 preproc] :seq)]
         `(~'when ~'init (~'file ~filename ~meta1 ~parser ~meta2) preproc)
      :else (map file->init program))))