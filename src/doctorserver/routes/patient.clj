(ns doctorserver.routes.patient
  (:require [doctorserver.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.java.io :as io]
            [doctorserver.controller.patient :as patient]
            [doctorserver.public.websocket :as websocket]
            [doctorserver.controller.doctor :as doctor]

            ))



(defroutes patient-routes

  (GET "/patient/getmypatientsbyid" [ patientid ] (patient/getmypatientsbyid  patientid ))

  (GET "/patient/getmydoctorsbyid" [ patientid ] (patient/getmydoctorsbyid  patientid ))

  (POST "/patient/sendmyDoctorToPatient"[patientid doctorid frompatientid] (doctor/sendmypatientToDoctor
                                                                           patientid doctorid frompatientid
                                                                           0
                                                                           websocket/channel-hub-key
                                                                           ))

  (POST "/patient/acceptrecommend"[rid ] (doctor/acceptrecommend rid 0 websocket/channel-hub-key))


 )
