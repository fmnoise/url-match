(ns url-match.core
  [:require [clojure.string :as s :only [join replace split]]])

(def ^:private host #"(host)\((.+?)\);")
(def ^:private path #"(path)\((.+?)\);")
(def ^:private query #"(queryparam)\((.+?)\);")
(def ^:private param #"\?(\w+)")

(defn- make-host-re
  [host]
  (str ".+://" (s/replace host "." "\\.") "/"))

(defn- make-path-re
  [path]
  (s/replace path param "(.+)"))

(defn- make-query-re
  [query]
  (when-not (empty? query)
    ; (str "\\?.*" (s/join "&?" (map #(str "(?:" % ")") (map #(s/replace % param "(.+)&?") query))))
    (str
      "\\?.*"
      (s/join "&" (map #(str "(?:" % ")") (map #(s/replace % param "(\\\\w+)") query))))))

; "[&.]*"

(defn- make-regex
  [pattern]
  (re-pattern
    (str
      (make-host-re (:host pattern))
      (make-path-re (:path pattern))
      (make-query-re (:query pattern)))))

(defn- extract-keys
  [pattern]
  (concat
    (map (comp keyword last) (re-seq param (:path pattern)))
    (map (comp keyword last #(s/split % #"=\?")) (:query pattern))))

(defn new-pattern
  [template]
  {:host  (last (re-find host template))
   :path  (last (re-find path template))
   :query (map last (re-seq query template))})

(defn recognize
  [pattern url]
  (when-let [vals (re-seq (make-regex pattern) url)]
    (into [] (zipmap (extract-keys pattern) (rest (first vals))))))
