(ns doctorserver.controller.patient
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


(defn patient-process [docwithpatient]
  (map #(conj {:patientinfo (db/get-patient-byid (ObjectId. (:patientid %)))}
          {:doctorinfo (db/get-doctor-byid (ObjectId. (:doctorid %))) } ) docwithpatient)
  )

(defn getmypatientsbyid [patientid]
  (let [
         mydoctors (db/get-relation-patient {:patientid patientid})
         doctorallpatients (map #(patient-process (db/get-relation-patient {:doctorid (:doctorid %)})) mydoctors)
         patients (apply concat  doctorallpatients)
         filters (filter (fn [x]
                           (not= (:_id (:patientinfo x)) patientid))
                   patients)
         ]



    (resp/json filters)

    )

  )

(defn getmydoctorsbyid [patientid]
  (let [
         mydoctors (db/get-relation-patient {:patientid patientid})
         doctorinfo (map #(db/get-doctor-byid (ObjectId. (:doctorid %))) mydoctors)
         ]
    (resp/json doctorinfo)
    )


  )


