(defproject free-your-data "0.1.0-SNAPSHOT"
  :description "liberator - free your data with rfc2616 demo for euroclojure 2013"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [liberator "0.9.0"]
                 [compojure "1.1.3"]
                 [hiccup "1.0.2"]]
  :profiles {:dev {:dependencies [[ring/ring-jetty-adapter "1.1.0"]
                                  [ring/ring-devel "1.1.6"]
                                  [org.clojure/tools.namespace "0.2.4"]]
                   :plugins [[lein-ring "0.7.1" :exclusions [org.clojure/clojure]]]}})
