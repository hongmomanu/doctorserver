(ns doctorserver.controller.user
  (:use compojure.core)
  (:require [doctorserver.db.core :as db]
            [noir.response :as resp]
            [clojure.data.json :as json]
            )
  )



(defn getuserlocation [id]
    (let [ a (db/get-user id)]

    (println "121")
    (println (json/write-str a))
    (resp/json {:foo "bar"})

    )

  )

