(ns doctorserver.routes.hospital
  (:use clj.qrgen)
  (:require [doctorserver.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :as resp]
            [clojure.java.io :as io]
            [noir.io :as nio]

            [clj-time.coerce :as c]
            [clj-time.local :as l]
            [noir.response :as nresp]
            [org.httpkit.client :as http]
            [doctorserver.public.common :as commonfunc]
            [doctorserver.controller.hospital :as hospital]
            [ring.util.response :refer [file-response]]

            ))



(defroutes hospital-routes
  (GET "/hospital/getappointmentcategory" []

    (hospital/getappointmentcategory )

    )
  (GET "/hospital/getappointmentcategorychild" [pid]

    (hospital/getappointmentcategorychild pid)

    )
  (GET "/hospital/getappointmentcategorydoctors" [pid]

    (hospital/getappointmentcategorydoctors pid)

    )
  (GET "/hospital/getreservedoctortimes" [pid]

    (hospital/getreservedoctortimes pid)

    )
  (GET "/hospital/getpossibleills" []

    (hospital/getpossibleills )

    )


  )
