# clomebrew

**Clomebrew** brings [Homebrew](https://brew.sh/) to Clojure.

**Warning:** This is highly experimental, don’t depend on it for serious stuff. Also, the API is not yet stabilised.

## Usage

You need to already have a working Homebrew installation.

Add the following dependency to your `project.clj`:

```clojure
[clomebrew  "0.0.2"]
```

Then import `clomebrew.core` and create your `brew` instance. It’ll
automatically find your Homebrew installation from your `PATH`.

```clojure
(ns your-ns
  (:require [clomebrew [core :as hb] ;; core API
                       [formula :as hf] ;; formulae
                       [tap :as ht] ;; taps
                    ]))

(def brew (hb/new-brew))
```

### How To

#### Get a Formula

```clojure
(def git (hf/by-name brew "git"))

;; you can also omit brew; it'll create an instance by itself
(def git (hf/by-name "git"))


;; convert the formula to a map:
(hf/->map git)
;; returned map:
{:name "git"
 :full_name "git"
 :desc "Distributed revision control system"
 :homepage "https://git-scm.com"

 :versions {:stable "2.13.2"
            :head "HEAD"
            :devel nil
            :bottle false}
 :revision 0

 :dependencies ("xz" "pcre" "gettext" "openssl" "curl")
 :build_dependencies ("xz")
 :optional_dependencies ("pcre" "gettext" "openssl" "curl")
 :recommended_dependencies ()

 :requirements ({:name "perl"
                 :default_formula "perl"})

 :conflicts_with ()

 :bottle {:stable {:cellar "/usr/local/Cellar"
                   :files {:el_capitan {:sha256 "11d..."
                                        :url "https://.../git-2.13.2.el_capitan.bottle.tar.gz"}
                           :sierra {:sha256 "ce6..."
                                    :url "https://.../git-2.13.2.sierra.bottle.tar.gz"}
                           :yosemite {:sha256 "2c4..."
                                      :url "https://.../git-2.13.2.yosemite.bottle.tar.gz"}}
                   :prefix "/usr/local"
                   :rebuild 0
                   :root_url "https://homebrew.bintray.com/bottles"}}

 :options ({:option "--with-blk-sha1"
            :description "Compile with the block-optimized SHA1 implementation"}
           ;; ...
           {:option "--with-perl"
            :description "Build against a custom Perl rather than system default"})

 :keg_only false
 :outdated false
 :pinned false
 :installed ()

 :caveats nil}
```

This is equivalent to calling `Formula["git"].to_hash` in Ruby.

#### Get a formula’s content

```clojure
(slurp (hf/path git))
```

#### Get Homebrew’s prefix/repo/cellar/cache paths

```clojure
(hb/prefix brew) ;; e.g. "/usr/local"
(hb/repository brew) ;; e.g. "/usr/local/Homebrew"
(hb/cellar brew) ;; e.g. "/usr/local/Cellar"
(hb/cache brew) ;; e.g. "/Users/you/Library/Caches/Homebrew"
```

#### Get installed taps

```clojure
(hb/tap-names brew) ;; => e.g. ("homebrew/core" "homebrew/php" ...)
```

#### Get a tap

```clojure
(def core-tap (ht/by-name "homebrew/core"))

;; Convert the tap to a map
(ht/->map core-tap)
;; returned map:
{:command_files ()
 :custom_remote -1
 ;; be aware it includes *ALL* the formula files.
 ;; That's a 4200+ -long list for homebrew/core
 :formula_files ("/usr/local/Homebrew/.../homebrew-core/Formula/a2ps.rb"
                 "/usr/local/Homebrew/.../homebrew-core/Formula/a52dec.rb"
                 "/usr/local/Homebrew/.../homebrew-core/Formula/aacgain.rb"
                 "/usr/local/Homebrew/.../homebrew-core/Formula/aalib.rb"
                 ...)
 :formula_names ("a2ps"
                 "a52dec"
                 "aacgain"
                 ...)
 :installed true
 :name "homebrew/core"
 :official true
 :path "/usr/local/Homebrew/.../homebrew-core"
 :pinned false
 :private true
 :remote "git@github.com:Homebrew/homebrew-core.git"
 :repo "core"
 :user "Homebrew"}
```

#### Run `brew doctor`

```clojure
(hb/doctor brew)
```

## License

Copyright © 2017 Baptiste Fontaine

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
