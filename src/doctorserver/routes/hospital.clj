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

  (GET "/hospital/getallhospitaldepts"[]
    (hospital/getallhospitaldepts )
    )

  (POST "/hospital/editilldata"[illdata]
    (hospital/editilldata illdata)
    )

  (POST "/hospital/getdeptsbycode"[codes]
    (hospital/getdeptsbycode codes)
    )

  (POST "/hospital/getpossibleillsbypage" [rowsname totalname page limit]
    (hospital/getpossibleillsbypage rowsname totalname page limit )
    )

  (POST "/hospital/getdrugsbypage" [rowsname totalname page limit]
    (hospital/getdrugsbypage rowsname totalname page limit )
    )

  (POST "/hospital/getcommondrugsbypage" [rowsname totalname page limit]
    (hospital/getcommondrugsbypage rowsname totalname page limit )
    )

  (GET "/hospital/getcommondrugs" []

    (hospital/getcommondrugs )

    )
  (GET "/hospital/getallclassify" [id]

    (hospital/getallclassify id)

    )
  (GET "/hospital/getclassifytree" [id]

    (hospital/getclassifytree id)

    )
  (GET "/hospital/getdrugsbypid" [pid]

    (hospital/getdrugsbypid pid)

    )
  (GET "/hospital/getexperts" []

    (hospital/getexperts )

    )
  (GET "/hospital/getdrugclassifybypid" [pid]

    (hospital/getdrugclassifybypid pid)

    )
  (GET "/hospital/getassayclassifybypid" [pid]

    (hospital/getassayclassifybypid pid)

    )
  (GET "/hospital/getaidclassifybypid" [pid]

    (hospital/getaidclassifybypid pid)

    )
  (POST "/hospital/getdrugdetailbyid" [drugid]

    (hospital/getdrugdetailbyid drugid)

    )
  (POST "/hospital/getaiddetailbyid" [pid]

    (hospital/getaiddetailbyid pid)

    )
  (POST "/hospital/getassaydetailbyid" [pid]

    (hospital/getassaydetailbyid pid)

    )
  (GET "/hospital/getaidsbypid" [pid]

    (hospital/getaidsbypid pid)

    )
  (GET "/hospital/getassaysbypid" [pid]

    (hospital/getassaysbypid pid)

    )

  (POST "/hospital/sendsoap" [url content action]

    (hospital/log-sendsoap url content action)
    )

  (POST "/hospital/getilldetailbyid" [illid]

    (hospital/getilldetailbyid illid)

    )

  (POST "/hospital/getmenusbytype" [type]

    (hospital/getmenusbytype type)

    )


  )
