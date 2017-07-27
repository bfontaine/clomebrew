(ns clomebrew.core
  (:require [clojure.string :as cs]
            [clomebrew [loader :as cl]
                       [formula :as f]
                       [tap :as t]
                       [cluby :as cluby]])
  (:import [clomebrew.loader Executor]))

(defn- getenv
  [^Executor e k]
  (-> e :env k))

(defn new-brew
  "Create a new (home)brew instance"
  ^Executor
  []
  (cl/mk-executor))

;; Environment API

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


;; -- deprecated API ---------------------------------------------------------

(defn formula
  "Retrieve a formula. Use clomebrew.formula/by-name instead."
  {:added "0.0.1" :deprecated "0.0.2"}
  [e formula-name]
  (f/by-name e formula-name))

(defn formula-path
  "Retrieve a formula's filepath. Use clomebrew.formula/path instead."
  {:added "0.0.1" :deprecated "0.0.2"}
  [e formula-name]
  (f/path (formula e formula-name)))

(defn tap-names
  "Retrieve all installed tap names. Use clomebrew.tap/names instead."
  {:added "0.0.2" :deprecated "0.0.3"}
  [e]
  (t/names e))
