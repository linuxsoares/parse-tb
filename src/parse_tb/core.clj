(ns parse-tb.core
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [instaparse.core :as insta])
  (:gen-class))

(def tubaina "document = chapter (box | <br> | empty-line+ | paragraph | quote | img | index | label | section | code | title | todo | list)+
              box = <'[box '> (text | italic | monospaced)+ <']'> (<br> | empty-line+ | paragraph | index | code)+ <'[/box]'>
              code = <'[code' ' '?> ('javascript' | 'ruby' | 'clojure' | 'java')? text? (<' label='> text)? <']'> (ns-sym | code-text | br)* <'[/code]'>
              br = <#'([\r]?[\n])+'>
              paragraph = <#'^'> (italic | bold | monospaced | text | ref | ref-label)* (br | <#'$'>)
              quote = <'[quote '> (text | ref | italic | <br>)+ <']'>
              ref = <'[ref '> text <']'>
              ref-label = <'[ref-label '> text <']'>
              index = <'[index '> text <']'>
              label = <'[label '> text <']'>
              todo = <'[TODO '> (text | <br>)+ <']'>
              section = <'[section '> text <']'>
              title = <'[title '> text <']'>
              img = <'[img '> text <']'>
              list = <'[list]'>(text | monospaced | br)+<'[/list]'>
              empty-line = <#'^[ ]*$'>
              chapter = (<'[chapter' ' '?> text? <']'>)
              italic = <'℔'> (bold | text | monospaced)+ <'℔'>
              bold = <'ℨ'> (italic | text | monospaced)+ <'ℨ'>
              monospaced = <'℥'> text <'℥'>
              ns-sym = '℔' code-text
              text = #'[A-ZÁÉÍÓÚÇÃÂÊÎÔÛÀa-z0-9áéíóúçãõâêîôûà.,;:!?&\\-_/\"\\'`’\\(\\)%=#\\*\\+\\<>{}$²^\\\\~@▷◁ ]+'
              code-text = (!'[/code]' !'::' #'[A-ZÁÉÍÓÚÇÃÂÊÎÔÛÀa-z0-9áéíóúçãõâêîôûà.,;:!?&$\\-_/\"\\'\\(\\)%=#\\*\\+\\[\\]<>{}²^\\\\`|~@ ]{0,200}')+")

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
  (let [result   (parse filename tubaina)
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
