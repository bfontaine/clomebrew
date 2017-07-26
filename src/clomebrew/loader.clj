(ns clomebrew.loader
  "Low-level API"
  (:require [clojure.walk :refer [keywordize-keys]]
            [clojure.string :as cs]
            [clojure.java.io :as io]
            [clomebrew.cluby :as cluby])
  (:import [org.jruby.embed ScriptingContainer]
           [org.jruby RubyHash]
           [java.io File]
           [java.nio.file Paths]))

(defn- first-existing
  [xs]
  (some #(when (.exists ^File %) %) xs))

(defn- normalized-path
  "Normalize an io/file's path without resolving symlinks (as does
   getCanonicalPath)."
  [^File f]
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
  (-> "clomebrew/bootstrap.rb" io/resource slurp))

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
    (. ^ScriptingContainer sc runScriptlet code)))

(defn bind
  ^Executor
  [{:keys [sc] :as ex} ^String k ^String v]
  (.put ^ScriptingContainer sc k v)
  ex)

(defn obj->map
  "Call to_hash on an object and return it as a Clojure map with keywordized
   keys."
  [{:keys [sc]} obj]
  (-> (.callMethod ^ScriptingContainer sc
                   obj "to_hash" RubyHash)
      cluby/->clj
      keywordize-keys))
