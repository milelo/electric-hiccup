(ns electric-hiccup.reader
  (:require
   [clojure.string :as str]))

(defn- parse-hiccup-tag [tag-form]
  (assert (keyword? tag-form))
  (let [[_ tag-name id classes] (re-matches #"^([^#.]+)(?:#([^#.]+))?(?:\.([^.]+(?:\.[^.]+)*))?$" (name tag-form))]
    {:tag tag-name
     :id id
     :classes (when classes (str/replace classes #"\." " "))}))

(defn- classes>str [classes]
  (cond
    (string? classes) classes
    (vector? classes) (str/join " " (map name classes))
    (keyword? classes) (str/replace (name classes) #"\." " ")))

(defmacro $< [hiccup]
  (assert (vector? hiccup))
  (let [[tag-form & attrs-and-content] hiccup
        {:keys [tag id classes]} (parse-hiccup-tag tag-form)
        _ (assert tag "Invalid hiccup tag")
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
                  (and p-classes classes) `(str ~classes " " (classes>str ~p-classes))
                  :else `(classes>str ~p-classes))
        props (if classes (assoc props :class classes) props)
        ;props id takes precedence
        props (if (and id (not (:id props))) (assoc props :id id) props)
        content (map #(cond
                        (vector? %) `($< ~%)
                        (string? %) `(hyperfiddle.electric-dom2/text ~%)
                        :else %) content)]
    (list* (-> (str "hyperfiddle.electric-dom2/" tag) symbol)
           (if props
             (cons `(hyperfiddle.electric-dom2/props ~props) content)
             content))))

(defn read-data [hiccup]
  `($< ~hiccup))



