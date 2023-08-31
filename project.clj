(defproject dev.gethop/notifications.firebase "0.1.12"
  :description "A duct library for managing notifications with Firebase Cloud Messaging"
  :url "http://github.com/gethop-dev/notifications.firebase"
  :min-lein-version "2.9.8"
  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [duct/logger "0.3.0"]
                 [integrant "0.8.0"]
                 [com.google.firebase/firebase-admin "8.1.0"]]
  :deploy-repositories [["snapshots" {:url "https://clojars.org/repo"
                                      :username :env/CLOJARS_USERNAME
                                      :password :env/CLOJARS_PASSWORD
                                      :sign-releases false}]
                        ["releases"  {:url "https://clojars.org/repo"
                                      :username :env/CLOJARS_USERNAME
                                      :password :env/CLOJARS_PASSWORD
                                      :sign-releases false}]]
  :profiles {:dev [:project/dev :profiles/dev]
             :profiles/dev {}
             :project/dev {:plugins [[jonase/eastwood "1.2.3"]
                                     [lein-cljfmt/lein-cljfmt "0.8.0"]]}
             :repl {:repl-options {:init-ns dev.gethop.notifications.firebase
                                   :host "0.0.0.0"
                                   :port 4001}}
             :eastwood {:linters [:all]
                        :debug [:progress :time]}})
