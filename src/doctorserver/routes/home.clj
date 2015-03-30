(ns doctorserver.routes.home
  (:require [doctorserver.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]
            [doctorserver.controller.user :as user]
            ))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/getuserlocation" [] (user/getuserlocation 1))
  (GET "/about" [] (about-page)))
