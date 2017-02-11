(defproject dlambda "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [instaparse "1.4.5"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [rhizome "0.2.7"]]
  :main ^:skip-aot dlambda.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
