## What is this?
This is a project that combines [JRuby](http://jruby.org/), [fpm](https://github.com/jordansissel/fpm), and some shell
scripting to create a single executable binary for running `fpm` with nothing more than just a JVM installed. It also
incorporates some hackery to speed up the unreasonably slow startup time for JRuby when there are additional gem paths
to search.

## Installation
It's a drop in replacement for `fpm`, so you can download the latest version of `jfpm`, include it in your $PATH, and
set it executable all in one go:

```bash
sudo curl -o /usr/local/bin/fpm -L "https://github.com/rholder/jfpm/releases/download/v1.0.2.1/jfpm" && \
sudo chmod +x /usr/local/bin/fpm
```
I'm setting the versioning such that it's the target `fpm` version followed by an additional patch number in case I
update anything in between major releases.

## Explanation of Speed Hacks
First, if you create a fatjar and just drop all of the gems for a project into the root of an extracted
`jruby-complete.jar` then it will happily run for you (see [here](https://github.com/jruby/jruby/wiki/StandaloneJarsAndClasses)).
However, it will also be 10x slower than if you run your application with an external `ruby-gems.jar` rolled from a pile
of downloaded gems (as sort of described [here](http://watchitlater.com/blog/2011/08/rubygems-in-a-jar/)). Fine, my
first hack is to just shove the `ruby-gems.jar` file into the final fatjar and extract it at runtime. (2 min -> 20s)

Next, I did several runs of timing this and that and came to the conclusion that the culprit is buried in the
`LoadService` and the order with which it searches for required dependencies. It iterates over every gem you've included
for every require (and its dependent requires). So I extracted an exact copy of that class, added some tooling to remove
it during the construction of the fatjar, and injected my own version for "experimentation." (monkey patch all the Java)

For the final hack, instead of searching for files that obviously don't exist, I instead check a cache that's built from
the `ruby-gems.jar` archive before we execute anything. Everything that was in the external `ruby-gems.jar` gets
returned immediately without having to go through the full path search. (20s -> 6s)

This was really more of a proof of concept to see if I could crank out a JRuby-standalone binary that didn't need quite
such a long time to bootstrap. Some future development might be to profile the exact order of resource loading being
done for the built-in files and try to optimize those lookups away in an additional cache.

##License
The `jfpm` project is released under version 2.0 of the
[Apache License](http://www.apache.org/licenses/LICENSE-2.0). Portions of modified JRuby source are released under
EPL 1.0 as described [here](https://raw.github.com/jruby/jruby/master/COPYING).

## References
* https://github.com/jordansissel/fpm
* https://github.com/jruby/jruby/wiki/StandaloneJarsAndClasses
* http://watchitlater.com/blog/2011/08/rubygems-in-a-jar/
* https://github.com/opscode/omnibus-ruby
