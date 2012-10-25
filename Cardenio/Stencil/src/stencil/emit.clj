(ns stencil.emit
  (:use stencil.compile)
  (:use [clojure.core.match :only (match)]))

(load "emitters/pico")
(load "emitters/ensureFields")
