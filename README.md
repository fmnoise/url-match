# url-match [![Build Status](https://travis-ci.org/fmnoise/url-match.svg?branch=master)](https://travis-ci.org/fmnoise/url-match)

A Clojure library for matching/extracting url parts using given pattern

## Usage

**!!! This is not published to clojars**

```clojure
(require '[url-match.core :refer [new-pattern recognize]])

(def dribbble (new-pattern "host(dribbble.com); path(shots/?id); queryparam(list=?type); queryparam(offset=?offset);"))

(recognize dribbble "https://dribbble.com/shots/1905065-Travel-Icons-pack?offset=1&list=users")
;; [[:id "1905065-Travel-Icons-pack"] [:offset 1] [:type "users"]]

(recognize dribbble "https://dribbble.com/shots/1905065-Travel-Icons-pack?offset=1")
;; nil ;; type queryparam missing

(recognize dribbble "https://instagram.com/shots/1905065-Travel-Icons-pack?offset=1&list=users")
;; nil ;; wrong host

(def twitter (new-pattern "host(twitter.com); path(?user/status/?id);"))

(recognize "http://twitter.com/bradfitz/status/562360748727611392")
;; [[:id 562360748727611392] [:user "bradfitz"]]
```

## License

Copyright © 2017 fmnoise

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
