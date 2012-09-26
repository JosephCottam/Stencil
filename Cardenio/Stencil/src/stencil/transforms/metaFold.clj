(in-ns 'stencil.transform)

(defn- metaParent? [n]
   (some #(= n %) '(stencil view table operator stream value)))

(defn- meta? [e]
   (and (list? e) (= 'meta (first e))))
   

(defn- phasedFold
  [program doFold]
  (match [program]
    ['meta & rst] `(meta ~rst)
    [([tag :guard metaParent? & rst] :seq)] (list tag (phasedFold rst false))
    [([maybe :guard list? meta :guard meta? & rst] :seq)]
        (if doFold
            (list (concat mabye meta) (phasedFold rst doFold))
            (list maybe meta (phasedFold rst doFold)))
    :else (map (partial phasedFold true) program)))

(defn metaFold
  "Folds meta-data into the preceeding tagged entity"
  [program]
  (phasedFold program true))
