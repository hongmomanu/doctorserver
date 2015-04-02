(ns doctorserver.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [clojure.java.io :as io]
    [monger.core :as mg]
                      [monger.collection :as mc]
                      [monger.operators :refer :all]

    ))

#_(def db-store (str (.getName (io/file ".")) "/site.db"))

#_(def db-spec
  {:classname   "org.h2.Driver"
   :subprotocol "h2"
   :subname     db-store
   :make-pool?  true
   :naming      {:keys   clojure.string/lower-case
                 :fields clojure.string/upper-case}})

#_(defqueries "sql/queries.sql" {:connection db-spec})

;; Tries to get the Mongo URI from the environment variable
;; MONGOHQ_URL, otherwise default it to localhost mongodb://127.0.0.1/jack!:1313!@127.0.0.1/doctorapp
(defonce db (let [uri (get (System/getenv) "MONGOHQ_URL" "mongodb://jack:1313@127.0.0.1/doctorapp")
                  {:keys [conn db]} (mg/connect-via-uri uri)]
              db))

(defn create-user [user]
  (mc/insert db "users" user))

(defn update-user [id first-name last-name email]
  (mc/update db "users" {:id id}
             {$set {:first_name first-name
                    :last_name last-name
                    :email email}}))

(defn get-user [id]

  (mc/find-maps
        db "userslocation" {:userid id}  [:userid])
  )

(defn get-doctors[]
    (mc/find-maps
     db "doctors"
     )

)

(defn create-message [message]
    (mc/insert-and-return db "messages" message)

)

(defn update-message [cond modified]

(mc/update db "messages" cond {$set modified} {:multi true})

)

(defn get-doctor-byusername [username]
    (mc/find-one-as-map
         db "doctors" {:userinfo.username username}
     )
)
