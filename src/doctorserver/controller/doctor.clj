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

(declare noreadrecommend-process)
(defn noreadrecommend-process [noreadrecommend]
  (let [
         fromid (:fromid noreadrecommend)
         doctorid (:doctorid noreadrecommend)
         patientid (:patientid noreadrecommend)
         rectype (:rectype noreadrecommend)
         frominfo (if (= rectype 1) (db/get-doctor-byid  (ObjectId. fromid))
                    (db/get-patient-byid  (ObjectId. fromid))
                    )

         patientinfo (db/get-patient-byid  (ObjectId. patientid))

         ]
    (conj noreadrecommend {:frominfo frominfo :patientinfo patientinfo})

    )

  )
(defn getnoread [id channel-hub-key]
  (let [
         noreadmessage  (db/get-message {:toid  id :isread false})

         noreadrecommend (db/findrecommends {:doctorid id :isreadbydoctor false})
         channel (get @channel-hub-key id)
        ;user (db/get-doctor-byid  (ObjectId. id))
        noreadmessage-userinfo (map #(conj % {:userinfo (:userinfo (db/get-doctor-byid  (ObjectId. (:fromid %))))}) noreadmessage)

         noreadrecommend-userinfo (map #(noreadrecommend-process %) noreadrecommend)
        ]
    (send! channel (json/write-str {:type "doctorchat" :data noreadmessage-userinfo} ) false)

    (send! channel (json/write-str {:type "recommend" :data noreadrecommend-userinfo} ) false)

    (db/update-message  {:toid id} {:isread true})
    (db/update-recommend   {:doctorid id} {:isreadbydoctor true})
    )

  )

(defn acceptrecommend [rid channel-hub-key]
  (try
    (do
      (let [
              update (db/update-recommend  {:_id (ObjectId. rid)} {:isdoctoraccepted true} )
              updateobj (db/findrecommend {:_id (ObjectId. rid)})
             ]
        (futrue (sendrecommendconfirm updateobj channel-hub-key))
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
(defn sendmypatientToDoctor [patientid doctorid fromdoctorid channel-hub-key]
    (try
      (do
        (let [
               channelp (get @channel-hub-key patientid)
               channeld (get @channel-hub-key doctorid)
               recommend (db/makerecommend {:patientid patientid :doctorid doctorid} {:patientid patientid :doctorid doctorid :fromid fromdoctorid
                                            :isdoctoraccepted false :ispatientaccepted false :rectype 1
                                            :isreadbydoctor false :isreadbypatient false})

               recommendmap (db/findrecommend {:patientid patientid :doctorid doctorid})
               recommendid (:_id recommendmap)

               patient (db/get-patient-byid (ObjectId. patientid))
               doctor (db/get-doctor-byid  (ObjectId. doctorid))
               ]

          (when-not (nil? channelp)
            (send! channelp (json/write-str {:type "recommend" :data [(noreadrecommend-process recommendmap)]} ) false)
            (db/update-recommend  {:_id recommendid} {:isreadbypatient true} )
            )

          (when-not (nil? channeld)
            (send! channeld (json/write-str {:type "recommend" :data [(noreadrecommend-process recommendmap)]} ) false)
            (db/update-recommend  {:_id recommendid} {:isreadbydoctor true} )
            )
          (resp/json {:success true})
          )
        )
      (catch Exception ex
        (println (.getMessage ex))
        (resp/json {:success false :message (.getMessage ex)})
        ))
  )



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

(defn chatprocess [data channel-hub-key]
;;{type chatdoctor, from 551b4cb83b83719a9aba9c01, to 551b4e1d31ad8b836c655377, content 1212}
    (let [ type (get data "type")
           from (get data "from")
           to   (get data "to")
           content (get data "content")
           message {:content content :fromid from :toid to :msgtime (l/local-now) :isread false}
        ]
     (try
          (do

             (let [
                 newmessage (db/create-message message)
                 messagid (:_id newmessage)
                 user (db/get-doctor-byid  (ObjectId. from))
                 channel (get @channel-hub-key to)
             ]
               (when-not (nil? channel)
                (send! channel (json/write-str {:type "doctorchat" :data [(conj message {:userinfo (:userinfo user)})]} ) false)
                (db/update-message  {:_id messagid} {:isread true} )
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


