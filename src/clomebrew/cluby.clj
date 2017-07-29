(ns clomebrew.cluby
  "JRuby->Clojure utilities"
  (:import [org.jruby
            Ruby
            RubyHash RubyArray RubyRange RubyEnumerator
            RubySymbol RubyFixnum]
           [org.jruby.runtime ThreadContext Block]))

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

(defmethod ->clj RubyFixnum
  [^RubyFixnum n]
  (.getBigIntegerValue n))

(defmethod ->clj RubyEnumerator
  [^RubyEnumerator e]
  ;(map ->clj (iterator-seq e)))
  (iterator-seq e))

(defmethod ->clj RubyRange
  [^RubyRange r]
  ;; Call .each on the range to get an enumerator.
  ;; It'd be good to find a way to do so without having to create a new Ruby
  ;; instance.
  (->clj (.each r (. ThreadContext newContext (Ruby/newInstance))
                  Block/NULL_BLOCK)))
