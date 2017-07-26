(defproject clomebrew "0.0.2"
  :description "Homebrew bindings in Clojure"
  :url "https://github.com/bfontaine/clomebrew"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.jruby/jruby-complete "9.1.12.0"]]

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace  "0.2.11"]]
                   :global-vars {*warn-on-reflection* true}}})
