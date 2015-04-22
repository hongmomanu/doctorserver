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
                                                                           1
                                                                           websocket/channel-hub-key
                                                                           ))

  (POST "/doctor/sendmyDoctorToPatient"[patientid doctorid fromdoctorid] (doctor/sendmypatientToDoctor
                                                                           patientid doctorid fromdoctorid
                                                                           1
                                                                           websocket/channel-hub-key
                                                                           ))

  (POST "/doctor/addblacklist"[patientid doctorid ] (doctor/addblacklist patientid doctorid ))

  (POST "/doctor/acceptrecommend"[rid ] (doctor/acceptrecommend rid 1 websocket/channel-hub-key))
  (POST "/doctor/acceptquickapply"[aid patientid doctorid addmoney]
    (doctor/acceptquickapply aid patientid doctorid addmoney websocket/channel-hub-key))

  (GET "/doctor/sendmsgtopatient" [doctorid patientid message] (doctor/sendmsgtopatient doctorid patientid message))

  (POST "/doctor/newdoctor" req

    (doctor/newdoctor req)

    )


  (POST "/doctor/updatedoctorlocation" [lon lat doctorid]

    (doctor/updatedoctorlocation lon lat doctorid)

    )

  (POST "/doctor/adddoctorbyid" [from to ]

    (doctor/adddoctorbyid from to websocket/channel-hub-key)

    )

 (POST "/doctor/getmypatient" [doctorid]

    (doctor/getmypatient  doctorid)

    )



 )
