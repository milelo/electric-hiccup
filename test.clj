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
  '[{:in [:div], :out (hyperfiddle.electric-dom3/div)}
    {:in [:div#id1 {:id id2}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:id id2}))}
    {:in
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
    {:in [:div#id],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:id "id"}))}
    {:in [:#id],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:id "id"}))}
    {:in [:.c1.c2],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2"}))}
    {:in [:#id.c1],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1", :id "id"}))}
    {:in [:.#id.c1],
     :out "Assert failed: Invalid hiccup tag-form: :.#id.c1\nvalid"}
    {:in [:.c1#id],
     :out "Assert failed: Invalid hiccup tag-form: :.c1#id\nvalid"}
    {:in
     [:button.btn
      {:type :Submit}
      "Update"
      (dom/on "click" (e/fn [e] (SetBar. (or @text ""))))],
     :out
     (hyperfiddle.electric-dom3/button
      (hyperfiddle.electric-dom3/props {:type :Submit, :class "btn"})
      (hyperfiddle.electric-dom3/text "Update")
      (dom/on "click" (e/fn [e] (SetBar. (or @text "")))))}
    {:in [:div.c1 {:class "c2 c3"}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2 c3"}))}
    {:in [:div {:class [:c1 :c2]}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2"}))}
    {:in [:div {:class ["c1" "c2"]}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2"}))}
    {:in [:div {:class :c1.c2}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2"}))}
    {:in [:div.c0 {:class [:c1 :c2]}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c0 c1 c2"}))}
    {:in [:div.c0 {:class ["c1" "c2"]}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c0 c1 c2"}))}
    {:in [:div.c0 {:class :c1.c2}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c0 c1 c2"}))}
    {:in [:div.flex],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "flex"}))}
    {:in
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
    {:in [:div#my-id.my-class1.my-class2],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props
       {:class "my-class1 my-class2", :id "my-id"}))}
    {:in [:div.my-class [:div] "Hello world" (expression)],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "my-class"})
      (hyperfiddle.electric-dom3/div)
      (hyperfiddle.electric-dom3/text "Hello world")
      (expression))}
    {:in
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
    {:in
     [:button.text-blue-500.hover:text-blue-800
      {:type :submit}
      "Sign out"],
     :out
     (hyperfiddle.electric-dom3/button
      (hyperfiddle.electric-dom3/props
       {:type :submit, :class "text-blue-500 hover:text-blue-800"})
      (hyperfiddle.electric-dom3/text "Sign out"))}
    {:in [:div {:class :my-class.my-class2}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "my-class my-class2"}))}
    {:in [:div {:class (str "a" "-b")}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props
       {:class (electric-hiccup.reader/seq-classes>str (str "a" "-b"))}))}
    {:in [:div.x {:class (str "a" "-b")}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props
       {:class
        (clojure.core/str
         "x"
         " "
         (electric-hiccup.reader/seq-classes>str (str "a" "-b")))}))}
    {:in [:div.class1 {:class (get-classes :my-key)}],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props
       {:class
        (clojure.core/str
         "class1"
         " "
         (electric-hiccup.reader/seq-classes>str
          (get-classes :my-key)))}))}
    {:in [:div.a {:class '(:b :c)} "list"],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props
       {:class
        (clojure.core/str
         "a"
         " "
         (electric-hiccup.reader/seq-classes>str '(:b :c)))})
      (hyperfiddle.electric-dom3/text "list"))}
    {:in
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
    {:in [:div (when true [:div])],
     :out (hyperfiddle.electric-dom3/div (when true [:div]))}
    {:in [:div#id.c1.c2 "Hello" [:div [:div "inner"]]],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2", :id "id"})
      (hyperfiddle.electric-dom3/text "Hello")
      (hyperfiddle.electric-dom3/div
       (hyperfiddle.electric-dom3/div
        (hyperfiddle.electric-dom3/text "inner"))))}
    {:in [:div#id.c1.c2 "Hello" [:div [:div (dom/text "text")]]],
     :out
     (hyperfiddle.electric-dom3/div
      (hyperfiddle.electric-dom3/props {:class "c1 c2", :id "id"})
      (hyperfiddle.electric-dom3/text "Hello")
      (hyperfiddle.electric-dom3/div
       (hyperfiddle.electric-dom3/div (dom/text "text"))))}
    {:in [:div.c#id],
     :out "Assert failed: Invalid hiccup tag-form: :div.c#id\nvalid"}
    {:in (str), :out "Assert failed: (vector? hiccup)"}
    {:in [:DIV#Id.c1.C2 "Hello" [:div]],
     :out
     (hyperfiddle.electric-dom3/DIV
      (hyperfiddle.electric-dom3/props {:class "c1 C2", :id "Id"})
      (hyperfiddle.electric-dom3/text "Hello")
      (hyperfiddle.electric-dom3/div))}
    {:in [:div..c],
     :out "Assert failed: Invalid hiccup tag-form: :div..c\nvalid"}
    {:in [:..], :out "Assert failed: Invalid hiccup tag-form: :..\nvalid"}
    {:in [:div#id..c],
     :out "Assert failed: Invalid hiccup tag-form: :div#id..c\nvalid"}
    {:in [:div##id],
     :out "Assert failed: Invalid hiccup tag-form: :div##id\nvalid"}
    {:in [], :out "Assert failed: (keyword? tag-form)"}
    {:in ["div"], :out "Assert failed: (keyword? tag-form)"}
    {:in ['div], :out "Assert failed: (keyword? tag-form)"}])

(defn record []
  (binding [electric-hiccup.reader/*electric-dom-pkg* 'hyperfiddle.electric-dom3]
    (pprint (mapv (fn [v] {:in v :out (expand v)}) tests))))

(defn test []
  (binding [electric-hiccup.reader/*electric-dom-pkg* 'hyperfiddle.electric-dom3]
    (pprint
     (map (fn [{:keys [in out]}]
            (let [r (expand in)]
              (if (= out r)
                "Pass"
                {:result "Fail" :in in :ref out :out r}))) recorded))))

;(pprint (expand '[:div.c1#id]))
;(record)
(test)
