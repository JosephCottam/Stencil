(ns stencil.emitters.vega
  (:use [stencil.util])
  (:require [clojure.core.match :refer (match)])
  (:require [stencil.transform :as t])
  (:require [stencil.pprint])
  (import (org.stringtemplate.v4 ST STGroup STGroupFile)))


(defn emit-vega [template]
  (let [g (STGroupFile. "src/stencil/emitters/vega.stg")
        t (.getInstanceOf g template)]
    (.render (.add t "program" "def"))))

(defn emit [program]
    (-> program 
      propagate-source
      scale-defs
      scale-uses
      tables
      guides
      top-level-defs
      as-dictionaries
      emit-vega))
