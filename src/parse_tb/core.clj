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
                    "document = chapter (box | <br> | empty-line+ | paragraph | quote | img | index | label | section | code | title | todo | list)+
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
                     code-text = (!'[/code]' !'::' #'[A-ZÁÉÍÓÚÇÃÂÊÎÔÛÀa-z0-9áéíóúçãõâêîôûà.,;:!?&$\\-_/\"\\'\\(\\)%=#\\*\\+\\[\\]<>{}²^\\\\`|~@ ]{0,200}')+
                     ")
        result (-> file
                slurp
                (str/replace #"::" "℔")
                (str/replace #"%%%%%", "℥%℥")
                (str/replace #"%%" "℥")
                (str/replace #"[*]{2}" "ℨ")
                (str/replace #"℥\[\[", "℥▷▷")
                (str/replace #"℥\[", "℥▷")
                (str/replace #"]℥", "◁℥")
                (parse) ; :trace true)
                #_pp/pprint)]
    (println "Parsing" file)
    (when (insta/failure? result)
      (pp/pprint result))))

  ; (let [file (first args)]
  ;   (-> file
  ;       slurp
  ;       ->intermediate-format
  ;       pp/pprint)))
