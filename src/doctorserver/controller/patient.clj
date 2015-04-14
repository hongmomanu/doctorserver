(ns doctorserver.controller.patient
  (:use compojure.core  org.httpkit.server)
  (:require [doctorserver.db.core :as db]
            ;;[doctorserver.public.common :as common]
            [noir.response :as resp]
            [clojure.data.json :as json]
            [clj-time.local :as l]
            [clj-time.format :as f]
            [monger.joda-time]
            [doctorserver.public.common :as commonfunc]
            [monger.operators :refer :all]
            )

    (:import [org.bson.types ObjectId]
              )
  )

(def applymoney 5)

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



(defn getmydoctorsbyid [patientid isreturn]
  (let [
         mydoctors (db/get-relation-patient {:patientid patientid})
         doctorinfo (map #(db/get-doctor-byid (ObjectId. (:doctorid %))) mydoctors)
         ]
    (if isreturn (resp/json doctorinfo) doctorinfo)
    )


  )

(defn applyfordoctor [patientid doctorid ]

  (let [
         myapply (db/get-apply-by-pid-dic {:applyid patientid :doctorid doctorid :ispay true})

         myapply (when myapply (conj myapply {:nums (db/get-message-num
                                                      {:fromid doctorid :msgtime
                                                      { "$gte" (:applytime myapply)
                                                        "$lte" (l/local-now) }}
                                                       )}))
         ]
    (resp/json myapply)
    )


  )

(defn makeapplyfordoctor [patientid doctorid ]

  (try
    (let [
           myapply (db/make-apply-by-pid-dic {:applyid patientid :doctorid doctorid}
                     {:applyid patientid :doctorid doctorid :applytime (l/local-now)})

           money (db/get-money-byid patientid)
           totalmoney (:totalmoney money)
           ]

      (if(and money (>= totalmoney applymoney))(do
                                                  (db/update-money-byid {:userid patientid} {:totalmoney (- totalmoney applymoney)}
                                                    )
                                                 (db/update-money-byid {:userid doctorid} {:totalmoney (+ totalmoney applymoney)}
                                                    )
                                                 (db/make-apply-by-pid-dic {:applyid patientid :doctorid doctorid} {:ispay true})

                                                          (resp/json {:success true})
                                                          )(resp/json {:success false :message
                                                        (str "余额" totalmoney "元,不足支付")}))
      ;(resp/json {:success true})
      )
    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      ))


  )


(defn makemoneybyuserid [userid addmoney isreturn]

  (try
    (let [
           money (db/get-money-byid userid)
           totalmoney (:totalmoney money)
           addmoney (read-string addmoney)
           ]

      (db/update-money-byid {:userid userid} {:totalmoney (+ totalmoney addmoney)})
      (when isreturn (resp/json {:success true}))
      )
    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      ))

  )

(defn makemoneybyuseridwithapply [userid money doctorid]

  (try
    (do
      (makemoneybyuserid userid money false)
      (makeapplyfordoctor userid doctorid)
        )
    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      )
    )

  )


(defn backmoneybyuseridwithapply [userid  doctorid]

  (try
    (do
      (makemoneybyuserid userid (str "" applymoney) false)
      (makemoneybyuserid doctorid (str "-" applymoney) false)
      (db/make-apply-by-pid-dic {:applyid userid :doctorid doctorid} {:ispay false})
      (resp/json {:success true})
        )
    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      )
    )

  )

(defn continuewithapply [userid  doctorid]
  (try
    (do
      (db/make-apply-by-pid-dic {:applyid userid :doctorid doctorid} {:applytime (l/local-now)})
      (resp/json {:success true})
      )

    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      )
    )

  )



(defn getquickdoctorsbyid  [patientid distance lon lat]
  (let [
         mydoctors (getmydoctorsbyid patientid false)
         nearbydoctors (db/get-doctors-by-cond  { :loc
                                                  { "$nearSphere"
                                                    { "$geometry"
                                                      {:type   "Point"
                                                      :coordinates  [ (read-string lon)  (read-string lat) ]
                                                       }
                                                      "$maxDistance"  (read-string distance)
                                                      } } } )

         alldoctors (if (> (count mydoctors) 0)
                      (let[
                            filterneardoctors (filter (fn [x]
                                                        (not (commonfunc/lazy-contains? mydoctors  x) ))
                                                nearbydoctors)
                            ]

                        (concat filterneardoctors mydoctors)
                        )

                      nearbydoctors
                      )
         ]

    (resp/json alldoctors)
    )

  )


