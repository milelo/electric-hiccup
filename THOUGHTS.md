## Discussions on the electric-hiccup approach

electric-hiccup is mainly compile time data transformation, it doesn't have a runtime overhead except some minor html class-attribute processing which can potentially be circumvented if desired.

The authoring overhead to prefix the hiccup-vectors with #electric-hiccup is minor.

```clojure
(e/defn Component []
  (e/client
   #electric-hiccup
    [:div "Foo"
      [:div "Bar"]
      (let [...]
        #electric-hiccup
        [:div "Baz"]
      )
     ]))
```

Although adding the #electric-hiccup prefix to the source is a small overhead, when hiccup children are regular statements that return hiccup, like the example above, it is worth considering if the `#electric-hiccup` tag on the returned hiccup data could be somehow eliminated. I see two potential ways of doing this.

### Runtime transformation of the returned hiccup

At runtime the expression will be executed and return the hiccup data structure, so it could theoretically be intercepted and turned into electric code. However the hiccup data would now have to be parsed at runtime and some code generated. In the worst case scenario the transformed data would then need to be evaluated at runtime which would need an embedded compiler. There may be many simplifications to this scenario but the main point is, there will be some runtime overhead so I don't consider this a worthy option.

### Static code analysis

We could try to write a code walker for hiccup children, that are clojure expressions, to identify the hiccup return data so it can be transformed into regular electric code.

If such static code walking was easy for the general case, I would have expected such a library to already exist, it would be useful, for example, to provide code type information. I haven't come across a library so I infer there isn't an easy solution. 

It would be possible to constrain the supported language statements that are available in child expressions to those that are helpful such that the expressions could be statically analysed.

My conclusion is, I don't think it is worth the effort supporting expression code analysis in this library, to eliminate the need for a few extra `#electric-hiccup` tags in the code. I do recognise however that if electric clojure wishes to support hiccup natively, I think this will be needed.

## Native electric support for hiccup

The same issues apply, except `#electric-hiccup` tags wont be available to identify mid-expression hiccup, electric would be forced to use a supported subset of language statements that can be statically analysed, with the aim of identifying the hiccup data.