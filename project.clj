(defproject dcript "0.1.0-SNAPSHOT"
  :description "Clojurecsript library for helping to solve substitution ciphers"
  :url "https://github.com/wuhounited/dcript"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2511"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.om/om "0.8.0"]]

  :plugins [[lein-cljsbuild "1.0.4"]]

  :source-paths ["src" "target/classes"]

  :clean-targets ["out/dcript" "public/dev/dcript.js" "public/prod/dcript.js"]

  :cljsbuild {:builds [{:id "prod"
                        :source-paths ["src"]
                        :compiler {
                                   :output-to "public/prod/dcript.js"
                                   :output-dir "out-prod"
                                   :optimizations :advanced
                                   :preamble ["react/react.min.js"]}}
                       {:id "dev"
                        :source-paths ["src"]
                        :compiler {
                                   :output-to "public/dev/dcript.js"
                                   :output-dir "out"
                                   :optimizations :none
                                   :source-map true}}]})
