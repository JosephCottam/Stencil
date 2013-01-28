(ns stencil.emit)

(defn emitValue [value] "")
(defn emitCall [call] "")
(defn emitDataPolicy [policy] "")


(defn emitExpr [expr] "")
(defn emitWhen [expr] "")

(defn emitStreams [program] "")
(defn emitRenderer [program] "")
(defn emitTables [program] "")

(defn stitch [streams renderer tables] "done!")


(defn emit 
  "program -> string: Creates a sourcecode to be run with the pico runtime"
  [program]
  (let [;program (-> program uniqueNames moveTypes)
        streams (emitStreams program)
        renderer(emitRenderer program)
        tables  (emitTables program)]
    (stitch streams renderer tables)))
  
