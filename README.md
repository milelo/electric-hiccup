# electric-hiccup

[![Clojars][clojars-badge]][clojars]
![Status][status-badge]

`electric-hiccup` provides hiccup-like syntactic sugar for [Electric V2][electric]. 

It allows dom elements supported by `hyperfiddle.electric-dom2` to be expressed as `electric-hiccup`. For non-trivial layouts this can simplify authoring and improve readability.

The `electric-hiccup` syntax can be intermingled with regular electric syntax.

This is an **Alpha release**. Use at your own risk, there will likely be breaking changes.

## Installation

Add the following dependency to your `deps.edn` file:

    milelo/electric-hiccup {:mvn/version "TBD"}

## Usage

Require `[hyperfiddle.electric-dom2]` and `[electric-hiccup.reader]` to use the `#electric-hiccup` tagged-literal to prefix `electric-hiccup` vector expressions.

### Sample code in regular electric syntax

Source: [biff-electric] - [app.cljc]

```clojure
(ns app
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            ))

(e/defn BarForm []
  (dom/div
   (dom/label (dom/props {:for "bar" :class :block})
              (dom/text "Bar: ")
              (dom/span (dom/props {:class "font-mono"})
                        (dom/text (e/server (pr-str (:user/bar user))))))
   (dom/div (dom/props {:class "h-1"}))
   (let [bar (e/server (:user/bar user))
         text (atom bar)]
     (dom/div
      (dom/props {:class "flex"})
      (dom/input
       (dom/props {:class "w-full"
                   :id "bar"
                   :type "text"
                   :value bar})
       (dom/on "keyup" (e/fn [e]
                         (reset! text (-> e .-target .-value))))
       (dom/on "keydown" (e/fn [e]
                           (when (= "Enter" (.-key e))
                             (SetBar. (or @text ""))))))
      (dom/div (dom/props {:class "w-3"}))
      (dom/button
       (dom/props {"class" "btn"
                   "type" :Submit})
       (dom/text "Update")
       (dom/on "click" (e/fn [e]
                         (SetBar. (or @text "")))))))
   (dom/div (dom/props {:class "h-1"}))
   (dom/div
    (dom/props {:class "text-sm text-gray-600"})
    (dom/text "This demonstrates updating a value with Electric."))))

```

### Equivalent sample code making use of `electric-hiccup` syntax

```clojure
(ns app
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [electric-hiccup.reader]
            ))

(e/defn BarForm []
   #electric-hiccup
   [:div
    [:label.block {:for :bar} "Bar: "
     [:span.font-mono
      (dom/text (e/server (pr-str (:user/bar user))))]]
    [:div.h-1]
    (let [bar (e/server (:user/bar user))
          text (atom bar)]
       #electric-hiccup
       [:div.flex
        [:input#bar.w-full {:type :text :value bar}
         (dom/on "keyup" (e/fn [e]
                           (reset! text (-> e .-target .-value))))
         (dom/on "keydown" (e/fn [e]
                             (when (= "Enter" (.-key e))
                               (SetBar. (or @text "")))))]
        [:div.w-3]
        [:button.btn {:type :Submit} "Update"
         (dom/on "click" (e/fn [_e]
                           (SetBar. (or @text ""))))]])
    [:div.text-sm.text-gray-600
     "This demonstrates updating a value with Electric."]])
```

## `electric-hiccup` syntax

`electric-hiccup` is loosely based on [hiccup].

#### HTML tags (supported by `hyperfiddle.electric-dom2`) are represented by a keyword at the start of a vector

```clojure
#electric-hiccup [:div]
```
The keyword-name is prefixed. During compilation this expands to:

```clojure 
(hyperfiddle.electric-dom2/div)
```

#### ID and class-shortcuts

```clojure
#electric-hiccup
[:div#my-id.my-class1.my-class2]
```

Expands to:

```clojure
(hyperfiddle.electric-dom2/div
 (hyperfiddle.electric-dom2/props
  {:class "my-class1 my-class2", :id "my-id"}))
```

#### Optional attributes are specified as a map

```clojure
#electric-hiccup
[:div#my-id.my-class1.my-class2 {:class [my-class3 my-class4]
                                 :id :my-id2 ;overridden by my-id
                                 :property1 :some-value
                                 :property2 (expression)}]
```

Expands to:

```clojure
(hyperfiddle.electric-dom2/div
 (hyperfiddle.electric-dom2/props
  {:class "my-class1 my-class2 my-class3 my-class4",
   :id "my-id",
   :property1 :some-value,
   :property2 (expression)}))
```
#### Content goes after the optional attributes

Supported content types:

* string `""`
* nested electric-hiccup `[]`
* an expression `()`

```clojure
#electric-hiccup
[:div.my-class
  [:div]
  "Hello world"
  (dom/text (expression1))
  (expression2)]
```

Expands to:

```clojure
(hyperfiddle.electric-dom2/div
 (hyperfiddle.electric-dom2/props {:class "my-class"})
 (hyperfiddle.electric-dom2/div)
 (hyperfiddle.electric-dom2/text "Hello world")
 (dom/text (expression1))
 (expression2))
```

Include nested `electric-hiccup` in `(expression2)` with `#electric-hiccup`.

## Alternative representations

### As a macro

```clojure
(ns app
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [electric-hiccup.reader :refer-macros [$<]]
            ))

(e/defn Component []
  ($< [:div "foo"]))
```

### Using your own tagged-literal name

tagged-literal's are global. By convention `#electric-hiccup` is namespaced to avoid conflict.

You can however define an alternative short-form name for your project:

* Create or append to the file `data_readers.cljc` in the root of your source folder.
* Append a new entry to the map: `{ehic electric-hiccup.reader/read-data}`
* The `ehic` entry defines the tag `#ehic`
* Don't forget to use the tags, require [hyperfiddle.electric-dom2] and [electric-hiccup.reader]


## License

[biff-electric]: https://github.com/jacobobryant/biff-electric
[app.cljc]: https://github.com/jacobobryant/biff-electric/blob/master/src/com/biffweb/examples/electric/app.cljc
[hiccup]: https://github.com/weavejester/hiccup
[hiccup-wiki]: https://github.com/weavejester/hiccup/wiki
[hiccup-api]: http://weavejester.github.io/hiccup
[electric]: https://github.com/hyperfiddle/electric

[license]: #license

[clojars-badge]: https://img.shields.io/clojars/v/luchiniatwork/hiccup-for-electric.svg
[clojars]: http://clojars.org/milelo/electric-hiccup

[status-badge]: https://img.shields.io/badge/project%20status-prod-brightgreen.svg