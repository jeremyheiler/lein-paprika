(ns leiningen.paprika
  (:require [clojure.edn :as edn]
            [clojure.java.browse :as b]
            [clojure.pprint :as p]
            [leiningen.core.project :as project]
            [leiningen.core.user :as user]
            [leiningen.repl :as repl]
            [paprika.auth :as auth]
            [paprika.http :as http]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]])
  (:import [java.io File]))

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
  (let [args (merge {:host "http://localhost" :port 8000 :scope []} args)
        args (assoc args :redirect-uri (str (:host args) ":" (:port args)))
        token-p (promise)
        handler (wrap-params (wrap-auth response-handler args token-p))
        server (jetty/run-jetty handler {:port (:port args) :join? false})]
    (b/browse-url (auth/generate-server-auth-url args))
    (let [result @token-p]
      (Thread/sleep 1000)
      (.stop server)
      result)))

(defn cache-user
  [opts user]
  (if-let [file-path (:cache-file-path opts)]
    (spit file-path (prn-str user))
    (println "WARNING: Missing :cache-file-path")))

(defn auth-command
  [project opts user]
  (p/pprint user))

(defn paprika-repl-profile
  [project user]
  {:paprika-repl {:injections [`(def ~(symbol "user") ~user)]}})

(defn repl-command
  [project opts user]
  (let [project (-> project
                    (project/project-with-profiles-meta
                      (paprika-repl-profile opts user))
                    (project/set-profiles [:paprika-repl]))]
    (repl/repl project)))

(def commands
  {:auth auth-command
   :repl repl-command})

(defn normalize-opts
  [opts]
  (http/transform-keys #(keyword (subs % 1)) (apply hash-map opts)))

(defn ^:no-project-needed paprika
  [project & args]
  (if-let [f (get commands (keyword (first args)))]
    (let [file-path (if-let [root (:root project)]
                      (str root "/.paprika-token")
                      (str (user/leiningen-home) "/paprika-token"))
          opts (merge (:paprika project)
                      {:cache-file-path file-path}
                      (normalize-opts (rest args)))
          user (if (.exists (File. file-path))
                 (edn/read-string (slurp file-path))
                 (let [user (authenticate opts)]
                   (cache-user opts user)
                   user))]
      (f project opts user))
    (println "Unknown Paprika Command: " (first args))))
