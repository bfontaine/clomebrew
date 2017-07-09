(ns clomebrew.core
  (:require [clomebrew.loader :as cl]))

(defn -main
  [& _]
  (let [e (cl/mk-executor)]
    (println "Git version:"
             (cl/exec e "require 'formula'; Formula['git'].version"))))
