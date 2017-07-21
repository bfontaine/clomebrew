(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [clomebrew [core :as hb]
                       [formula :as hf]
                       [loader :as hbl]]))

(def brew (hb/new-brew))
