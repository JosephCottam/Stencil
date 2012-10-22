(in-ns 'stencil.transform)

(defn meta? [e]
   (and (list? e) (= '$meta (first e))))
   
(defn foldMetadata
  "Folds meta-data into the preceeding tagged entity"
  [program]
  (match [program]
    [([head (mta :guard meta?) & tail] : seq)]
       (if (list? head)
         (list (concat head (list mta)) (metaFold tail))
         (throw (RuntimeException. (str "Attempt to apply meta-data '" mta "' in disallowed context."))))
    :else (map metaFold program)))

;(defn metadataDict 
   ;"Converts metadata definitions to dictionaries. If no key is supplied, a $Pn is generated with n as the index."
  ;[program]
  ;(match [program]
    ;[(['$meta & args] :seq)]
       
    ;:else (map metadataDict program)))

