;Based roughly on -- http://thingsaaronmade.com/blog/writing-an-s-expression-parser-in-ruby.html 
(ns stencil.rparse
  "Not-quite simple parser.
   Takes in balanced-delimeter trees and eventually produces s-expressions from them.
   Has a simple 'reader-macro' system, so the inputs can be not-quite s-expressions.")

(defn insertAlters [sentinel tokens alters]
  (cond 
    (empty? alters) tokens
    (empty? tokens) '()
    (= sentinel (first tokens)) (cons (first alters) (insertAlters sentinel (rest tokens) (rest alters)))
    :else (cons (first tokens) (insertAlters sentinel (rest tokens) alters))))

(defn makeTree [tokens open close emit]
   "Produce an s-expression using open/close to do nesting and transforms to patch over non-compliant regions."
  (cond 
    (empty? tokens) 
       '(() ())

    (re-matches open (first tokens))
       (let [[phase1 remain] (makeTree (rest tokens) open close emit)
             [phase2 remain] (makeTree remain open close emit)]
         (list (cons phase1 phase2) remain))

    (re-matches close (first tokens))
       (list '() (rest tokens))
    
    :else (let [[phase1 remain] (emit tokens)
                [phase2 remain] (makeTree remain open close emit)]
            (if (nil? phase1)
              (list phase2 remain)
              (list (cons phase1 phase2) remain)))))
         


(defn emitter [transforms]
  "Produce a function that take tokens are returns value/token-seq pairs from the transforms"
  (fn [tokens]
    (loop [transforms transforms]
      (let [[p t] (first transforms)]
        (cond
          (and (fn? t) (p tokens)) (t tokens)
          (nil? t) (list (read-string (first tokens)) (rest tokens))
          :else (recur (rest transforms)))))))
          
(defn parse [src split exclude placeHolder open close transforms]
   "Convert a source string into an s-expression.
    split -- divide up tokens, must include white-space (sorry!)
    exclude -- finds large blocks where split should not be applied (like string-literals)
    placeHolder -- used to protect excluded items, MUST NOT be divdied up by split
    open/close -- patterns for moving up and down in the tree, must include any possible match in one group
    transforms -- list of pred/trans pairs.  If the pred passes, trans is applied to the curent token stream.
                  Trans returns a value and a modified token stream. If the value is NOT nil, it is used in the result."
  (let [bigTokens (re-seq exclude src)
        protected (clojure.string/replace src exclude placeHolder)
        expanded  (.trim (clojure.string/replace (clojure.string/replace protected open " $1 ") close " $1 "))
        pTokens   (clojure.string/split expanded split)
        tokens    (insertAlters placeHolder pTokens bigTokens)
        [tree _]  (makeTree tokens open close (emitter transforms))]
     tree))


(defn bind? [tokens] (.equals ":" (first tokens)))
(defn bind [tokens] (list (read-string "$op:") (rest tokens))) 
(defn tupleLit? [tokens] (= (list "#" "(") (take 2 tokens)))
(defn tupleLit [tokens] (list nil (cons "(" (cons "$tuple" (rest (rest tokens))))))


(defn parsePrograms [src]
  "string -> [tree]: Parses stencil programs from a string."
  (parse src #"[\s,]+" #"\"(?:[^\"\\]|\\.)*\"" "__++STRING_LITERAL++__" #"(\(|\{|\[)" #"(\)|\}|\])" 
         `((~tupleLit? ~tupleLit) (~bind? ~bind))))


