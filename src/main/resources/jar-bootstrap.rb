require "rubygems"

require 'fpm'
require 'fpm/command'

exit(FPM::Command.run || 0)