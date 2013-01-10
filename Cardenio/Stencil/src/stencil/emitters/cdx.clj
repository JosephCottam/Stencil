(ns stencil.emit.cdx)

(defn emitValue [value] "")
(defn emitCall [call] "")
(defn emitDataPolicy [policy] "")


(defn emitExpr [expr] "")
(defn emitWhen [expr] "")
(defn emitUsing [program] "")
(defn emitLet [program] "")

(defn emitStreams [program] (throw (RuntimeException. "Cannot do streams in CDX.")))
(defn emitRenderer [program] "")
(defn emitTables [program] "")

(defn stitch [streams renderer tables] "done!")

(defn cdx 
  [program]
  (let [;program (-> program uniqueNames moveTypes)
        streams (emitStreams program)
        renderer(emitRenderer program)
        tables  (emitTables program)]
    (stitch streams renderer tables)))
  
