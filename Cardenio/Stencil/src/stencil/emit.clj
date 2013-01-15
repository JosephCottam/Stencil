(ns stencil.emit
  (:require [clojure.java.io :as io]))

(load "emitters/pico")
(load "emitters/cdx")


;; From comment on http://cemerick.com/2009/12/04/string-interpolation-in-clojure/
 (defn template
   [#^String template #^java.util.Map context]
     (let [t (StringTemplate. txt)]
         (.setAttributes t context)
             (.toString t)))
