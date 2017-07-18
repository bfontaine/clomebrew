ENV.update(DEFAULT_CLOMEBREW_ENV)

# Homebrew's OS detection is broken on JRuby (TODO submit a PR) because it
# relies on RUBY_PLATFORM.
# See https://stackoverflow.com/a/13586108/735926.
require "rbconfig"

# TODO: use a fuller value to include e.g. hardware stuff
clomebrew_os =
  case RbConfig::CONFIG["host_os"]
  when /darwin|mac os/
    # Homebrew assumes this is set
    ENV["HOMEBREW_MACOS_VERSION"] = `/usr/bin/sw_vers -productVersion 2>&1`
    "darwin"
  when /linux/
    "linux"
  end

unless clomebrew_os.nil?
  # Don't print the const_set warning.
  # See https://github.com/bfontaine/silent/blob/3538cab/lib/silent.rb
  stderr_ = $stderr
  begin
    $stderr = StringIO.new
    Object.send(:const_set, "RUBY_PLATFORM", clomebrew_os)
  ensure
    $stderr = stderr_
  end
end

# Homebrew assumes this is always available
require "global"

# monkeypatch Utils.popen not to use IO.popen("-") and thus not to fork
require "utils/popen"

module Utils
  def self.popen(args, mode)
    IO.popen(args.join(" "), mode) do |pipe|
      if pipe
        return pipe.read unless block_given?
        yield pipe
      else
        $stderr.reopen("/dev/null", "w")
        exec(*args)
      end
    end
  end
end
# /Utils.popen monkeypatching

# monkeypatch Formulary.ensure_utf8_encoding to work around a JRuby IO issue
# https://github.com/jruby/jruby/issues/4712
# Fixed in JRuby 9.2.0.0
require "formulary"

module Formulary
  def self.ensure_utf8_encoding(io)
    io.set_encoding(Encoding::UTF_8)
    io
  end
end
# /Formulary monkeypatch

# monkeypatch quiet_system not to use fork
require "utils"
require "shellwords"

def quiet_system(*args)
  `#{Shellwords.join(args)} 2>&1` # capture stdin & stderr
  $?.success?
end
# /monkeypatch quiet_system

# this one is useful for e.g. Formula["foo"]
require "formula"
