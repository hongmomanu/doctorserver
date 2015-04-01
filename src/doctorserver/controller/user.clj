(ns doctorserver.controller.user
  (:use compojure.core)
  (:require [doctorserver.db.core :as db]
            [doctorserver.public.common :as common]
            [noir.response :as resp]
            [clojure.data.json :as json]
            )
  )




(defn getuserlocation [id]
    (let [ a (db/get-user id)]

    (json/write-str a :value-fn common/write-ObjectId)
    ;(resp/json [{:foo "bar"}])

    )

  )

(defn getdoctors []
    (let [doctors (db/get-doctors)]
        (json/write-str doctors :value-fn common/write-ObjectId)
    )
)

