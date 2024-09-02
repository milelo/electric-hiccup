(ns electric-hiccup.reader
  (:require
   [clojure.string :as str]))

(def ^:dynamic *electric-dom-pkg* nil)

(defn- dom-symbol [tag-ns symbol-name]
  (let [symbol-ns (or
                   tag-ns
                   *electric-dom-pkg*
                   'hyperfiddle.electric-dom2)]
    (symbol (str (name symbol-ns) "/" (name symbol-name)))))

(defn- parse-hiccup-tag [tag-form]
  (assert (keyword? tag-form))
  (let [[valid tag-name id classes] (re-matches #"^([^#.]+)?(?:#([^#.]+))?(?:\.((?:[^#.]+\.)*[^#.]+))?$" (name tag-form))]
    (assert valid (str "Invalid hiccup tag-form: " tag-form))
    {:tag-ns (namespace tag-form)
     :tag (or tag-name "div")
     :id id
     :classes (when classes (str/replace classes #"\." " "))}))

(defn- classes>str [classes]
  (cond
    (string? classes) classes
    (vector? classes) (str/join " " (map name classes))
    (keyword? classes) (str/replace (name classes) #"\." " ")))

(defn- seq-classes>str [classes]
  (if (sequential? classes)
    (-> classes vec classes>str)
    (classes>str classes)))

(defmacro $<
  "Transform hiccup into hyperfiddle.electric form"
  [hiccup]
  (assert (vector? hiccup))
  (let [[tag-form & attrs-and-content] hiccup
        {:keys [tag-ns tag id classes]} (parse-hiccup-tag tag-form)
        props (let [p (first attrs-and-content)] (when (map? p) p))
        content (if props (rest attrs-and-content) attrs-and-content)
        p-classes (:class props)
        ;if p-classes is a literal, ensure its a string
        p-classes (or (classes>str p-classes) p-classes)
        ;join classes and p-classes at compile or runtime as required
        ;p-classes take precedence
        classes (cond
                  (and (not p-classes) (not classes)) nil
                  (and (not p-classes) classes) classes
                  (and (string? p-classes) (not classes)) p-classes
                  (and (string? p-classes) classes) (str classes " " p-classes)
                  (and p-classes classes) `(str ~classes " " (seq-classes>str ~p-classes))
                  :else `(seq-classes>str ~p-classes))
        props (if classes (assoc props :class classes) props)
        ;props id takes precedence
        props (if (and id (not (:id props))) (assoc props :id id) props)
        content (map #(cond
                        (vector? %) `($< ~%)
                        (string? %) `(~(dom-symbol nil 'text) ~%)
                        :else %) content)]
    (list* (dom-symbol tag-ns tag)
           (if props
             (cons `(~(dom-symbol nil 'props) ~props) content)
             content))))

(defn read-data [hiccup]
  `($< ~hiccup))
