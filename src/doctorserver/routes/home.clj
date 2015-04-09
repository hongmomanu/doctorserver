(ns doctorserver.routes.home
  (:require [doctorserver.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.response :as resp]
            [clojure.java.io :as io]

            ))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))

    (GET "/downloadtest/*" [] (resp/redirect "/cordova-app-hello-world-3.6.3.tar.gz"))


  (GET "/about" [] (about-page)))
