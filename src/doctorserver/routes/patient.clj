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

  (GET "/patient/getmydoctorsbyid" [ patientid ] (patient/getmydoctorsbyid  patientid true))


  (GET "/patient/getquickdoctorsbyid" [ patientid distance] (patient/getquickdoctorsbyid  patientid distance))

  (POST "/patient/sendmyDoctorToPatient"[patientid doctorid frompatientid] (doctor/sendmypatientToDoctor
                                                                           patientid doctorid frompatientid
                                                                           0
                                                                           websocket/channel-hub-key
                                                                           ))

  (POST "/patient/acceptrecommend"[rid ] (doctor/acceptrecommend rid 0 websocket/channel-hub-key))

  (GET "/patient/applyfordoctor"[patientid doctorid ] (patient/applyfordoctor patientid doctorid ))

  (POST "/patient/makeapplyfordoctor"[patientid doctorid] (patient/makeapplyfordoctor patientid doctorid ))


  (POST "/patient/makemoneybyuserid" [userid money] (patient/makemoneybyuserid userid money true))


  (POST "/patient/makemoneybyuseridwithapply" [userid money doctorid]
    (patient/makemoneybyuseridwithapply userid money doctorid))

 (POST "/patient/backmoneybyuseridwithapply" [userid  doctorid]
    (patient/backmoneybyuseridwithapply userid  doctorid))

  (POST "/patient/continuewithapply" [userid  doctorid]
    (patient/continuewithapply userid  doctorid))




 )
