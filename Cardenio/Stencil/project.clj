(defproject stencil "0.0.1-SNAPSHOT"
  :description "Stencil -- Cardenio remake!"
  :url "http://cs.indiana.edu/~jcottam/stencil.xml"
  :repositories {"local" ~(str (.toURI (java.io.File. "locallib")))}  ;;Because I couldn't find a consistently updated Stringtemplate in external repos
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/core.match "0.2.0-alpha11"]
                 [org.antlr/stringtemplate "4.0.7"]]
  :dev-dependencies [[org.clojure/tools.trace "0.7.3"]])

