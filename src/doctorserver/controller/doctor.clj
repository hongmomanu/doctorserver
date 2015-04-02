(ns doctorserver.controller.doctor
  (:use compojure.core  org.httpkit.server)
  (:require [doctorserver.db.core :as db]
            [doctorserver.public.common :as common]
            [noir.response :as resp]
            [clojure.data.json :as json]
            [clj-time.local :as l]
            [monger.joda-time]
            )

    (:import [org.bson.types ObjectId]
              )
  )




(defn chatprocess [data channel-hub-key]
;;{type chatdoctor, from 551b4cb83b83719a9aba9c01, to 551b4e1d31ad8b836c655377, content 1212}
(println 1111111111)
    (let [ type (get data "type")
           from (get data "data")
           to   (get data "to")
           content (get data "content")
           message {:content content :fromid from :toid to :msgtime (l/local-now)}
        ]
        (println "begin")
     (try
          (do
          (println message)
            (db/create-message message)
             (let [channel (get @channel-hub-key to)]
               (when-not (nil? channel)
                (send! channel (json/write-str message ) false)
                (db/update-message  {:toid to} {:isread true} )
               )

             )
                (println "success")
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


