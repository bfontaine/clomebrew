ENV.update(DEFAULT_CLOMEBREW_ENV)

require "global"

# Kernel.fork monkeypatching
# based off https://stackoverflow.com/q/19102575/735926
require "ffi"

module Exec
  extend FFI::Library
  ffi_lib FFI::Library::LIBC
  attach_function :fork, [], :int
end

module Process
  def fork
    pid = Exec.fork
    if pid.zero?
      yield if block_given?
      return
    else
      return pid
    end
  end
end

module Kernel
  def fork
    Process.fork
  end
end
# /Kernel.fork monkeypatching

# monkeypatch Utils.popen not to use IO.popen with a block
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
