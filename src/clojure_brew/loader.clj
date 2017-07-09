(ns clojure-brew.loader
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

        prefix (-> brew-file .getParent .getParent)
        repo (some #(.exists (io/file % ".git"))
                   [prefix (io/file prefix "Homebrew")])

        lib (io/file prefix "Library")
        lib-path (io/file lib "Homebrew")]
    {:env {:brew-file brew-file
           :prefix prefix
           :repository repo
           :library lib
           :cellar (io/file prefix "Cellar")
           :cache "/tmp" ; TODO use the correct value
           }
     :load-paths [lib-path]}))

(defn- mk-env-var
  [k]
  (-> k str (cs/replace #"-" "_") (cs/upper-case)))

(defn mk-env-init-code
  [{:keys [env]}]
  (->> env
       (map (fn [[k v]]
              (format "\"%s\" => \"%s\"" (mk-env-var k) v)))
       (cs/join ",")
       (format "ENV.update({%s})\n")))

(defn mk-executer
  []
  (let [{:keys [env load-paths]} (mk-env)]
    {:sc (doto (ScriptingContainer.)
           (.setLoadPaths load-paths))
     :init (mk-env-init-code env)}))

(defn exec
  [ex ^String code]
  (let [{:keys [sc init]} ex
        code (str init code)]
    (. ex runScriptlet code)))
