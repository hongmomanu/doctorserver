(ns doctorserver.routes.patient
  (:require [doctorserver.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.java.io :as io]
            [doctorserver.controller.patient :as patient]

            ))



(defroutes patient-routes

  (GET "/patient/getmypatientsbyid" [ patientid ] (patient/getmypatientsbyid  patientid ))
  (GET "/patient/getmydoctorsbyid" [ patientid ] (patient/getmydoctorsbyid  patientid ))


 )
