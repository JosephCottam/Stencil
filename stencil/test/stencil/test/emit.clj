(ns stencil.test.emit
  (:require [stencil.emitters.cdx :as cdx])
  (:require [stencil.core :as c])
  (:use [clojure.test]))

(def base "../tests/")

(deftest cdx
    (is (= (.trim (cdx/emit (c/compile (str base "cdx/scatterplot-inline.stencil"))))
           (.trim (slurp (str base "cdx/scatterplot-inline.py"))))
        "Scatterplot: One table, inline render")
    (is (= (.trim (cdx/emit (c/compile (str base "cdx/scatterplot-twoTable.stencil"))))
           (.trim (slurp (str base "cdx/scatterplot-twoTable.py"))))
        "Scatterplot: Two tables, inline render"))
