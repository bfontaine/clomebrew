(ns clomebrew.formula
  (:require [clomebrew [loader :as cl]
                       [cluby :as cluby]])
  (:import [clomebrew.loader Executor]))

(defrecord Formula [f ex])

(def ^:private var-name "__clomebrew_f")

(defn- bind-formula
  [{:keys [f ex]}]
  (cl/bind ex var-name f))

(defn by-name
  "Retrieve a formula by its name"
  ([formula-name]
   (by-name (cl/mk-executor) formula-name))
  ([^Executor e formula-name]
   (let [f (-> e
               (cl/bind "__clomebrew_f" formula-name)
               (cl/exec "Formula[__clomebrew_f]"))]
     (map->Formula {:f f
                    :ex e}))))

(defn path
  "Retrieve the file path of a formula."
  [f]
  (-> (bind-formula f)
      (cl/exec (format "%s.path.to_s" var-name))
      cluby/->clj))
