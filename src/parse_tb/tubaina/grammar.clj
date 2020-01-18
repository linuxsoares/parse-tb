(ns parse-tb.tubaina.grammar)

(def tubaina "document = chapter (box | <br> | empty-line+ | paragraph | quote | img | index | label | section | code | title | todo | list)+
              box = <'[box '> (text | italic | monospaced)+ <']'> (<br> | empty-line+ | paragraph | index | code)+ <'[/box]'>
              code = <'[code' ' '?> ('javascript' | 'ruby' | 'clojure' | 'java' | 'html')? text? (<' label='> text)? <']'> (ns-sym | code-text | br)* <'[/code]'>
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
