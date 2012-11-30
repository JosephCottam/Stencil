;Based loosely on https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/LispReader.java
(ns stencil.parse
  "An s-expression reader macro system for pre-processing before going to the clojure reader.")

(defn emitter [transforms]
  "Returns a function that converts one character stream into another.
   Transforms is a list of predicate/transform pairs.  
   The first predicate that returns true is applied."
  (fn [emit tokens]
    (loop [transforms transforms]
      (let [[p t] (first transforms)]
        (cond
          (empty? tokens) '()
          (nil? t) (list (str (first tokens)) (rest tokens))
          (and (fn? t) (p tokens)) (t emit tokens)
          :else (recur (rest transforms)))))))

(defn readUntil 
  "Read the tokens, applying emitter to each, until the delimiter s found (or end of stream).
   TODO: Should end-of-stream be an error...at least optionally?"
   [emit delim tokens]
  (cond 
    (empty? tokens) '(() ())
    (= delim (take (count delim) tokens)) (list () (rest tokens))
    :else (let [[phase1 remain] (emit emit tokens)
                [phase2 remain] (readUntil emit delim remain)]
            (list (concat phase1 phase2) remain))))

(defn stComment? [tokens] (= \; (first tokens)))
(defn stComment [emit tokens] 
  (let [term (if (= '(\; \*) (take 2 tokens)) '(\* \;) '(\newline))
       [comment remain] (readUntil (emitter '()) term (drop (count term) tokens)) ]
       (list (concat "(comment \"" comment "\")") remain)))

(defn bind? [tokens] (= \: (first tokens)))
(defn bind [emit tokens] (list " $C " (rest tokens))) 

(defn tupleLit? [tokens] (= '(\# \() (take 2 tokens)))
(defn tupleLit [emit tokens] (list nil (concat " ($tuple " (drop 2 tokens))))

(defn pTupleLit? [tokens] (= '(\# \# \() (take 3 tokens)))
(defn pTupleLit [emit tokens] (list nil (concat " ($ptuple " (drop 3 tokens))))

(defn tupleRef? [tokens] (= \[ (first tokens)))
(defn tupleRef [emit tokens] 
  (let [[internal remain] (readUntil emit '(\]) (rest tokens))]
   (list (concat "(tuple-ref " internal ")") remain)))
                          
(defn stMeta? [tokens] (= \{ (first tokens)))
(defn stMeta  [emit tokens] 
  (let [[internal remain] (readUntil emit '(\}) (rest tokens))]
    (list (concat "($meta " internal ")") remain)))

(defn stString? [tokens] (= \" (first tokens)))
(defn stString  [emit tokens]
  ((fn gather [tokens]
     (cond
       (= \\ (first tokens)) (concat (take 2 tokens) (gather (drop 2 tokens)))
       (= \" (first tokens)) (list '(\") (rest tokens)))) 
     tokens))

(defn stList? [tokens] (= \( (first tokens)))
(defn stList [emit tokens] 
  (let [[internal remain] (readUntil emit '(\)) (rest tokens))]
    (list (concat "(" internal ")") remain)))

(defn driver [emit tokens] 
  (if (empty? tokens)
    '(() ())
    (let [[done remain] (emit emit tokens)
          [step leftovers] (driver emit remain)]
      (list (concat done step) leftovers))))

(defn parse-program [src]
  "string -> tree: Parses stencil program from a string."
  (let [[srcLs remain] 
          (driver
            (emitter `((~stComment? ~stComment) (~stMeta? ~stMeta) (~bind? ~bind) 
                       (~stString? ~stString) (~tupleLit? ~tupleLit) (~pTupleLit? ~pTupleLit)
                       (~stList? ~stList) (~tupleRef? ~tupleRef))) 
             src)
        source (apply str srcLs)]
    (read-string source)))

