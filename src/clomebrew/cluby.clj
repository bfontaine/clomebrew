(ns clomebrew.cluby
  "JRuby->Clojure utilities"
  (:import [org.jruby RubyHash RubyArray RubySymbol]))

(defmulti ->clj class)

(defmethod ->clj RubyHash
  [^RubyHash h]
  (reduce (fn reduce-ruby-hash [m kv]
            (let [k (.getKey kv)
                  v (.getValue kv)]
              (assoc m (->clj k) (->clj v))))
          {}
          h))

(defmethod ->clj RubyArray
  [^RubyArray a]
  (map ->clj a))

(defmethod ->clj RubySymbol
  [^RubySymbol s]
  (keyword (.toString s)))

(defmethod ->clj Long [x] x)
(defmethod ->clj Double [x] x)
(defmethod ->clj String [x] x)
(defmethod ->clj Boolean [x] x)
(defmethod ->clj nil [x] x)
