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
    (->> query
         (map #(s/replace % param "(\\\\w+)"))
         (map #(str "(?=.*" % ")"))
         s/join
         (#(str "\\?.*" % ".+")))))

(defn- make-pattern-regex
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

(defn- try-int
  [val]
  (if-let [int-value (re-find #"^[1-9]\d*$" val)]
    (read-string int-value)
    val))

(defn new-pattern
  [template]
  {:host  (last (re-find host template))
   :path  (last (re-find path template))
   :query (map last (re-seq query template))})

; TODO: use named regexp groups instead of zipmap
(defn recognize
  [pattern url]
  (when-let [values (re-seq (make-pattern-regex pattern) url)]
    (->> (zipmap (extract-keys pattern) (rest (first values)))
         (into [])
         (map (fn [[k v]] [k (try-int v)]))
         sort)))
