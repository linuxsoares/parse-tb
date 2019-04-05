(ns parse-tb.core
  (:require [parse-tb.parser :as parser]
            [clojure.pprint :as pp]
            [instaparse.core :as insta])
  (:gen-class))

(defn- ->intermediate-format [text]
  (parser/parse text))

(defn -main [& args]
  (let [file (first args)
        parse  (insta/parser
                    "document = chapter (br | empty-line+ | paragraph)+
                     br = #'([\r]?[\n])+'
                     paragraph = #'^' (italic | bold | text)+ (br | #'$')
                     empty-line = #'^[ ]*$'
                     chapter = '[chapter ' text ']'
                     italic = &'::' (bold | text)+ '::'
                     bold = '**' (italic | text)+ '**'
                     text = !'::' (#'[A-ZÁÉÍÓÚÇÃÂÊÎÔÛÀa-z0-9áéíóúçãõâêîôûà.,!?\\-/: ]')+
                     ")]
    (-> file
        slurp
        (parse :unhide :all)
        pp/pprint)))

  ; (let [file (first args)]
  ;   (-> file
  ;       slurp
  ;       ->intermediate-format
  ;       pp/pprint)))
