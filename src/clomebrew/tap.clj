(ns clomebrew.tap
  (:require [clomebrew [loader :as cl]
                       [cluby :as cluby]])
  (:import [clomebrew.loader Executor]))

(defrecord Tap [t ex])

(def ^:private var-name "__clomebrew_t")

(defn- bind-tap
  [{:keys [t ex]}]
  (cl/bind ex var-name t))

(defn names
  "Retrieve all installed tap names."
  [e]
  (-> e
      (cl/exec "require 'tap'; Tap.names")
      cluby/->clj))

(defn by-name
  "Retrieve a tap by its name"
  ([tap-name]
   (by-name (cl/mk-executor) tap-name))
  ([^Executor e tap-name]
   (let [t (-> e
               (cl/bind "__clomebrew_t" tap-name)
               (cl/exec "Tap.fetch(__clomebrew_t)"))]
     (map->Tap {:t t
                :ex e}))))

(defn ->map
  "Convert a tap to a map"
  [{:keys [t ex]}]
  (cl/obj->map ex t))
