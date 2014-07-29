(defproject jjgrep "0.0.1"
  :description "This is me toying with clojure and should not be used for anything ever."
  :url "github.com/ploubser/jjgrep-toy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.5"]
                 [org.clojure/tools.cli "0.2.4"]]
  :main ^:skip-aot jjgrep.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
