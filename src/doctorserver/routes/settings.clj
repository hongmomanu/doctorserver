(ns doctorserver.routes.settings
  (:require [doctorserver.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.java.io :as io]
            [doctorserver.controller.settings :as settings]

            ))



(defroutes settings-routes


  (POST "/settings/savecustompush" [content sendtime  doctorid frequency] (settings/savecustompush
                                                                          content sendtime  doctorid
                                                                          frequency
                                                                           ))

  (POST "/settings/getcustompush" [ doctorid ] (settings/getcustompush  doctorid ))
  (GET "/settings/getblaclistbyid" [ doctorid ] (settings/getblaclistbyid  doctorid ))
  (GET "/settings/getenumerate" [ type ] (settings/getenumerate  type ))

 )
