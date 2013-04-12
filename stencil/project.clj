
(defproject stencil "0.0.1.3-SNAPSHOT"
  :description "Stencil -- Cardenio remake!"
  :main stencil.core
  :url "http://cs.indiana.edu/~jcottam/stencil.xml"
  :repositories {"local" ~(str (.toURI (java.io.File. "locallib")))}  ;;Because I couldn't find a consistently updated Stringtemplate in external repos
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.json "0.2.2"]
                 [org.clojure/tools.reader "0.7.3"]
                 [org.clojure/core.match "0.2.0-alpha11"]
                 [org.stringtemplate/v4 "4.0.7"]]
  :dev-dependencies [[org.clojure/tools.trace "0.7.3"]])


;;To install (stringtemplate, for example) to the local repo:
;;mvn install:install-file -DgroupId=org.stringtemplate -DartifactId=v4 -Dversion=4.0.7 -Dpackaging=jar -Dfile=~/Downloads/stringtemplate-4.0.7.jar -DlocalRepositoryPath=locallib -DcreateChecksum=true
