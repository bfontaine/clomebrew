(ns clomebrew.formula-test
  (:require [clojure.test :refer :all]
            [clomebrew.core :as hb]
            [clomebrew.formula :as hf]))

#_(deftest by-name
  (is (some? (hf/by-name "git"))))
