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
  [^Executor e formula-name]
  (-> e
      (cl/bind "f" formula-name)
      (cl/exec "Formula[f].to_hash")
      cluby/->clj
      keywordize-keys))

(defn -main
  [& _]
  (let [e (cl/mk-executor)]
    (println "Git version:"
             (cl/exec e "Formula['git'].version"))))
