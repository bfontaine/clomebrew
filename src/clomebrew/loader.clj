(ns clomebrew.loader
  (:require [clojure.string :as cs]
            [clojure.java.io :as io])
  (:import [org.jruby.embed ScriptingContainer]))

(defn- first-existing
  [xs]
  (some #(when (.exists %) %) xs))

(defn mk-env
  []
  (let [path (cs/split (System/getenv "PATH") #":")

        brew-file (->> path
                       (map #(io/file % "brew"))
                       first-existing)

        prefix (.getCanonicalPath (io/file brew-file "../.."))
        repo (some #(when (.exists (io/file % ".git")) %)
                   [prefix (io/file prefix "Homebrew")])

        lib (io/file prefix "Library")
        lib-path (io/file lib "Homebrew")]

    {:env {:brew-file brew-file
           :prefix prefix
           :repository repo
           :library lib
           :cellar (io/file prefix "Cellar")
           :cache (first-existing
                      [(io/file (System/getProperty "user.home")
                                "Library/Caches/Homebrew")
                       ;; safe default
                       (io/file "/tmp")])

           :no-compat "1"}
     :load-paths [(str lib-path)]}))

(defn- mk-env-var
  [k]
  (-> k name (cs/replace #"-" "_") (cs/upper-case)))

(def ^:private env-template-code
  (-> "clomebrew_init.rb" io/resource slurp))

(defn mk-env-init-code
  [env]
  (->> env
       (map (fn [[k v]]
              (format "'HOMEBREW_%s' => '%s'" (mk-env-var k) v)))
       (cs/join ",")
       (format "{%s}")
       (cs/replace env-template-code #"DEFAULT_CLOMEBREW_ENV")))

(defn mk-executor
  []
  (let [{:keys [env load-paths]} (mk-env)]
    {:sc (doto (ScriptingContainer.)
           (.setLoadPaths load-paths))
     :init (mk-env-init-code env)}))

(defn exec
  [ex ^String code]
  (let [{:keys [sc init]} ex
        code (str init code)]
    (. sc runScriptlet code)))
