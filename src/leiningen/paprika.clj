(ns leiningen.paprika
  (:require [paprika.auth :as auth]
            [paprika.http :as http]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.java.browse :as browse :refer [browse-url]]
            [leiningen.core.project :as project]
            [leiningen.repl :as repl]))

(defn response-handler
  [{:keys [r-status r-body]}]
  (let [html "<html><head><title>%d</title><body>%s</body></html>"]
    {:status r-status
     :headers {"Content-Type" "text/html"}
     :body (format html r-status r-body)}))

(defn wrap-auth
  [client args token-p]
  (fn [req]
    (if-let [code (get-in req [:params "code"])]
      (do
        (deliver token-p (auth/request-server-token (assoc args :code code)))
        (client (assoc req :r-status 200 :r-body "200 OK")))
      (do
        (deliver token-p {})
        (client (assoc req :r-status 404 :r-body "404 Not Found"))))))

(defn authenticate
  [args]
  (println "HERE")
  (clojure.pprint/pprint args)
  (let [args (merge {:host "http://localhost" :port 8000 :scope []} args)
        args (assoc args :redirect-uri (str (:host args) ":" (:port args)))
        token-p (promise)
        handler (wrap-params (wrap-auth response-handler args token-p))
        server (jetty/run-jetty handler {:port (:port args) :join? false})]
    (browse-url (auth/generate-server-auth-url args))
    (let [result @token-p]
      (Thread/sleep 1000)
      (.stop server)
      result)))

(defn normalize-opts
  [opts]
  (http/transform-keys #(keyword (subs % 1)) (apply hash-map opts)))

(defn authenticate-command
  [project opts]
  (clojure.pprint/pprint (authenticate (merge (:paprika project) opts))))

(defn paprika-repl
  [project]
  {:paprika-repl {:injections [`(def ~(symbol "user")
                                  (authenticate ~(:paprika project)))]}})

(defn repl-command
  [project opts]
  (let [project (-> project
                    (project/project-with-profiles-meta (paprika-repl project))
                    (project/set-profiles [:paprika-repl]))]
    (clojure.pprint/pprint project)
    (repl/repl project)))

(def commands
  {:authenticate authenticate-command
   :auth authenticate-command
   :repl repl-command})

(defn ^:no-project-needed paprika
  [project & args]
  (if-let [f (get commands (keyword (first args)))]
    (f project (normalize-opts (rest args)))
    (println "Unknown Paprika Command: " (first args))))
