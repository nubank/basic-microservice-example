(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [basic-microservice-example.components.system-utils :as system-utils]
            [basic-microservice-example.components :as components]))

(defn system []
  @system-utils/system)

(def init
  "Constructs the current development system."
  components/create-and-start-system!)

(def start
  "Starts the current development system."
  (partial components/ensure-system-up! :base-system))

(def stop
  "Shuts down and destroys the current development system."
  system-utils/stop-system!)

(defn go
  "Initializes the current development system and starts it running."
  []
  (init)
  (start))
