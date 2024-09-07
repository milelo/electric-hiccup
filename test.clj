;bb tests.clj
(ns test
  (:require
   [electric-hiccup.reader :refer [$<]]
   [clojure.walk :as walk]
   [clojure.pprint :refer [pprint]]))

(defn expand
  "Expand the 'quoted hiccup - test and debug support."
  [hiccup]
  (try 
    (walk/prewalk (fn [x]
                    (if (and (seq? x) (-> x first (= 'electric-hiccup.reader/$<)))
                      (macroexpand-1 x)
                      x)) `(electric-hiccup.reader/$< ~hiccup))
    (catch java.lang.AssertionError e (ex-message e))
    ))

(def tests 
  '[[:div]
    [:div#id1 {:id id2}]
    [:div#id2.my-div {:p1 :p1-value :class [:other-classes]}
     [:div]
     (prn "Hello")
     "Sign out"]
    [:div#id]
    [:#id]
    [:.c1.c2]
    [:#id.c1]
    [:.#id.c1]
    [:.c1#id]
    [:button.btn {:type :Submit}
     "Update"
     (dom/on "click" (e/fn [e]
                       (SetBar. (or @text ""))))]
    [:div.c1 {:class "c2 c3"}]
    [:div {:class [:c1 :c2]}]
    [:div {:class ["c1" "c2"]}]
    [:div {:class :c1.c2}]
    [:div.c0 {:class [:c1 :c2]}]
    [:div.c0 {:class ["c1" "c2"]}]
    [:div.c0 {:class :c1.c2}]
    [:div.flex]
    [:div.flex
     [:div "flex-div"]
     [:input#bar.w-full {:type :text :value bar}
      (dom/on "keyup" (e/fn [e]
                        (reset! text (-> e .-target .-value))))
      (dom/on "keydown" (e/fn [e]
                          (when (= "Enter" (.-key e))
                            (SetBar. (or @text "")))))]
     [:div.w-3]
     [:button.btn {:type :Submit} "Update"
      (dom/on "click" (e/fn [_e]
                        (SetBar. (or @text ""))))]]
    [:div#my-id.my-class1.my-class2]
    [:div.my-class [:div] "Hello world" (expression)]
    [:div#my-id.my-class1.my-class2 {:class [:my-class3 :my-class4]
                                     :id :my-id2 ;override my-id
                                     :property1 :some-value
                                     :property2 (expression)}]
    [:button.text-blue-500.hover:text-blue-800 ;mid keyword ":" is accepted
     {:type :submit} "Sign out"]
    [:div {:class :my-class.my-class2}]
    [:div {:class (str "a" "-b")}]
    [:div.x {:class (str "a" "-b")}]
    [:div.class1 {:class (get-classes :my-key)}]
    [:div.a {:class '(:b :c)} "list"]
    [:dom/.c]
    [:svg/svg {:viewBox "0 0 300 100"}
     [:svg/circle {:cx 50 :cy 50 :r (+ 30 offset)
                   :style {:fill "#af7ac5 "}}]
     [:svg/g {:transform
              (str "translate(105,20) rotate(" (* 3 offset) ")")}
      [:svg/polygon {:points "30,0 0,60 60,60"
                     :style {:fill "#5499c7"}}]]
     [:svg/rect {:x 200 :y 20 :width (+ 60 offset) :height (+ 60 offset)
                 :style {:fill "#45b39d"}}]]
    [:div (when true [:div])]
    [:div#id.c1.c2 "Hello" [:div [:div "inner"]]]
    [:div#id.c1.c2 "Hello" [:div [:div (dom/text "text")]]]
    [:div.c#id]
    (str)
    [:DIV#Id.c1.C2 "Hello" [:div]] ;make tag lower case?
    [:div..c]
    [:..]
    [:div#id..c]
    [:div##id] 
    []
    ["div"]
    ['div]
    ])

(def recorded
  '[{:src [:div], :out (hyperfiddle.electric-dom3/div)}
    {:src [:div#id1 {:id id2}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:id id2}))}
    {:src
     [:div#id2.my-div
      {:p1 :p1-value, :class [:other-classes]}
      [:div]
      (prn "Hello")
      "Sign out"],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props
       {:p1 :p1-value, :class "my-div other-classes", :id "id2"})
      (hyperfiddle.electric-dom3/div)
      (prn "Hello")
      (hyperfiddle.electric-dom3/text "Sign out"))}
    {:src [:div#id],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:id "id"}))}
    {:src [:#id],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:id "id"}))}
    {:src [:.c1.c2],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2"}))}
    {:src [:#id.c1],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1", :id "id"}))}
    {:src [:.#id.c1],
     :out "Assert failed: Invalid hiccup tag-form: :.#id.c1\nvalid"}
    {:src [:.c1#id],
     :out "Assert failed: Invalid hiccup tag-form: :.c1#id\nvalid"}
    {:src
     [:button.btn
      {:type :Submit}
      "Update"
      (dom/on "click" (e/fn [e] (SetBar. (or @text ""))))],
     :out
     (hyperfiddle.electric-dom3/button
      (hyperfiddle.electric-dom3/props {:type :Submit, :class "btn"})
      (hyperfiddle.electric-dom3/text "Update")
      (dom/on "click" (e/fn [e] (SetBar. (or @text "")))))}
    {:src [:div.c1 {:class "c2 c3"}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2 c3"}))}
    {:src [:div {:class [:c1 :c2]}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2"}))}
    {:src [:div {:class ["c1" "c2"]}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2"}))}
    {:src [:div {:class :c1.c2}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2"}))}
    {:src [:div.c0 {:class [:c1 :c2]}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c0 c1 c2"}))}
    {:src [:div.c0 {:class ["c1" "c2"]}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c0 c1 c2"}))}
    {:src [:div.c0 {:class :c1.c2}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c0 c1 c2"}))}
    {:src [:div.flex],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "flex"}))}
    {:src
     [:div.flex
      [:div "flex-div"]
      [:input#bar.w-full
       {:type :text, :value bar}
       (dom/on "keyup" (e/fn [e] (reset! text (-> e .-target .-value))))
       (dom/on
        "keydown"
        (e/fn [e] (when (= "Enter" (.-key e)) (SetBar. (or @text "")))))]
      [:div.w-3]
      [:button.btn
       {:type :Submit}
       "Update"
       (dom/on "click" (e/fn [_e] (SetBar. (or @text ""))))]],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "flex"})
      (hyperfiddle.electric-dom3/div
       (hyperfiddle.electric-dom3/text "flex-div"))
      (hyperfiddle.electric-dom3/input
       (hyperfiddle.electric-dom3/props
        {:type :text, :value bar, :class "w-full", :id "bar"})
       (dom/on "keyup" (e/fn [e] (reset! text (-> e .-target .-value))))
       (dom/on
        "keydown"
        (e/fn [e] (when (= "Enter" (.-key e)) (SetBar. (or @text ""))))))
      (hyperfiddle.electric-dom3/div
       (hyperfiddle.electric-dom3/props {:class "w-3"}))
      (hyperfiddle.electric-dom3/button
       (hyperfiddle.electric-dom3/props {:type :Submit, :class "btn"})
       (hyperfiddle.electric-dom3/text "Update")
       (dom/on "click" (e/fn [_e] (SetBar. (or @text ""))))))}
    {:src [:div#my-id.my-class1.my-class2],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props
       {:class "my-class1 my-class2", :id "my-id"}))}
    {:src [:div.my-class [:div] "Hello world" (expression)],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "my-class"})
      (hyperfiddle.electric-dom3/div)
      (hyperfiddle.electric-dom3/text "Hello world")
      (expression))}
    {:src
     [:div#my-id.my-class1.my-class2
      {:class [:my-class3 :my-class4],
       :id :my-id2,
       :property1 :some-value,
       :property2 (expression)}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props
       {:class "my-class1 my-class2 my-class3 my-class4",
        :id :my-id2,
        :property1 :some-value,
        :property2 (expression)}))}
    {:src
     [:button.text-blue-500.hover:text-blue-800
      {:type :submit}
      "Sign out"],
     :out
     (hyperfiddle.electric-dom3/button
      (hyperfiddle.electric-dom3/props
       {:type :submit, :class "text-blue-500 hover:text-blue-800"})
      (hyperfiddle.electric-dom3/text "Sign out"))}
    {:src [:div {:class :my-class.my-class2}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "my-class my-class2"}))}
    {:src [:div {:class (str "a" "-b")}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props
       {:class (electric-hiccup.reader/seq-classes>str (str "a" "-b"))}))}
    {:src [:div.x {:class (str "a" "-b")}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props
       {:class
        (clojure.core/str
         "x"
         " "
         (electric-hiccup.reader/seq-classes>str (str "a" "-b")))}))}
    {:src [:div.class1 {:class (get-classes :my-key)}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props
       {:class
        (clojure.core/str
         "class1"
         " "
         (electric-hiccup.reader/seq-classes>str
          (get-classes :my-key)))}))}
    {:src [:div.a {:class '(:b :c)} "list"],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props
       {:class
        (clojure.core/str
         "a"
         " "
         (electric-hiccup.reader/seq-classes>str '(:b :c)))})
      (hyperfiddle.electric-dom3/text "list"))}
    {:src
     [:svg/svg
      {:viewBox "0 0 300 100"}
      [:svg/circle
       {:cx 50, :cy 50, :r (+ 30 offset), :style {:fill "#af7ac5 "}}]
      [:svg/g
       {:transform (str "translate(105,20) rotate(" (* 3 offset) ")")}
       [:svg/polygon
        {:points "30,0 0,60 60,60", :style {:fill "#5499c7"}}]]
      [:svg/rect
       {:x 200,
        :y 20,
        :width (+ 60 offset),
        :height (+ 60 offset),
        :style {:fill "#45b39d"}}]],
     :out
     (svg/svg
      (hyperfiddle.electric-dom3/props {:viewBox "0 0 300 100"})
      (svg/circle
       (hyperfiddle.electric-dom3/props
        {:cx 50, :cy 50, :r (+ 30 offset), :style {:fill "#af7ac5 "}}))
      (svg/g
       (hyperfiddle.electric-dom3/props
        {:transform (str "translate(105,20) rotate(" (* 3 offset) ")")})
       (svg/polygon
        (hyperfiddle.electric-dom3/props
         {:points "30,0 0,60 60,60", :style {:fill "#5499c7"}})))
      (svg/rect
       (hyperfiddle.electric-dom3/props
        {:x 200,
         :y 20,
         :width (+ 60 offset),
         :height (+ 60 offset),
         :style {:fill "#45b39d"}})))}
    {:src [:div (when true [:div])],
     :out (hyperfiddle.electric-dom3/div (when true [:div]))}
    {:src [:div#id.c1.c2 "Hello" [:div [:div "inner"]]],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2", :id "id"})
      (hyperfiddle.electric-dom3/text "Hello")
      (hyperfiddle.electric-dom3/div
       (hyperfiddle.electric-dom3/div
        (hyperfiddle.electric-dom3/text "inner"))))}
    {:src [:div#id.c1.c2 "Hello" [:div [:div (dom/text "text")]]],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2", :id "id"})
      (hyperfiddle.electric-dom3/text "Hello")
      (hyperfiddle.electric-dom3/div
       (hyperfiddle.electric-dom3/div (dom/text "text"))))}
    {:src [:div.c#id],
     :out "Assert failed: Invalid hiccup tag-form: :div.c#id\nvalid"}
    {:src (str), :out "Assert failed: (vector? hiccup)"}
    {:src [:DIV#Id.c1.C2 "Hello" [:div]],
     :out
     (hyperfiddle.electric-dom3/DIV
      (hyperfiddle.electric-dom3/props {:class "c1 C2", :id "Id"})
      (hyperfiddle.electric-dom3/text "Hello")
      (hyperfiddle.electric-dom3/div))}
    {:src [:div..c],
     :out "Assert failed: Invalid hiccup tag-form: :div..c\nvalid"}
    {:src [:..], :out "Assert failed: Invalid hiccup tag-form: :..\nvalid"}
    {:src [:div#id..c],
     :out "Assert failed: Invalid hiccup tag-form: :div#id..c\nvalid"}
    {:src [:div##id],
     :out "Assert failed: Invalid hiccup tag-form: :div##id\nvalid"}
    {:src [], :out "Assert failed: (keyword? tag-form)"}
    {:src ["div"], :out "Assert failed: (keyword? tag-form)"}
    {:src ['div], :out "Assert failed: (keyword? tag-form)"}])

(defn record
  "Print the supplied src hiccup data forms with the corresponding generated electric code.
   Save this as the recorded data for the regression tests."
  [tests]
  (binding [electric-hiccup.reader/*electric-dom-pkg* 'hyperfiddle.electric-dom3]
    (pprint (mapv (fn [v] {:src v :out (expand v)}) tests))))

(defn test 
  "Verify the recorded electric code with the regenerated electric code for the corresponding
   src test cases."
  [recorded]
  (binding [electric-hiccup.reader/*electric-dom-pkg* 'hyperfiddle.electric-dom3]
    (pprint
     (map (fn [{:keys [src out]}]
            (let [r (expand src)]
              (if (= out r)
                "Pass"
                {:result "Fail" :src src :ref out :out r}))) recorded))))

;(pprint (expand '[:div.c1#id]))

;run with babashka:
;> bb test.clj
;
;(record tests) ;re-record to capture new test cases or changes.
(test recorded)
