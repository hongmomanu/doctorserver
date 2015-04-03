(ns doctorserver.routes.doctor
  (:require [doctorserver.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]
            [doctorserver.controller.doctor :as doctor]
            ))



(defroutes doctor-routes

  (GET "/doctor/test" [] (str "test"))
  (GET "/doctor/sendmsgtopatient" [doctorid patientid message] (doctor/sendmsgtopatient doctorid patientid message))

 )
