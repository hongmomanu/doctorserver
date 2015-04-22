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
            [doctorserver.controller.doctor :as doctor]
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
           money (db/get-money-byid patientid)
           money-doctor (db/get-money-byid doctorid)

           totalmoney (if (nil? money) 0 (:totalmoney money))
           totalmoney-doctor (if (nil? money-doctor) 0 (:totalmoney money-doctor))
           ]

      (if (>= totalmoney commonfunc/applymoney)(do

                                                 (db/make-apply-by-pid-dic {:applyid patientid :doctorid doctorid}
                                                              {:applyid patientid :needmoney commonfunc/applymoney :isreply false :doctorid doctorid :applytime (l/local-now)})

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
           ;totalmoney (:totalmoney money)
           totalmoney (if (or (nil? money) (:totalmoney money)) 0 (:totalmoney money))
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
         needmoney (:needmoney (db/get-apply-by-pid-dic {:applyid userid :doctorid doctorid}))
         applymoney needmoney

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

(defn backmoneybydoctorwithapply [patientid  doctorid channel-hub-key]

  (let [
         needmoney (:needmoney (db/get-apply-by-pid-dic {:applyid patientid :doctorid doctorid}))
         applymoney needmoney
         ]

    (try
      (do
        (makemoneybyuserid patientid (str "" applymoney) false)
        (makemoneybyuserid doctorid (str "-" applymoney) false)
        (db/make-apply-by-pid-dic {:applyid patientid :doctorid doctorid} {:isreply true})
        (doctor/sendmsgtopatient channel-hub-key doctorid patientid "此次诊断已退款")
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
(defn applyforsingledoctor [patientid doctorid addmoney channel-hub-key]

  (db/create-applydoctors {:patientid patientid :doctorid doctorid}
    {:isaccept false :addmoney addmoney :isread false :applytime (l/local-now) :patientid patientid :doctorid doctorid})

  (let [
         user  (db/get-patient-byid  (ObjectId. patientid))
         channel (get @channel-hub-key doctorid)
         applydoctor (db/get-applyingquick {:patientid patientid :doctorid doctorid})
         ]
    (when-not (nil? channel)

      (send! channel (json/write-str {:type "patientquickapply" :data (conj applydoctor {:userinfo  user})}) false)

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

         applynotsaying (db/get-apply-by-pid {:applyid patientid
                                              :applytime
                                              { "$lte" oldtime }
                                              :ispay true })


         applynotsaying (filter (fn [x]
                                  (= (db/get-message-num
                                       {:fromid (:doctorid x) :msgtime
                                       { "$gte" (:applytime x)
                                         "$lte" (l/local-now) }}
                                       ) 0))
                          applynotsaying)




         applyaccepted (concat applyaccepted applynotsaying)


         channel (get @channel-hub-key patientid)
         ]


(println applyaccepted)
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
(defn applyforquickdoctorswhocanhelp [patientid doctorids addmoney channel-hub-key]

  (let [
         money (db/get-money-byid patientid)
         money (if (nil? money) 0 (:totalmoney money))
         needmoney (+ commonfunc/quickapplymoney (read-string addmoney))
         doctorids (json/read-str doctorids)

         ]
    (if (>= money needmoney) (try
                               (do
                                 (dorun (map #(applyforsingledoctor patientid % (read-string addmoney) channel-hub-key) doctorids))

                                 (resp/json {:success true})
                                 )

                               (catch Exception ex
                                 (println (.getMessage ex))
                                 (resp/json {:success false :message (.getMessage ex)})
                                 )
                               )

      (resp/json {:success false :message (str "余额" money "元,不足支付")})

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

                                                               (future (send! channel (json/write-str {:type "scanadd" :data (conj {:fromtype 0} (db/get-patient-byid (ObjectId. patientid)))} ) false))

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





