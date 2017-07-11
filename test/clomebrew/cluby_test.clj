(ns clomebrew.cluby-test
  (:require [clojure.test :refer :all]
            [clomebrew.cluby :as cluby]))

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
