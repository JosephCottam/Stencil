(ns stencil.emitters.cdx
  (require [stencil.transform :as t]))

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

(defn python-list [ls] (apply str "[" (apply str (interpose "," ls)) "]"))
  
(defn emit-render [render table]
  (let [render (drop-metas render)
        scatter (= "SCATTER" (.toUpperCase (str (second render))))
        pairs (t/lop->map (drop 2 render))
        x (pairs 'x)
        y (pairs 'y)
        color (pairs 'color)]
    (str "p.plot('" x "', '" y "', color='" color "', data_source=" table ", scatter=" (pyBool scatter) ")")))
        

(defn emit-table[table]
  (let [name (second table)
        fields (rest (drop-metas (first (t/filter-policies 'fields table))))
        fields-member (str "_" name "_fields = " (python-list fields))
        long-initArgs (map-indexed #(str (indent 1) (str %2 "=args[" %1 "]")) fields)
        long-initInner  (print-str (interpose ", " (map #(str % "=" %) fields)) ")")
        long-initTable (str "self." name " = p.make_source(idx=range(len(" (first fields) ")), " 
                            (.substring long-initInner 1 (- (.length long-initInner) 2)))
        short-init (str "self." name " = **kwards[" name "]")
        renderDef (emit-render (t/filter-policies 'render table) name)]
         `(~fields-member 
             "def __init__(self, *args, **kwargs):" 
             "if (len(args) == len(fields)):"
             ~@(map #(str (indent 1) %) long-initArgs)
             ~(str (indent 1) long-initTable)
             "else:"
             ~(str (indent 1) short-init)
             ~renderDef)))

(defn emit-classDef [program]
  (list (str "class " (second program) ":")))

(defn header [program]
  (list "from webplot import p" 
        "import numpy as np"
        "import datetime"
        "import time"))

(defn footer [program]
  (list "x = np.arange(100) / 6.0" 
        "y = np.sin(x)"
        "z = np.cos(x)"
        "plot = Scatterplot(x,y,z)"))

(defn cdx [program]
    (let [classDef (emit-classDef program)
          tableDefs (emit-table (first (t/filter-policies 'table program)))
          header (header program)
          footer (footer program)]
      (apply str (interpose "\n" (concat header 
                                         '("\n") 
                                         classDef 
                                         (map #(str (indent 1) %) tableDefs) 
                                         '("\n") 
                                         footer)))))


