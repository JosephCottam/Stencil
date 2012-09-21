(ns stencil.rparse
  "Not-quite simple parser.
   Takes in balanced-delimeter trees and eventually produces s-expressions from them.
   Has a simple 'reader-macro' system, so the inputs can be not-quite s-expressions.")

(defn parse [source split exclude open close transforms]
   "Convert a source string into an s-expression.
    Tokens are split appart using split, but regions that match exclude will be returned as a single block
    open/close -- tokens corresponding to "down" and "up" in the tree
    transforms -- list of pred/trans pairs.  If the pred passes, trans is applied and its result placed in the tree."
   (-> source 
      (tokenize split exclude)
      (makeTree delim transforms)))


(defn tokenize [source split exclude]
   "Produce an sequence of tokens."
    nil)


(defn makeTree [tokens open close transforms]
   "Produce an s-expression using open/close to do nesting and transforms to patch over non-compliant regions."
    nil)
          

