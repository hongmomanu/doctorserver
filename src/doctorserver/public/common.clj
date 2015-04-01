(ns doctorserver.public.common
  (:use compojure.core)
  (:require
            [clojure.data.json :as json]
            )
  )


(defn write-ObjectId [k v]
    (let [condition (class v)]
        (cond (= org.bson.types.ObjectId condition) (str v) (= java.util.Date condition)
        (str (java.sql.Date. (.getTime v))) :else v)

    )

    )

