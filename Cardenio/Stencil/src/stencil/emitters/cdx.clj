;; A ***VERY**** hacked up emitter
;; DON'T pattern your own emitters off of this.  Do much better.

(ns stencil.emitters.cdx
  (:require [clojure.core.match :refer (match)])
  (:require [stencil.transform :as t])
  (:require [clojure.java.io :as io]))

(defn indent [n] (apply str (take n (repeat "  "))))
(defn drop-metas [program] 
  (cond
    (t/atom? program) program
    (empty? program) program
    (seq? program) (map drop-metas (remove t/meta? program))))

(defn pyBool [b]
  (cond
    b "True"
    (not b) "False"
    :else (throw (RuntimeException. "Not a boolean:" b))))

(defn pyName [name]
  (symbol (.replaceAll (str name) "-|>|<|\\?|\\*" "_")))

(defn python-list [ls] (apply str "[" (apply str (interpose "," (map #(str \" % \") ls))) "]"))
  
(defn emit-render [render table]
  (let [render (drop-metas render)
        scatter (= "SCATTER" (.toUpperCase (str (second render))))
        pairs (t/lop->map (rest (nth render 2)))
        x (pairs 'x)
        y (pairs 'y)
        color (pairs 'color)]
    (str "p.plot('" x "', '" y "', color='" color "', data_source=self." table ", scatter=" (pyBool scatter) ")")))
        

(defn emit-table[table]
  (let [name (pyName (second table))
        fields (rest (drop-metas (first (t/filter-policies 'fields table))))
        fields-member-name (str "_" name "_fields")
        fields-member (str fields-member-name" = " (python-list fields))
        long-initArgs (map-indexed #(str %2 "=args[" %1 "]") fields)
        long-initInner (print-str (interpose ", " (map #(str % "=" %) fields)) ")")
        long-initTable (str "self." name " = p.make_source(idx=range(len(" (first fields) ")), " 
                            (.substring long-initInner 1 (- (.length long-initInner) 2)))
        short-init (str "self." name " = kwards[" name "]")
        renderDef (emit-render (first (t/filter-policies 'render table)) name)]
         `(~fields-member 
             "def __init__(self, *args, **kwargs):" 
             ~(str (indent 1) "if (len(args) == len(self."  fields-member-name ")):")
             ~@(map #(str (indent 2) %) long-initArgs)
             ~(str (indent 2) long-initTable)
             ~(str (indent 1) "else:")
             ~(str (indent 2) short-init)
             ~(str (indent 1) renderDef))))

(defn emit-classDef [program]
  (list (str "class " (pyName (second program)) ":")))

(defn header [program]
  (list "from webplot import p" 
        "import numpy as np"
        "import datetime"
        "import time"))

(defn footer [program]
  (let [name (pyName (second program))]
    (list "x = np.arange(100) / 6.0" 
          "y = np.sin(x)"
          "z = np.cos(x)"
          (str "plot = " name "(x,y,z)"))))

(defn cdx 
  ([file program] 
   (with-open [wrtr (io/writer file)]
     (.write wrtr (cdx program))))
  ([program]
    (let [classDef (emit-classDef program)
          tableDefs (emit-table (first (t/filter-policies 'table program)))
          header (header program)
          footer (footer program)]
      (apply str (interpose "\n" (concat header 
                                         '("\n") 
                                         classDef 
                                         (map #(str (indent 1) %) tableDefs) 
                                         '("\n") 
                                         footer))))))

