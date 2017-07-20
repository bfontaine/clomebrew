(ns clomebrew.core
  (:require [clojure.walk :refer [keywordize-keys]]
            [clojure.string :as cs]
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
      (cl/bind "__clomebrew_f" formula-name)
      (cl/exec "Formula[__clomebrew_f].to_hash")
      cluby/->clj
      keywordize-keys))

(defn formula-path
  [^Executor e formula-name]
  (-> e
      ;; TODO factorize this code somewhere
      (cl/bind "__clomebrew_f" formula-name)
      (cl/exec "Formula[__clomebrew_f].path.to_s")
      cluby/->clj))

(defn- getenv
  [^Executor e k]
  (-> e :env k))

(defn cache
  "Return Homebrew's cache directory. This is equivalent to capturing the
   output of `brew --cache`."
  [e]
  (getenv e :cache))

(defn cellar
  "Return Homebrew's Cellar directory. This is equivalent to capturing the
   output of `brew --cellar`."
  [e]
  (getenv e :cellar))

(defn prefix
  "Return Homebrew's prefix directory. This is equivalent to capturing the
   output of `brew --prefix`."
  [e]
  (getenv e :prefix))

(defn repository
  "Return Homebrew's repository directory. This is equivalent to capturing the
   output of `brew --repository`."
  [e]
  (getenv e :repository))

;; Commands

(defn- official-cmd
  "Run an arbitrary official Homebrew command. It doesn't check the validity of
   that command and thus can execute arbitrary Ruby code."
  [^Executor e cmd]
  (let [module-name (format "'cmd/%s'" cmd)
        method-name (cs/replace cmd #"-" "_")]
    (-> e
        (cl/exec (format "require %s; Homebrew.%s" module-name method-name))
        cluby/->clj)))

(defn doctor
  "Run the 'doctor' Homebrew command"
  [^Executor e]
  (official-cmd e "doctor"))

(defn env
  "Run the '--env' Homebrew command"
  [^Executor e]
  (official-cmd e "--env"))
