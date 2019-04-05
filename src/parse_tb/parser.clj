(ns parse-tb.parser
  (:require [clojure.string :as str]))

(defn- ->chapter [lines]
  (let [target (first lines)
        document (rest lines)]
    (re-matches )
    ))

(defn parse [text]
  (-> text
      (str/split #"([\r]?\n){2}")
      (->chapter)))
