## What is this?
This project is a drop in replacement for `fpm` that you don't have to gem install and can even run without a Ruby
installation. It combines [JRuby](http://jruby.org/), [fpm](https://github.com/jordansissel/fpm), and some shell
scripting to create a single executable binary for running `fpm` with nothing more than just a JVM installed. It also
incorporates some hackery to speed up the unreasonably slow startup time for JRuby when there are additional gem paths
to search. [fpm](https://github.com/jordansissel/fpm) is an awesome tool, and we should all be grateful for its
existence.

## Installation
It's a drop in replacement for `fpm`, so you can download the latest version of `jfpm`, include it in your $PATH, and
set it executable all in one go:

```bash
sudo curl -o /usr/local/bin/fpm -L "https://github.com/rholder/jfpm/releases/download/v1.3.3.1/jfpm" && \
sudo chmod +x /usr/local/bin/fpm
```
I'm setting the versioning such that it's the target `fpm` version followed by an additional patch number in case I
update anything in between major releases.

## Explanation of Speed Hacks
First, if you create a fatjar and just drop all of the gems for a project into the root of an extracted
`jruby-complete.jar` then it will happily run for you (see [here](https://github.com/jruby/jruby/wiki/StandaloneJarsAndClasses)).
However, it will also be 10x slower than if you run your application with an external `ruby-gems.jar` rolled from a pile
of downloaded gems (as sort of described [here](http://watchitlater.com/blog/2011/08/rubygems-in-a-jar/)). Fine, so
let's just shove the `ruby-gems.jar` file into the final fatjar and extract it at runtime.

This was really more of a proof of concept to see if I could crank out a JRuby-standalone binary that didn't need quite
such a long time to bootstrap. Some future development might be to profile the exact order of resource loading being
done for the built-in files and try to optimize those lookups away in an additional cache. A previous iteration monkey
patched the built-in `org.jruby.runtime.load.LoadService` class to inject a cache for reading `.rb` files contained in
the external gem archive, but as of JRuby 1.7.18, it doesn't add any additional speedup so it's gone in favor of
sweeping up some of the complexity.

##License
The `jfpm` project is released under version 2.0 of the
[Apache License](http://www.apache.org/licenses/LICENSE-2.0). Portions of modified JRuby source are released under
EPL 1.0 as described [here](https://raw.githubusercontent.com/jruby/jruby/master/COPYING). [fpm](https://github.com/jordansissel/fpm)
is [MIT-style licensed](https://raw.githubusercontent.com/jordansissel/fpm/master/LICENSE).

## References
* https://github.com/jordansissel/fpm
* https://github.com/jruby/jruby/wiki/StandaloneJarsAndClasses
* http://watchitlater.com/blog/2011/08/rubygems-in-a-jar/
* https://github.com/opscode/omnibus-ruby
