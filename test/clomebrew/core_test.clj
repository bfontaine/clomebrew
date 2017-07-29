(ns clomebrew.core-test
  (:require [clojure.test :refer :all]
            [clomebrew.core :as hb]))

(deftest new-brew
  (is (some? (hb/new-brew))))

(deftest paths
  (let [b (hb/new-brew)]
    (is (string? (hb/cache b)))
    (is (string? (hb/cellar b)))
    (is (string? (hb/prefix b)))
    (is (string? (hb/repository b)))))
