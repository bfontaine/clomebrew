(ns clomebrew.cluby-test
  (:require [clojure.test :refer :all]
            [clomebrew.cluby :as cluby])
  (:import [org.jruby.embed ScriptingContainer]))

(deftest basic-values->clj
  (testing "integers"
    (are [x] (= (cluby/->clj x) x)
         0 1 2 3 1000 -50))
  (testing "floats"
    (are [x] (= (cluby/->clj x) x)
         0.0 -0.0 1.5 3.1415))
  (testing "strings"
    (are [x] (= (cluby/->clj x) x)
         "" "foo" "x\"'\\"))
  (testing "booleans"
    (are [x] (= (cluby/->clj x) x)
         true false))
  (testing "nil"
    (are [x] (= (cluby/->clj x) x)
         nil)))

(deftest basic-ruby-values->clj
  (let [sc (ScriptingContainer.)]
    (are [expected code] (= expected (cluby/->clj (. sc runScriptlet code)))
         0       "0"
         1       "1"
         :foo    ":foo"
         "hello" "'hello'"
         nil     "nil"
         true    "true")))

(deftest ruby-collections->clj
  (let [sc (ScriptingContainer.)]
    (are [expected code] (= expected (cluby/->clj (. sc runScriptlet code)))
         [1 2 3] "[1, 2, 3]"
         {:a 42} "{:a => 42}"
         {:a {:b 1 :c [2 3]}} "{:a => {:b => 1, :c => [2, 3]}}")))

(deftest ruby-range->clj
  (let [sc (ScriptingContainer.)]
    (is (= 55 (reduce + (cluby/->clj (. sc runScriptlet "(1..10)")))))))

(deftest ruby-iterator->clj
  (let [sc (ScriptingContainer.)]
    (is (= 55 (reduce + (cluby/->clj (. sc runScriptlet "(1..10).lazy")))))))
