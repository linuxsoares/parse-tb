(ns parse-tb.core
  (:require [parse-tb.parser :as parser]
            [clojure.pprint :as pp]
            [instaparse.core :as insta]
            [clojure.string :as str])
  (:gen-class))

(defn- ->intermediate-format [text]
  (parser/parse text))

(defn -main [& args]
  (let [file (first args)
        parse  (insta/parser
                    "document = chapter (<br> | empty-line+ | paragraph | quote)+
                     br = <#'([\r]?[\n])+'>
                     paragraph = <#'^'> (italic | bold | text)* (br | <#'$'>)
                     quote = <'[quote '> (text | <br>)+ <']'>
                     empty-line = <#'^[ ]*$'>
                     chapter = <'[chapter '> text <']'>
                     italic = <'℔'> (bold | text)+ <'℔'>
                     bold = <'**'> (italic | text)+ <'**'>
                     text = !'::' #'[A-ZÁÉÍÓÚÇÃÂÊÎÔÛÀa-z0-9áéíóúçãõâêîôûà.,;:!?\\-/\"\\'\\(\\) ]+'
                     ")]
    (-> file
        slurp
        (str/replace #"::" "℔")
        (parse :trace true)
        pp/pprint)))

  ; (let [file (first args)]
  ;   (-> file
  ;       slurp
  ;       ->intermediate-format
  ;       pp/pprint)))
