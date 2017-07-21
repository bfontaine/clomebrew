(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [clomebrew [core :as hb]
                       [formula :as hf]
                       [tap :as ht]
                       [loader :as hbl]]))

(def brew (hb/new-brew))
