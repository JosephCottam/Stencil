(ns stencil.emitters.vega
  (:use [stencil.util])
  (:require [clojure.core.match :refer (match)])
  (:require [stencil.transform :as t])
  (:require [stencil.pprint])
  (import (org.stringtemplate.v4 ST STGroup STGroupFile)))

