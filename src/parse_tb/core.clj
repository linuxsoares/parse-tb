(ns parse-tb.core
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [instaparse.core :as insta]
            [parse-tb.tubaina.grammar :as grammar]
            [parse-tb.vue :as vue])
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

(defn- generate-vue [result file]
  )

(defn- write-files [result file]
  (let [json-file (str/replace (.getName file) #"afc" "json")
        vue-file  (str/replace (.getName file) #"afc" "vue")]
    (println json-file)
    (json/generate-stream result (io/writer (str "json/" json-file)))
    (vue/generate! result (str "vue/" vue-file))))

(defn- parse-file [filename]
  (println "Parse " filename)
  (let [file   (java.io.File. filename)
        result (when (.isFile file) (parse filename grammar/tubaina))
        ; _ (pp/pprint result)
        ]
    (if (insta/failure? result)
      (pp/pprint result)
      (write-files result file))))

(defn- parse-folder [folder]
  (let [fs (file-seq folder)
        files (map str (rest fs))]
    (doall (map parse-file files))))

(defn -main [& args]
  (let [f (clojure.java.io/file (first args))]
    (if (.isDirectory f)
      (parse-folder f)
      (parse-file (.getAbsolutePath f)))))
