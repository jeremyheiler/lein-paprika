# lein-paprika

A Leiningen plugin for launching an App.net-authenticated Clojure REPL. As the name suggests, this project depends on [Paprika](https://github.com/literally/paprika) for interacting with App.net.

## Usage

This plugin is available on [Clojars](https://clojars.org/com.literallysoftware/lein-paprika). Add the following to your `:plugins` vector in either your `project.clj` or a profile.

```clojure
[com.literallysoftware/lein-paprika "0.0.1"]
```

You are required to add a `:paprika` key to your `project.clj` or profile with the credentials for [your app](https://account.app.net/developer/apps/).

* `:client-id` - This is the Client ID created for your app.

* `:client-secret` - This is the Client Secret created for your app.

You may optionally provided a `:host` and a `:port` for the Callback URL.

### Standalone

This plugin can be used outside the context of a project. In order to use it, you must add the plugin to a profile in your `~/profiles.clj` file. Unless you have a reason not to, it is recommended to add the plugin and its configuration to the default `:user` profile.

Then from any directory you can launch a REPL with the following subcommand.

```
lein paprika repl

;; with profile "foo"
lein with-profile foo paprika repl
```

Once the REPL has launched, the default namespace will contain a `user` var that points to the map recieved from App.net once properly authenticated.

```clojure
;; To pretty print the whole thing
(clojure.pprint/pprint user)

;; To obtain your access token
(:access-token user)
```

If you just want to see that information with out launching a REPL, you can use the `auth` subcommand.

```
lein paprika auth
```

## License

Copyright © 2013 Literally Software Inc.

Distributed under the Eclipse Public License, the same as Clojure.
