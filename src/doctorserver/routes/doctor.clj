(ns doctorserver.routes.doctor
  (:require [doctorserver.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.java.io :as io]
            [doctorserver.controller.doctor :as doctor]
            [doctorserver.public.websocket :as websocket]
            ))



(defroutes doctor-routes

  (GET "/doctor/test" [] (str "test"))
  (POST "/doctor/sendmypatientToDoctor"[patientid doctorid fromdoctorid] (doctor/sendmypatientToDoctor
                                                                           patientid doctorid fromdoctorid
                                                                           websocket/channel-hub-key
                                                                           ))

  (POST "/doctor/sendmyDoctorToPatient"[patientid doctorid fromdoctorid] (doctor/sendmypatientToDoctor
                                                                           patientid doctorid fromdoctorid
                                                                           websocket/channel-hub-key
                                                                           ))

  (POST "/doctor/addblacklist"[patientid doctorid ] (doctor/addblacklist patientid doctorid ))

  (GET "/doctor/sendmsgtopatient" [doctorid patientid message] (doctor/sendmsgtopatient doctorid patientid message))

 )
