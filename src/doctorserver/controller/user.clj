(ns doctorserver.controller.user
  (:use compojure.core)
  (:require [doctorserver.db.core :as db]
            [noir.response :as resp]
            [clojure.data.json :as json]
            )
  )


(defn write-ObjectId [k v]
  (if (= org.bson.types.ObjectId (class v))
    (str v)
    v))

(defn getuserlocation [id]
    (let [ a (db/get-user id)]

    (json/write-str a :value-fn write-ObjectId)
    ;(resp/json [{:foo "bar"}])

    )

  )

