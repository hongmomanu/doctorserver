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



(defn getnoread [id channel-hub-key]
  (let [noreadmessage  (db/get-message {:toid  id :isread false})
        channel (get @channel-hub-key id)
        ;user (db/get-doctor-byid  (ObjectId. id))
        noreadmessage-userinfo (map #(conj % {:userinfo (:userinfo (db/get-doctor-byid  (ObjectId. (:fromid %))))}) noreadmessage)
        ]
    (send! channel (json/write-str {:type "doctorchat" :data noreadmessage-userinfo} ) false)
    (db/update-message  {:toid id} {:isread true})
    )

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


