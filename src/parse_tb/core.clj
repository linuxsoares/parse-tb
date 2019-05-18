(ns parse-tb.core
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [instaparse.core :as insta]
            [parse-tb.tubaina.grammar :as grammar])
  (:gen-class))

(defn- parse [filename grammar]
  (println "Parsing" filename)

  (let [parser (insta/parser grammar :output-format :enlive)
        result (-> filename
                slurp
                (str/replace #"::" "℔")
                (str/replace #"%%%%%", "℥%℥")
                (str/replace #"%%" "℥")
                (str/replace #"[*]{2}" "ℨ")
                (str/replace #"℥\[\[", "℥▷▷")
                (str/replace #"℥\[", "℥▷")
                (str/replace #"]℥", "◁℥")
                parser)]
    result))

(defn parse-file [filename]
  (println "Parse " filename)
  (let [result   (parse filename grammar/tubaina)
        file   (java.io.File. filename)
        out    (str/replace (.getName file) #"afc" "json")]
    (println out)
    (if (insta/failure? result)
      (pp/pprint result)
      (json/generate-stream result (io/writer (str "json/" out))))))

(defn -main [& args]
  (let [folder (clojure.java.io/file (first args))
        fs (file-seq folder)
        files (map str (rest fs))]
    (doall (map parse-file files))))
