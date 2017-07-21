# clomebrew

**Clomebrew** brings [Homebrew](https://brew.sh/) to Clojure.

**Warning:** This is highly experimental, don’t depend on it for serious stuff. Also, the API is not yet stabilised.

## Usage

You need to already have a working Homebrew installation.

Add the following dependency to your `project.clj`:

```clojure
[clomebrew  "0.0.1"]
```

Then import `clomebrew.core` and create your `brew` instance. It’ll
automatically find your Homebrew installation from your `PATH`.

```clojure
(ns your-ns
  (:require [clomebrew.core :as hb]))

(def brew (hb/new-brew))
```

### Examples

#### Get a Formula

```clojure
(hb/formula brew "git")

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
                   :files {:el_capitan {:sha256 "11d...31"
                                        :url "https://.../git-2.13.2.el_capitan.bottle.tar.gz"}
                           :sierra {:sha256 "ce6...54"
                                    :url "https://.../git-2.13.2.sierra.bottle.tar.gz"}
                           :yosemite {:sha256 "2c4...c6"
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

#### Run `brew doctor`

```clojure
(hb/doctor brew)
```

#### Get a formula’s content

```clojure
(slurp (hb/formula-path brew "git"))
```

#### Get Homebrew’s prefix/repo/cellar/cache paths

```clojure
(hb/prefix brew) ;; e.g. "/usr/local"
(hb/repository brew) ;; e.g. "/usr/local/Homebrew"
(hb/cellar brew) ;; e.g. "/usr/local/Cellar"
(hb/cache brew) ;; e.g. "/Users/you/Library/Caches/Homebrew"
```

## License

Copyright © 2017 Baptiste Fontaine

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
