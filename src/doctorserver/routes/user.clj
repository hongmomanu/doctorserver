(ns doctorserver.routes.user
  (:require [doctorserver.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.java.io :as io]
            [doctorserver.controller.user :as user]
            ))



(defroutes user-routes

  (GET "/user/getuserlocation" [] (user/getuserlocation 1))
  (GET "/user/getdoctors" [] (user/getdoctors ))
  (GET "/user/getdoctorsbyid" [id] (user/getdoctorsbyid id))
  (GET "/user/getpatientsbyid" [id] (user/getpatientsbyid id))
  (POST "/user/doctorlogin" [username password] (user/doctorlogin username password))
  (POST "/user/patientlogin" [username password] (user/patientlogin username password))

 )
