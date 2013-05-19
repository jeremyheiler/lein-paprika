(ns leiningen.paprika
  (:require [paprika.auth :as auth]
            [paprika.http :as http]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]
            [clojure.java.browse :as browse :refer [browse-url]]))

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
    (browse-url (auth/generate-server-auth-url args))
    (let [result @token-p]
      (Thread/sleep 1000)
      (.stop server)
      result)))

(defn paprika
  [project & args]
  (if (= "token" (first args))
    (let [args (apply hash-map (rest args))
          args (http/transform-keys #(keyword (subs % 1)) args) 
          result (authenticate args)]
      (clojure.pprint/pprint result))
    (println "Unknown Paprika Command: " (first args))))
