(defproject basic-microservice-example "0.0.1-SNAPSHOT"
  :description "Very simplistic example of how nubank organizes and tests microservices"
  :url "https://github.com/nubank/basic-microservice-example"
  :license {:name "Apache License, Version 2.0"}
  :plugins [[lein-midje "3.2.1"]]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [io.pedestal/pedestal.service "0.5.7"]
                 [io.pedestal/pedestal.jetty "0.5.7"]

                 [http-kit "2.3.0"]
                 [com.stuartsierra/component "0.4.0"]
                 [prismatic/schema "1.1.12"]
                 [cheshire "5.9.0"]
                 [io.aviso/pretty "0.1.37"]

                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.29"]
                 [org.slf4j/jcl-over-slf4j "1.7.29"]
                 [org.slf4j/log4j-over-slf4j "1.7.29"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "basic-microservice-example.server/run-dev"]}
                   :dependencies [[midje "1.9.9"]
                                  [nubank/selvage "0.0.1"]
                                  [nubank/matcher-combinators "1.2.5"]
                                  [io.pedestal/pedestal.service-tools "0.5.7"]]}
             :uberjar {:aot [basic-microservice-example.server]}}
  :main ^{:skip-aot true} basic-microservice-example.server)

