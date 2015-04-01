(ns doctorserver.handler
  (:require [compojure.core :refer [defroutes routes]]
            [doctorserver.routes.home :refer [home-routes]]
            [doctorserver.routes.user :refer [user-routes]]
            [doctorserver.public.websocket :as websocket]
            [doctorserver.middleware
             :refer [development-middleware production-middleware]]
            [doctorserver.session :as session]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [cronj.core :as cronj]))

(defroutes base-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info
     :enabled? true
     :async? false ; should be always false for rotor
     :max-message-per-msecs nil
     :fn rotor/appender-fn})

  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "doctorserver.log" :max-size (* 512 1024) :backlog 10})

  (if (env :dev) (parser/cache-off!))
  ;;start the expired session cleanup job
  (cronj/start! session/cleanup-job)
  (timbre/info "\n-=[ doctorserver started successfully"
               (when (env :dev) "using the development profile") "]=-")
  (websocket/start-server 3001)
  )

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "doctorserver is shutting down...")
  (cronj/shutdown! session/cleanup-job)
  (timbre/info "shutdown complete!"))

(def app
  (-> (routes
        home-routes
        user-routes
        base-routes)
      development-middleware
      production-middleware))
