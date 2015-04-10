(ns doctorserver.controller.doctor
  (:use compojure.core  org.httpkit.server)
  (:require [doctorserver.db.core :as db]
            ;;[doctorserver.public.common :as common]
            [noir.response :as resp]
            [clojure.data.json :as json]
            [clj-time.local :as l]
            [monger.joda-time]
            )

    (:import [org.bson.types ObjectId]
              )
  )

(declare noreadrecommend-process sendrecommendconfirm)
(defn noreadrecommend-process [noreadrecommend]
  (let [
         fromid (:fromid noreadrecommend)
         doctorid (:doctorid noreadrecommend)
         patientid (:patientid noreadrecommend)
         rectype (:rectype noreadrecommend)
         frominfo (if (= rectype 1) (db/get-doctor-byid  (ObjectId. fromid))
                    (db/get-patient-byid  (ObjectId. fromid))
                    )
         doctorinfo (db/get-doctor-byid (ObjectId. doctorid))

         patientinfo (db/get-patient-byid  (ObjectId. patientid))

         ]
    (conj noreadrecommend {:frominfo frominfo :patientinfo patientinfo :doctorinfo doctorinfo})

    )
  )
(defn getnoread [id readtype channel-hub-key]
  (let [
         noreadmessage  (db/get-message {:toid  id :isread false :fromtype 1})
         noreadmessage-patient  (db/get-message {:toid  id :isread false :fromtype 0})


         noreadrecommend (if (= 1 readtype)
                           (db/findrecommends {:doctorid id :isreadbydoctor false })
                           (db/findrecommends {:patientid id :isreadbypatient false })
                           )
         channel (get @channel-hub-key id)
        ;user (db/get-doctor-byid  (ObjectId. id))
        noreadmessage-userinfo (map #(conj % {:userinfo (:userinfo (db/get-doctor-byid  (ObjectId. (:fromid %))))}) noreadmessage)
         noreadmessage-patientinfo (map #(conj % {:patientinfo (db/get-patient-byid  (ObjectId. (:fromid %)))}) noreadmessage-patient)
         noreadrecommend-userinfo (map #(noreadrecommend-process %) noreadrecommend)
        ]
    (send! channel (json/write-str {:type "doctorchat" :data
    (concat noreadmessage-userinfo noreadmessage-patientinfo)} ) false)

    (send! channel (json/write-str {:type "recommend" :data noreadrecommend-userinfo} ) false)

    (db/update-message  {:toid id} {:isread true})
    (if (= 1 readtype) (db/update-recommend   {:doctorid id} {:isreadbydoctor true})
      (db/update-recommend   {:patientid id} {:isreadbypatient true}))
    )

  )




(defn acceptrecommend [rid type channel-hub-key]
  (try
    (do
      (let [
              update (if (= 1 type) (db/update-recommend  {:_id (ObjectId. rid)} {:isdoctoraccepted true} )
                       (db/update-recommend  {:_id (ObjectId. rid)} {:ispatientaccepted true} )
                       )
              updateobj (db/findrecommend {:_id (ObjectId. rid)})
             ]
        (future (sendrecommendconfirm updateobj channel-hub-key))
        (resp/json {:success true})
        )

      )
    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      ))
  )

(defn sendrecommendconfirm [recommend channel-hub-key]
  (when (and (:isdoctoraccepted recommend) (:ispatientaccepted recommend))

     (let [
           patientid (:patientid recommend)

           doctorid (:doctorid recommend)

           info (noreadrecommend-process recommend)

           channelp (get @channel-hub-key patientid)

           channeld (get @channel-hub-key doctorid)

           ]

       (db/makedoctorsvspatients {:doctorid doctorid :patientid patientid} {:doctorid doctorid :patientid patientid})

       (when-not (nil? channelp)
         (send! channelp (json/write-str {:type "recommendconfirm" :data [info]} ) false)
         ;(db/update-recommend  {:_id recommendid} {:isreadbypatient true} )
         )

       (when-not (nil? channeld)
         (send! channeld (json/write-str {:type "recommendconfirm" :data [info]} ) false)
         ;(db/update-recommend  {:_id recommendid} {:isreadbydoctor true} )
         )

      )

    )



  )
;;doctor recommend
(defn sendmypatientToDoctor [patientid doctorid fromdoctorid rectype channel-hub-key ]

    (try

      (do
        (if (> (count (db/get-relation-patient {:patientid patientid :doctorid doctorid})) 0)
          (resp/json {:success false :message "关系已建立，无需推荐"})
          (let [
                 channelp (get @channel-hub-key patientid)
                 channeld (get @channel-hub-key doctorid)
                 recommend (db/makerecommend {:patientid patientid :doctorid doctorid} {:patientid patientid :doctorid doctorid :fromid fromdoctorid
                                                                                        :isdoctoraccepted false :ispatientaccepted false :rectype rectype
                                                                                        :isreadbydoctor false :isreadbypatient false})

                 recommendmap (db/findrecommend {:patientid patientid :doctorid doctorid})
                 recommendid (:_id recommendmap)

                 patient (db/get-patient-byid (ObjectId. patientid))
                 doctor (db/get-doctor-byid  (ObjectId. doctorid))
                 ]

            (when-not (nil? channelp)
              (println "channelp")
              (send! channelp (json/write-str {:type "recommend" :data [(noreadrecommend-process recommendmap)]} ) false)
              (db/update-recommend  {:_id recommendid} {:isreadbypatient true} )
              )

            (when-not (nil? channeld)
              (println "channeld")
              (send! channeld (json/write-str {:type "recommend" :data [(noreadrecommend-process recommendmap)]} ) false)
              (db/update-recommend  {:_id recommendid} {:isreadbydoctor true} )
              )
            (resp/json {:success true})
            )

          )

        )
      (catch Exception ex
        (println (.getMessage ex))
        (resp/json {:success false :message (.getMessage ex)})
        ))
  )


;; send message to my patient
(defn sendmsgtopatient [channel-hub-key doctorid patientid message]
  (let [
         channel (get @channel-hub-key patientid)
         message {:content message :fromid doctorid :toid patientid :msgtime (l/local-now) :isread false}
         newmessage (db/create-message message)
         messagid (:_id newmessage)
         user (db/get-doctor-byid  (ObjectId. doctorid))
         ]

    (try
      (do
          (when-not (nil? channel)
            (send! channel (json/write-str {:type "doctorpatientchat" :data [(conj message {:userinfo (:userinfo user)})]} ) false)
            (db/update-message  {:_id messagid} {:isread true} )
            )
        {:success true}
        )
      (catch Exception ex
        (println (.getMessage ex))
        {:success false :message (.getMessage ex)}
        ))

    )

  )

;; add black list
(defn addblacklist [patientid doctorid]
  (try
    (do
      (db/createblacklist {:doctorid doctorid :patientid patientid} {:doctorid doctorid :patientid patientid})

      (resp/json {:success true})
      )
    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      ))

  )

;; chat process func begin here
(defn chatprocess [data  channel-hub-key]
;;{type chatdoctor, from 551b4cb83b83719a9aba9c01, to 551b4e1d31ad8b836c655377, content 1212}
    (let [ type (get data "type")
           from (get data "from")
           to   (get data "to")
           content (get data "content")
           fromtype (get data "fromtype")
           imgid (get data "imgid")
           message {:content content :fromid from :toid to
                    :msgtime (l/local-now) :isread false
                    :fromtype fromtype
                    }
        ]
     (try
          (do

             (let [
                 newmessage (db/create-message message)
                 messagid (:_id newmessage)
                 user (if (= fromtype 1) (db/get-doctor-byid  (ObjectId. from)) (db/get-patient-byid  (ObjectId. from)))
                 channel (get @channel-hub-key to)
                 channelfrom (get @channel-hub-key from)
             ]
               (when-not (nil? channel)
                (send! channel (json/write-str {:type "doctorchat" :data [(conj newmessage {:userinfo  user})]} ) false)

                 (db/update-message  {:_id messagid} {:isread true} )
               )

               (when-not (nil? channelfrom)
                 (send! channelfrom (json/write-str {:type "chatsuc" :data {:imgid imgid :toid to}} ) false)
                 ;(db/update-message  {:_id messagid} {:isread true} )
                 )


             )
                {:success true}
            )
          (catch Exception ex
          (println (.getMessage ex))
            {:success false :message (.getMessage ex)}
            ))

    ;;(json/write-str a :value-fn common/write-ObjectId)
    ;(resp/json [{:foo "bar"}])

    )

  )


