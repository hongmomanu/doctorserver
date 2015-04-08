(ns doctorserver.controller.settings
  (:use compojure.core  org.httpkit.server)
  (:require [doctorserver.db.core :as db]
            ;;[doctorserver.public.common :as common]
            [noir.response :as resp]
            [clojure.data.json :as json]
            [clj-time.local :as l]
            [clj-time.format :as f]
            [monger.joda-time]
            )

    (:import [org.bson.types ObjectId]
              )
  )



(defn savecustompush [content sendtime  doctorid frequency]

  (try
    (do
      (db/update-custompush  {:doctorid doctorid} {:content content
                                                   :sendtime (l/to-local-date-time sendtime)
                                                   :doctorid doctorid
                                                   :frequency frequency})
      (resp/json{:success true })
      )
    (catch Exception ex
      (println  (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      ))



  )

(defn getcustompush [doctorid]

  (try
    (do
      (resp/json {:success true :data (db/get-custompush  {:doctorid doctorid} )})
      )
    (catch Exception ex
      (println  (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      ))



  )

(defn getblaclistbyid [doctorid]

  (try
    (do
      (let [
             list (db/get-blaclist  {:doctorid doctorid} )
             patientlist (map #(conj % {:patientinfo (db/get-patient-byid (ObjectId. (:patientid %)))}) list)
             ]
        (resp/json patientlist)
        )

      )
    (catch Exception ex
      (println  (.getMessage ex))
      (resp/json [])
      ))



  )



