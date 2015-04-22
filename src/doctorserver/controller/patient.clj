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
            [clj-time.core :as t]
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
                     {:applyid patientid :addmoney 0 :isreply false :doctorid doctorid :applytime (l/local-now)})

           money (db/get-money-byid patientid)
           money-doctor (db/get-money-byid doctorid)



           totalmoney (:totalmoney money)

           totalmoney-doctor (:totalmoney money-doctor)

           totalmoney (if (nil? totalmoney) 0 totalmoney)


           totalmoney-doctor (if (nil? totalmoney-doctor) 0 totalmoney-doctor)


           ]

      (if(and money (>= totalmoney commonfunc/applymoney))(do
                                                  (db/update-money-byid {:userid patientid} {:totalmoney (- totalmoney commonfunc/applymoney)}
                                                    )
                                                 (db/update-money-byid {:userid doctorid} {:totalmoney (+ totalmoney-doctor commonfunc/applymoney)}
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

(defn getmoneybyid [userid]

  (try
    (let [
           money (db/get-money-byid userid)
           money (if (nil? money) 0 (:totalmoney money))
           ]

      (resp/json {:success true :money money})
      )
    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      )
    )

  )

(defn makemoneybyuserid [userid addmoney isreturn]

  (try
    (let [
           money (db/get-money-byid userid)
           totalmoney (:totalmoney money)
           totalmoney (if (nil? totalmoney) 0 totalmoney)
           addmoney (read-string addmoney)
           ]

      (db/update-money-byid {:userid userid} {:totalmoney (+ totalmoney addmoney)})

      (when isreturn (resp/json {:success true :message  (+ totalmoney addmoney)}))

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

  (let [
         addmoney (:addmoney (db/get-apply-by-pid-dic {:applyid userid :doctorid doctorid}))
         applymoney (+ commonfunc/applymoney addmoney)

         ]

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



  )

(defn backmoneybydoctorwithapply [patientid  doctorid]

  (let [
         addmoney (:addmoney (db/get-apply-by-pid-dic {:applyid patientid :doctorid doctorid}))
         applymoney (+ commonfunc/applymoney addmoney)
         ]

    (try
      (do
        (makemoneybyuserid patientid (str "" applymoney) false)
        (makemoneybyuserid doctorid (str "-" applymoney) false)
        (db/make-apply-by-pid-dic {:applyid patientid :doctorid doctorid} {:isreply true})
        (resp/json {:success true})
        )
      (catch Exception ex
        (println (.getMessage ex))
        (resp/json {:success false :message (.getMessage ex)})
        )
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
(defn applyforsingledoctor [patientid doctorid channel-hub-key]

  (db/create-applydoctors {:patientid patientid :doctorid doctorid}
    {:isaccept false :isread false :applytime (l/local-now) :patientid patientid :doctorid doctorid})

  (let [
         user  (db/get-patient-byid  (ObjectId. patientid))
         channel (get @channel-hub-key doctorid)
         ]
    (when-not (nil? channel)

      (send! channel (json/write-str {:type "patientquickapply" :data {:userinfo  user}}) false)

      (db/create-applydoctors  {:patientid patientid :doctorid doctorid} {:isaccept false :isread true})

      )

    )




  )
(defn getquickapplying [patientid channel-hub-key]



  (let [
         oldtime (t/plus (l/local-now) (t/minutes commonfunc/applyquicktime) )
         applytrue (db/get-applyingquick {:patientid patientid
                                          :applytime
                                          { "$gte" oldtime }
                                          :isaccept true })

         applyingquick (if(nil? applytrue)(db/get-applyingquick {:patientid patientid
                                                                 :applytime
                                                                 { "$gte" oldtime }
                                                                 :isread false }) nil)
          channel (get @channel-hub-key patientid)
         ]

    (println applyingquick)

    (when-not (nil? applyingquick)
      (send! channel (json/write-str {:type "quickapplying" :data applyingquick} ) false)
      )
    )
  )

(defn getquickaccept [patientid channel-hub-key]
  (let [
         oldtime (t/plus (l/local-now) (t/minutes commonfunc/applyquicktime) )
         applyaccepted (db/get-apply-by-pid {:applyid patientid
                                          :applytime
                                          { "$gte" oldtime }
                                          :ispay true })


         channel (get @channel-hub-key patientid)
         ]


    (dorun (map #(

                   (send! channel (json/write-str {:type "quickaccept" :data (db/get-doctor-byid (ObjectId. (:doctorid %)))} ) false)

                   ) applyaccepted))


    )


  )
(defn ispatientinapplybydoctorid [patientid doctorid channel-hub-key]

  (let [
         oldtime (t/plus (l/local-now) (t/minutes commonfunc/applyquicktime) )

         applyaccepted (db/get-apply-by-pid-dic {:applyid patientid
                                             :doctorid doctorid
                                             :applytime
                                             { "$gte" oldtime }
                                              :isreply false
                                             :ispay true })

         ]
    (if (nil? applyaccepted) (resp/json {:success false :msg "用户未申请或已退款"})
      (resp/json {:success true}))

  )

  )
(defn applyforquickdoctorswhocanhelp [patientid doctorids channel-hub-key]

  (try
    (do
      (dorun (map #(applyforsingledoctor patientid % channel-hub-key) doctorids))

      (resp/json {:success true})
      )

    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      )
    )
  ;(resp/json {:success true})


  )

(defn newpatient [username realname password]

  (try
    (let [
           patient (db/get-patient-byusername username)

           ]
      (if (nil? patient) (resp/json {:success true :message (db/make-new-patient
                           {:username username
                            :realname realname
                            :password password
                            })}) (resp/json {:success true :message "用户已存在"}))

      )

    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      )

    )

  )


(defn adddoctorbyid [patientid doctorid channel-hub-key]

  (try
    (let [
           rels (db/get-relation-patient {:doctorid doctorid :patientid patientid})


           channel (get @channel-hub-key doctorid)

           ]

      (if (> (count rels) 0) (resp/json {:success false :message  "关系已经存在"} ) (

                                                             (do

                                                               (db/makedoctorsvspatients {:doctorid doctorid :patientid patientid} {:doctorid doctorid :patientid patientid})

                                                               (future (send! channel (json/write-str {:type "scanadd" :data (db/get-patient-byid (ObjectId. patientid))} ) false))

                                                               (resp/json {:success true})

                                                               )

                                                             ))




      )

    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      )

    )

  )


