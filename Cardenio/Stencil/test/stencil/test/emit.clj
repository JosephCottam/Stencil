(ns stencil.test.emit
  (:require [stencil.emit :as e])
  (:require [stencil.emitters.cdx :as cdx])
  (:require [stencil.core :as core])
  (:require [stencil.compile :as c])
  (:use [clojure.test]))

(def base "../tests/")

(deftest cdx
    (is (= (.trim (cdx/cdx (c/compile (core/read (str base "cdx/scatterplot-inline.stencil")))))
                    (.trim (slurp (str base "cdx/scatterplot-inline.py"))))
        "Scatterplot: One table, inline render")
    (is (= (.trim (cdx/cdx (c/compile (core/read (str base "cdx/scatterplot-twoTable.stencil")))))
                    (.trim (slurp (str base "cdx/scatterplot-twoTable.py"))))
        "Scatterplot: Two tables, inline render"))
