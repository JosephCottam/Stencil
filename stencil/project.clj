
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
  :dev-dependencies [[org.clojure/tools.trace "0.7.3"]]
 
  :jar-name "stencil-core.jar"
  :uberjar-name "stencil.jar"

  :deploy-branches ["master"]
  :min-lein-version "2.0.0")

;;To install (stringtemplate, for example) to the local repo, run the following in the "stencil" subdirectory of the repo:
;;mvn install:install-file -Dfile=ST-4.0.7.jar -DgroupId=org.stringtemplate -DartifactId=v4 -Dversion=4.0.7 -Dpackaging=jar -DlocalRepositoryPath=locallib -DcreateChecksum=true
;;NOTE: The -dfile=<path> is particular about paths and maven is not communicative about those issues.
;;      For example, it will claim successful install with -Dfile=~/Downloads/ST-4.0.7.jar" but not actually copy the jar file.
;;      The remedy seems to be not using any "fancy" items in the path, like "~" or "..".  Absolute paths only.
;;      The above example assumes that the file has been copied "stencil" subdirectory of this repo.
