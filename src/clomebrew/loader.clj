(ns clomebrew.loader
  (:require [clojure.string :as cs]
            [clojure.java.io :as io])
  (:import [org.jruby.embed ScriptingContainer]
           [java.nio.file Paths]))

(defn- first-existing
  [xs]
  (some #(when (.exists %) %) xs))

(defn- normalized-path
  "Normalize an io/file's path without resolving symlinks (as does
   getCanonicalPath)."
  [f]
  (-> f
      .getPath
      (Paths/get (into-array [""]))
      .normalize
      str))


(defn mk-env
  []
  (let [path (cs/split (System/getenv "PATH") #":")

        brew-file (->> path
                       (map #(io/file % "brew"))
                       first-existing)

        prefix (normalized-path (io/file brew-file "../.."))

        repo (some #(when (.exists (io/file % ".git")) %)
                   [prefix (io/file prefix "Homebrew")])

        lib (io/file repo "Library")
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

(defrecord Executor [sc init env])

(defn- update-all-values
  [m f]
  (reduce (fn [m [k v]]
            (assoc m k (f v)))
          {}
          m))

(defn mk-executor
  []
  (let [{:keys [env load-paths]} (mk-env)]
    (map->Executor
      {:sc (doto (ScriptingContainer.)
             (.setLoadPaths load-paths))
       :init (mk-env-init-code env)
       :env (update-all-values env str)})))

(defn exec
  [^Executor ex ^String code]
  (let [{:keys [sc init]} ex
        code (str init code)]
    (. sc runScriptlet code)))

(defn bind
  ^Executor
  [^Executor ex ^String k ^String v]
  (.put (:sc ex) k v)
  ex)
