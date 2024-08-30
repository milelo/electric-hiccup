;bb tests.clj
(ns test
  (:require
   [electric-hiccup.reader :refer [$<]]
   [clojure.walk :as walk]
   [clojure.pprint :refer [pprint]]))

(defn expand
  "Expand the 'quoted hiccup - test and debug support."
  [hiccup]
  (walk/prewalk (fn [x]
                  (if (and (seq? x) (-> x first (= 'electric-hiccup.reader/$<)))
                    (macroexpand-1 x)
                    x)) `(electric-hiccup.reader/$< ~hiccup)))

(def tests '[[:div]
            [:div#id1 {:id id2}]
            [:div#id2.my-div {:p1 :p1-value :class [:other-classes]}
              [:div]
              (prn "Hello")
              "Sign out"]
            [:div#id]
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
            [:input#bar2.w-full {:type :text :value bar}
              (dom/on "keyup" (e/fn [e]
                                (reset! text (-> e .-target .-value))))
              (dom/on "keydown" (e/fn [e]
                                  (when (= "Enter" (.-key e))
                                    (SetBar. (or @text "")))))]
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
            [:div#my-id.my-class1.my-class2 {:class [my-class3 my-class4]
                                              :id :my-id2 ;overridden
                                              :property1 :some-value
                                              :property2 (expression)}]
            [:div.my-class [:div] "Hello world" (expression)]
            [:div#my-id.my-class1.my-class2 {:class [:my-class3 :my-class4]
                                              :id :my-id2 ;overridden by my-id
                                              :property1 :some-value
                                              :property2 (expression)}]
            [:div {:class :my-class.my-class2}]
            [:div {:class (str "a" "-b")}]
            [:div.x {:class (str "a" "-b")}]
            [:div.class1 {:class (get-classes :my-key)}]
            [:div.a {:class '(:b :c)} "list"]
            [:div (when true [:div])]
            [:div#id.c1.c2 "Hello" [:div [:div "inner"]]]
            [:div#id.c1.c2 "Hello" [:div [:div (dom/text "text")]]]
            [:DIV#Id.c1.C2 "Hello" [:div]]])

(def recorded '({:in [:div], :out (hyperfiddle.electric-dom3/div)}
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
                 [:input#bar2.w-full
                  {:type :text, :value bar}
                  (dom/on "keyup" (e/fn [e] (reset! text (-> e .-target .-value))))
                  (dom/on
                   "keydown"
                   (e/fn [e] (when (= "Enter" (.-key e)) (SetBar. (or @text "")))))],
                 :out
                 (hyperfiddle.electric-dom3/input
                  (hyperfiddle.electric-dom3/props
                   {:type :text, :value bar, :class "w-full", :id "bar2"})
                  (dom/on "keyup" (e/fn [e] (reset! text (-> e .-target .-value))))
                  (dom/on
                   "keydown"
                   (e/fn [e] (when (= "Enter" (.-key e)) (SetBar. (or @text ""))))))}
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
                {:in
                 [:div#my-id.my-class1.my-class2
                  {:class [my-class3 my-class4],
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
                {:in [:DIV#Id.c1.C2 "Hello" [:div]],
                 :out
                 (hyperfiddle.electric-dom3/DIV
                  (hyperfiddle.electric-dom3/props {:class "c1 C2", :id "Id"})
                  (hyperfiddle.electric-dom3/text "Hello")
                  (hyperfiddle.electric-dom3/div))}))

(defn record []
  (binding [electric-hiccup.reader/*electric-dom-pkg* 'hyperfiddle.electric-dom3]
    (pprint (map (fn [v] {:in v :out (expand v)}) tests))))

(defn test []
  (binding [electric-hiccup.reader/*electric-dom-pkg* 'hyperfiddle.electric-dom3]
    (pprint
     (map (fn [{:keys [in out]}]
            (let [r (expand in)]
              (if (= out r)
                "Pass"
                {:result "Fail" :in in :ref out :out r}))) recorded))))

;(record)
(test)
