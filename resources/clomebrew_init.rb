ENV.update(DEFAULT_CLOMEBREW_ENV)

require "global"

# monkeypatch Utils.popen not to use IO.popen("-") and thus not to fork
require "utils/popen"

module Utils
  def self.popen(args, mode)
    IO.popen(args.join(" "), mode) do |pipe|
      if pipe
        return pipe.read unless block_given?
        yield pipe
        # else
        #     $stderr.reopen("/dev/null", "w")
        #     exec(*args)
      end
    end
  end
end
# /Utils.popen monkeypatching

# monkeypatch Formulary.ensure_utf8_encoding to work around a JRuby IO issue
# https://github.com/jruby/jruby/issues/4712
require "formulary"

module Formulary
  def self.ensure_utf8_encoding(io)
    io.set_encoding(Encoding::UTF_8)
    io
  end
end
# /Formulary monkeypatch
