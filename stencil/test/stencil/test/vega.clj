(ns stencil.test.vega
  (:require [stencil.emitters.vega :as vega])
  (:require [stencil.core :as c])
  (:require [stencil.transform :as t])
  (:require [clojure.data.json :as json])
  (:require [clojure.set :as set])
  (:use [clojure.test])
  (:use [stencil.test.utils]))


(def source "../tests/vega/")
(def target (freshDir "../testResults"))

(defmethod assert-expr 'emit-eq? [msg form]
  `(let [emitter# ~(nth form 1)
         base# ~(nth form 2)
         src# (str source "/" base# ".stencil")
         ref# (str source "/" base# ".js")
         rslt# (str target "/" base# ".js")
         result# (json/read-str (.trim (emit emitter# target src# rslt#)))
         expected# (json/read-str (.trim (slurp ref#)))]
     (if (= result# expected#)
       (report {:type :pass :message ~msg, :expected ref# :actual rslt#})
       (report {:type :fail :message 
                (str "Compile did not match --- " ~msg "\n" )
                :expected ref# :actual rslt#}))
     result#))        

(deftest vega
  (binding 
    [t/*default-runtime* 'VegaRuntime]
    (is (emit-eq? vega/emit "bars")
        "Bar chart")))

