(ns stencil.compile 
  "Home of the stencil compiler.  Coordiates tree transforms into a final form."
  (:require [stencil.transform :as t]))

(defn compile [program]
  "Compile a stencil program"
  (let [_ (t/validate program)
        program (t/normalize program)
        modules (t/imports program)
        program (t/prep-emit program)]
     program))
