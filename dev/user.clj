(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [clomebrew.core :as hb]
            [clomebrew.loader :as hbl]))

(def brew (hb/new-brew))
