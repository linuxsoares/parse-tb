(ns parse-tb.vue
  (:require [clojure.string :as str])
  (:gen-class))

(def chapnum (atom 0))
(def secnum (atom 0))
(def titnum (atom 0))

(defn debug [value]
  (println ">>>>>>>>>>>>>>>>>>>>>>>>>>>" value))

(defmulti parse (fn [node]
                  (:tag node)))

(defmethod parse :bold [{:keys [content]}]
  (str "<b>" (parse (first content)) "</b>"))

(defmethod parse :box [{:keys [content]}]
  (let [title (first content)
        body  (rest content)]
    (str "<box title=\"" (parse title) "\">\n"
         (apply str (map parse body))
         "</box>\n")))

(defmethod parse :br [_])

(defmethod parse :chapter [{:keys [content]}]
  (reset! secnum 0)
  (reset! titnum 0)
  (str "<h1>" (str @chapnum ". " (parse (first content))) "</h1>\n"))

(defmethod parse :code [{:keys [content]}]
  (let [language (first content)
        body     (rest content)]
    (str "<snippet language=\"" language "\">"
         (apply str (map parse body))
         "</snippet>\n")))

(defmethod parse :code-text [{:keys [content]}]
  (str (str/replace (first content) #"<" "&lt;") "\n"))

(defmethod parse :document [{:keys [content]}]
  (str "<template>\n<page>\n<wip />"
    (apply str (map parse content))
    "</page>\n</template>\n"))

(defmethod parse :italic [{:keys [content]}]
  (str "<i>" (parse (first content)) "</i>"))

(defmethod parse :label [{:keys [content]}]
  (str "<label entry=\"" (parse (first content)) "\" />\n"))

(defmethod parse :monospaced [{:keys [content]}]
  (str "<code>" (parse (first content)) "</code>"))

(defmethod parse :ns-sym [{:keys [content]}]
  (str "&colon;&colon;" (parse (second content))))

(defmethod parse :paragraph [{:keys [content]}]
  (let [lines (apply str (map parse content))]
    (when-not (empty? lines)
      (str "<p class=\"text-justify\">" lines "</p>\n"))))

(defmethod parse :quote [{:keys [content]}]
  (let [quote  (parse (first content))
        who    (str/split (parse (second content)) #",")
        author (str/trim (str/replace (first who) #"--" ""))
        work   (some->> who
                        second
                        (str ","))]
    (str "<chapter-quote author=\"" author "\" work=\"" work "\">" quote "</chapter-quote>\n")))

(defmethod parse :section [{:keys [content]}]
  (swap! secnum inc)
  (reset! titnum 0)
  (str "<h2>" (str @chapnum "." @secnum ". " (parse (first content)) "</h2>\n")))

(defmethod parse :text [{:keys [content]}]
  (first content))

(defmethod parse :title [{:keys [content]}]
  (swap! titnum inc)
  (str "<h3>" (str @chapnum "." @secnum "." @titnum ". " (parse (first content)) "</h3>\n")))

(defmethod parse :default [{:keys [tag]}]
  (println "Tag " tag " not available yet"))

(defn generate! [node file]
  (reset! chapnum (Integer/parseInt (last (re-find #"^vue\/(\d+)" file))))
  (println "File: " file)
  (spit "../descobrindoclojure/src/components/book/Data.vue"
    (-> node
        parse
        (str "<script>export default { name: 'CHANGEME' }</script>"))))
