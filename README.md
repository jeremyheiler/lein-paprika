# lein-paprika

A Leiningen plugin for launching an App.net-authenticated REPL.

## Installation

```clojure
[com.literallysoftware/lein-paprika "0.0.1"]
```

## Usage

This plugin can be used standalone or from within a project.

### Standalone

Add the plugin to a profile in your `~/profiles.clj` file. For brevity, the example will show it being added to the default `:user` profile.

```clojure
{:user {:plugins [com.literallysoftware/lein-paprika "0.0.1"]
        :paprika {:client-id <YOUR CLIENT ID>
                  :client-secret <YOUR SECRET>}}}
```

Then for any directory you can launch a REPL like so:

```
lein paprika repl
```

Once the REPL has launched, the default namespace will contain a `user` var that points to the map recieved from App.net once properly authenticated. Print it with:

```clojure
(clojure.pprint/pprint user)
```

If you just want to see that information with out launching a REPL:

```
lein paprika auth
```

## License

Copyright © 2013 Literally Software Inc.

Distributed under the Eclipse Public License, the same as Clojure.
