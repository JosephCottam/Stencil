(ns stencil.emit.cdx)

(defn indent [n] (apply str "\n" (take n (repeat " "))))
(defn drop-metas [program] 
  (cond
    (empty? program) program
    (atom? program) program
    (seq? program) (map drop-metas (remove meta? program))))

(defn python-list [list] (apply str "[" (interpose "," list) "]")
  

(defn table[table level]
  (let [name (second table)
        fields (drop-metas (filter-policy table 'fields))
        fields-member (str "_" name "_fields = " (python-list fields))
        long-initArgs (map-indexed #(apply str %1 "=args[" %2 "]") fields)
        long-initTable (str "self." name " = p.make_source(idx=range(len(" (first fields) "))"
                        (apply str (map #(str % " = " %)) fields) ")")
        short-init (str "self." name " = **kwards[" name "]")]
    (map #(str (indent level) %) 
         (list fields-member 
               "def __init__(self, *args, **kwargs):" 
               "if (len(args) == len(fields)):"
               long-initArgs 
               long-initTable 
               "else:"
               short-init))))


(defn classDef [program in]
  (str (indent in) "class " (second program) ":"))

(defn init [program in]
  (str (indent in) "def __init__(self):"
       (let [in (+ 1 in)]
         (indent in) 




(defn cdx 
  [program]
  (let [;program (-> program uniqueNames moveTypes)
        renderer(emitRenderer program)
        tables  (emitTables program)]
    (stitch streams renderer tables)))
  
