(ns doctorserver.controller.user
  (:use compojure.core)
  (:require [doctorserver.db.core :as db]
            ;[doctorserver.public.common :as common]
            [noir.response :as resp]
            [clojure.data.json :as json]
            [monger.json]
            )
  (:import [org.bson.types ObjectId]
           )
  )




(defn getuserlocation [id]
    (let [ a (db/get-user id)]

    (json/write-str a )
    ;(resp/json [{:foo "bar"}])

    )

  )

(defn getdoctors []
    (let [doctors (db/get-doctors)]
        (json/write-str doctors)
    )
)
(defn getdoctorsbyid [id]
    (let [
           rids (map #(ObjectId. (:rid %)) (db/get-relation-doctor id))
           doctors (db/get-doctors-byid  rids)
           ]
        (json/write-str doctors)
    )
)
(defn doctorlogin [username password]
    (let [
        doctor (db/get-doctor-byusername username)
        userinfo (:userinfo doctor)
    ]
    (if (and doctor (= password (:password userinfo)))(json/write-str {:success true :user doctor})
    (json/write-str {:success false}))
    )
)

