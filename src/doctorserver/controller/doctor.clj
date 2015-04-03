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
  (let [noreadmessage  (db/get-message {:toid (ObjectId. id) :isread false})
        channel (get @channel-hub-key id)
        ]
    (send! channel (json/write-str noreadmessage ) false)
    (db/update-message  {:toid id} {:isread true})
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
                 channel (get @channel-hub-key to)
             ]
               (when-not (nil? channel)
                (send! channel (json/write-str message ) false)
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


