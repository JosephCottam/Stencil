;Based loosely on https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/LispReader.java
(ns stencil.rparse
  "An s-expression reader macro system for pre-processing before going to the clojure reader.")

(defn readUntil [emit delim tokens]
  (cond 
    (empty? tokens) '(() ())
    (= delim (first tokens)) (list () (rest tokens))
    :else (let [[phase1 remain] (emit emit tokens)
                [phase2 remain] (readUntil emit delim remain)]
            (list (concat phase1 phase2) remain))))

(defn emitter [transforms]
  "Produce a function that take tokens are returns (before after) pairs from the transforms
   Transforms must be functions [emitter tokens] -> [tokens tokens] "
  (fn [emit tokens]
    (loop [transforms transforms]
      (let [[p t] (first transforms)]
        (cond
          (and (fn? t) (p tokens)) (t emit tokens)
          (nil? t) (list (str (first tokens)) (rest tokens))
          :else (recur (rest transforms)))))))
         


(defn comment? [tokens] (.equals \; (first tokens)))
(defn comment [emit tokens] 
  (let [term (if (= '(; *) (take 2 tokens)) '(* ;) '(\n))
       [[comment remain] (gatherTo \n (rest tokens))]
       (list (concat "(comment " comment ")") remain)))

(defn bind? [tokens] (.equals \: (first tokens)))
(defn bind [emit tokens] 
  (print "Bind")
  (list " $op-colon " (rest tokens))) 

(defn tupleLit? [tokens] (= '(\# \() (take 2 tokens)))
(defn tupleLit [emit tokens] (list nil (concat " ($tuple " (drop 2 tokens))))

(defn stMeta? [tokens] 
  (print "meta check")
  (= '(\: \[) (take 2 tokens)))

(defn stMeta  [emit tokens] 
  (print "meta!")
  (let [[internal remain] (readUntil emit \] (drop 2 tokens))]
    (list (concat "(meta (" internal "))") remain)))

(defn stString? [tokens] (= \" (first tokens)))
(defn stString  [emit tokens]
  ((fn gather [tokens]
     (cond
       (= \\ (first tokens)) (concat (take 2 tokens) (gather (drop 2 tokens)))
       (= \" (first tokens)) (list '(\") (rest tokens)))) 
     tokens))

(defn startList? [tokens] (= \( (first tokens)))
(defn readList [emit tokens] 
  (let [[internal remain] (readUntil emit \) (rest tokens))]
    (list (concat "(" internal ")") remain)))

(defn parseProgram [src]
  "string -> tree: Parses stencil program from a string."
  (let [[srcLs remain] (readList (emitter `((~stMeta? stMeta) (~bind? ~bind) (~stString? ~stString) (~tupleLit? ~tupleLit) (~startList? ~readList))) src)
        source (apply str srcLs)]
    (read-string source)))

