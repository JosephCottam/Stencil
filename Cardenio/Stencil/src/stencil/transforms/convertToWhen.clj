(in-ns 'stencil.transform)

(defn pull->when
  "Convert pull statements to when statements"
  [program]
  (match [program]
    [a :guard atom?] a
    [(['pull from conseq] :seq)] 
         (list 'when (list 'onChange from) (list 'items from) conseq)
    :else (map pull->when program)))

(defn init->when
  "Convert init statements to when statements."
  [program]
  (match [program]
    [a :guard atom?] a
    [(['init gen expr] :seq)]
      `(when ~'init ~gen ~expr)
    :else (map init->when program)))




(defn file->init
  "Convert a 'file' statement that is in a 'data' policy to an init statement"
  [program]
  
  (defn default-preproc [fields] 
    (list 'let 
          (map-indexed (fn [i name] (list '$C name `(~'tuple-ref ~'$source ~i)))) 
          (list '$ptuple fields fields)))

  (match [program]
    [a :guard atom?] a
    [['data [(['file filename meta1 parser meta2] :seq)]]] `(~'when ~'init (~'file ~filename ~meta1 ~parser ~meta2) ~(default-preproc '(PLACEHOLDER)))
    [['data [(['file filename meta1 parser meta2 preproc] :seq)]]] `(~'when ~'init (~'file ~filename ~meta1 ~parser ~meta2) preproc)
    :else (map file->init program)))
