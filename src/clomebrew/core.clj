(ns clomebrew.core
  (:require [clojure.walk :refer [keywordize-keys]]
            [clomebrew [loader :as cl]
                       [cluby :as cluby]])
  (:import [clomebrew.loader Executor]))

(defn new-brew
  ^Executor
  []
  (cl/mk-executor))

(defn formula
  "Retrieve a formula as a map"
  [^Executor e formula-name]
  (-> e
      (cl/bind "f" formula-name)
      (cl/exec "Formula[f].to_hash")
      cluby/->clj
      keywordize-keys))

;; Commands

(defn doctor
  "Run the 'doctor' Homebrew command"
  [^Executor e]
  (-> e
      (cl/exec "require 'cmd/doctor'
                Homebrew.doctor")
      cluby/->clj))
