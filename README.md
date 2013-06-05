# lein-paprika

A Leiningen plugin that allows you to interact with the App.net API. The primary feature at the moment is being able to launch a Clojure REPL as an App.net client. As the name suggests, this project uses [Paprika](https://github.com/literally/paprika) for the App.net integration.

An [App.net Developer account](https://join.app.net/signup?plan=developer) is required to use this plugin.

## Usage

This plugin is available on [Clojars](https://clojars.org/com.literallysoftware/lein-paprika). Add the following to the `:plugins` vector in either your `project.clj` or a profile.

```clojure
[com.literallysoftware/lein-paprika "0.0.1"]
```

You do not need a project in order to use this plugin.

### Getting Started

You must have an App.net app in order to use this plugin. If you don't already have one, go to the [developer dashboard](https://account.app.net/developer/apps/) to create it. You will be prompted to enter a Callback URL, and it should default to `http://localhost:8000`. You may change the port number, but leave the host as "localhost".

### Launching a REPL

Once you have created your app, you'll have a Client ID and Client Secret available to you. (Be sure to keep your Client Secret protected.) Fill those values into the command blow at your terminal. You should also provide `:host` and `:port` parameters if you deviated from the deault `http://localhost` and `8000`.

```
lein paprika repl :client-id "CLIENT ID" :client-secret "CLIENT SECRET"
```

Once the REPL has launched, you can access your information from `user`.

```clojure
;; To pretty print the whole map
(clojure.pprint/pprint user)

;; To obtain your access token
(:access-token user)

;; To create a post
(require 'paprika.core)
(paprika.core/create-post {:text "I'm posting this from my #clojure repl!"} (:access-token user))
```

### Storing Parameters

It is recommended that you add the `:paprika` key to your `project.clj` or a profile with both the `:client-id` and `:client-secret` keys. You may optionally provided a `:host` and `:port` key for the Callback URL as well.

```clojure
{:paprika {:client-id "YOUR CLIENT ID"
           :client-secret "YOUR CLIENT SECRET"
           :host "http://localhost"
           :port 3000}}
```

Then you can omit the parameters from the command.

```
lein paprika repl
```

Be careful to not push your Client Secret to a public location. The best way to prevent this is to put these parameters in a profile that is stored on your machine, and not in a shared project.

### No REPL

If you just want to obtain an access token without a REPL, you can use the `auth` subcommand.

```
lein paprika auth
```

## Support

* Search the [issues](/issues), and open one if you didn't find your answer.
* Message [@literally](https://app.net/literally) or [@jeremyheiler](https://app.net/jeremyheiler) on App.net.
* Join the [Paprika](http://patter-app.net/room.html?channel=17641) Patter room.

## License

Copyright © 2013 Literally Software Inc.

Distributed under the Eclipse Public License, the same as Clojure.
