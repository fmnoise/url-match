(ns url-match.core-test
  (:require [clojure.test :refer :all]
            [url-match.core :refer :all]))

(def twitter (new-pattern "host(twitter.com); path(?user/status/?id);"))
(def dribbble (new-pattern "host(dribbble.com); path(shots/?id); queryparam(offset=?offset);"))
(def dribbble2 (new-pattern "host(dribbble.com); path(shots/?id); queryparam(offset=?offset); queryparam(list=?type);"))

(def twitter-url "http://twitter.com/bradfitz/status/562360748727611392")
(def dribbble-url "https://dribbble.com/shots/1905065-Travel-Icons-pack?offset=1&list=users")
(def bad-dribbble-url "https://dribbble.com/shots/1905065-Travel-Icons-pack?list=users")
(def bad-twitter-url "https://twitter.com/shots/1905065-Travel-Icons-pack?list=users&offset=1")
(def twitter-params [[:user "bradfitz"] [:id "562360748727611392"]])
(def dribbble-params [[:id "1905065-Travel-Icons-pack"] [:offset "1"]])
(def dribbble2-params [[:id "1905065-Travel-Icons-pack"] [:offset "1"] [:type "users"]])

(deftest valid-twitter-url
  (is (= twitter-params (recognize twitter twitter-url))))

(deftest invalid-twitter-url
  (is (= nil (recognize twitter bad-twitter-url))))

(deftest valid-dribbble-url
  (is (= dribbble-params (recognize dribbble dribbble-url))))

(deftest invalid-dribble-url
  (is (= nil (recognize dribbble bad-twitter-url))))

(deftest pattern-with-2-params
  (is (= dribbble2-params (recognize dribbble2 dribbble-url))))
