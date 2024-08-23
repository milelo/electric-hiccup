(ns electric-hiccup.reader
  (:require
   [clojure.string :as str]))

(defn- parse-hiccup-tag [tag-form]
  (assert (keyword? tag-form))
  (let [[_ tag-name id classes] (re-matches #"^([^#.]+)(?:#([^#.]+))?(?:\.([^.]+(?:\.[^.]+)*))?$" (name tag-form))]
    {:tag tag-name
     :id id
     :classes (when classes (str/replace classes #"\." " "))}))

(defmacro $< [hiccup]
  (assert (vector? hiccup))
  (let [[tag-form & attrs-and-content] hiccup
        {:keys [tag id classes]} (parse-hiccup-tag tag-form)
        _ (assert tag "Invalid hiccup tag")
        props (let [p (first attrs-and-content)] (when (map? p) p))
        content (if props (rest attrs-and-content) attrs-and-content)
        p-classes (:class props)
        p-classes (cond
                    (vector? p-classes) (str/join " " (map name p-classes))
                    (keyword? p-classes) (str/replace (name p-classes) #"\." " ")
                    :else p-classes)
        classes (when (or classes p-classes)
                  (str/join " " (filter identity [classes p-classes])))
        props (if classes (assoc props :class classes) props)
        props (if id (assoc props :id id) props)
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



