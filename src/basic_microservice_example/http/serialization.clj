(ns basic-microservice-example.http.serialization
  (:require [cheshire.core :refer [parse-string generate-string]]
            [clojure.edn :as edn]
            [clojure.walk :as walk]
            [clojure.string :as str]
            [schema.core :as s]))

(def string-or-keyword (s/if keyword? s/Keyword s/Str))

(s/defn ^:private replace-char :- s/Keyword
  ;; Replaces the from character with the to character in s, which can be a String or a Keyword
  ;; Does nothing if s is a keyword that is in the exception set
  [s :- string-or-keyword, from :- Character, to :- Character, exceptions :- #{s/Keyword}]
  (if (contains? exceptions s)
    s
    (keyword (str/replace (name s) from to))))

(def underscore->dash-exceptions #{})

(s/defn ^:private replace-char-gen :- (s/pred fn?)
  ;; Will replace dashes with underscores or underscores with dashes for the keywords in a map
  ;; Ignores String values in a map (both keys and values)
  ([from :- Character, to :- Character] (replace-char-gen from to #{}))
  ([from :- Character, to :- Character, exceptions :- #{s/Keyword}]
    #(if (keyword? %) (replace-char % from to exceptions) %)))

(defn dash->underscore [json-doc]
  (walk/postwalk (replace-char-gen \- \_) json-doc))

(defn underscore->dash [json-doc]
  (walk/postwalk (replace-char-gen \_ \- underscore->dash-exceptions) json-doc))

(defn write-json [data]
  (-> data
      dash->underscore
      generate-string))

(defn read-json [data]
  (-> data
      underscore->dash
      (parse-string true)))

(s/defn read-edn [v :- s/Str]
  (if (string? v) (edn/read-string {:readers *data-readers*} v) v))

(s/defn write-edn :- s/Str
  [v :- (s/pred coll?)]
  (pr-str v))
