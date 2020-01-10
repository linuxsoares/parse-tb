(ns parse-tb.vue
  (:require [clojure.string :refer [trim]])
  (:gen-class))

(defn debug [value]
  (println ">>>>>>>>>>>>>>>>>>>>>>>>>>>" value))

(defmulti parse (fn [node]
                  (:tag node)))

(defmethod parse :br [_])

(defmethod parse :chapter [{:keys [content]}]
  (str "<h1>" (parse (first content)) "</h1>\n"))

(defmethod parse :document [{:keys [content]}]
  (str "<template>\n<page>\n"
    (apply str (map parse content))
    "</page>\n</template>\n"))

(defmethod parse :label [{:keys [content]}]
  (str "<index entry=\"" (parse (first content)) "\" />\n"))

(defmethod parse :paragraph [{:keys [content]}]
  (let [lines (apply str (map parse content))]
    (when-not (empty? lines)
      (str "<p class=\"text-justify\">" lines "</p>\n"))))

(defmethod parse :quote [{:keys [content]}]
  
  ; (str "<chapter-quote author="Alan Perlis" work=", primeiro ganhador do Turing Award">É melhor termos 100 funções operando em uma estrutura de dados do que 10 funções operando em dez estruturas</chapter-quote>)
  (debug content)
  )

(defmethod parse :text [{:keys [content]}]
  (-> content
      first
      trim))

(defmethod parse :default [{:keys [tag]}]
  (println "Tag " tag " not available yet"))

(defn generate! [node file]
  (spit "../descobrindoclojure/src/components/book/Data.vue"
    (-> node
        parse
        (str "<script>export default { name: 'CHANGEME' }</script>"))))
