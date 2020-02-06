(defproject authnet-clj "0.1.0"
  :description "Native Clojure client for Authorize.Net JSON API"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "3.10.0"]
                 [cheshire "5.9.0"]
                 ]
  :main ^:skip-aot authnet-clj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
          :dev {
                   :dependencies [[lein-marginalia "0.9.1"]]
                   :plugins [[lein-marginalia "0.9.1"]]
                   }

             })
