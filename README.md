# lein-paprika

A Leiningen plugin for launching an App.net-authenticated Clojure REPL. As the name suggests, this project depends on [Paprika](https://github.com/literally/paprika) for interacting with App.net.

## Usage

This plugin is available on [Clojars](https://clojars.org/com.literallysoftware/lein-paprika). Add the following to the `:plugins` vector in either your `project.clj` or a profile.

```clojure
[com.literallysoftware/lein-paprika "0.0.1"]
```

Use the following command to launch a REPL that authenticates you with one of your [App.net apps](https://account.app.net/developer/apps/).

```
lein paprika repl :client-id "YOUR CLIENT ID" :client-secret "YOUR CLIENT SECRET"
```

However, it is recommended that you add the `:paprika` key to your `project.clj` or a profile with both the `:client-id` and `:client-secret` keys. You may optionally provided a `:host` and `:port` key for the Callback URL. Their default values are "localhost" and "8000", respectively.

### Standalone

This plugin can be used outside the context of a project. In order to use it, you must add the plugin to a profile in your `~/profiles.clj` file. Unless you have a reason not to, it is recommended to add the plugin and its configuration to the default `:user` profile.

Then from any directory you can launch a REPL with the following subcommand.

```
lein paprika repl

;; with the "foo" profile
lein with-profile foo paprika repl
```

Once the REPL has launched, you can access your information from `user`.

```clojure
;; To pretty print the whole thing
(clojure.pprint/pprint user)

;; To obtain your access token
(:access-token user)

;; To create a post
(require 'paprika.core)
(paprika.core/create-post {:text "I'm posting this from my #clojure repl!"} (:access-token user))
```

If you just want to see that information with out launching a REPL, you can use the `auth` subcommand.

```
lein paprika auth
```

## Support

* Search the [issues](/issues), and open one if you didn't find your answer.
* Message [@literally](https://app.net/literally) or [@jeremyheiler](/https://app.net/jeremyheiler) on App.net.
* Join the [Paprika](http://patter-app.net/room.html?channel=17641) Patter room.

## License

Copyright © 2013 Literally Software Inc.

Distributed under the Eclipse Public License, the same as Clojure.
