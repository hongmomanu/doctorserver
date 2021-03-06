(ns doctorserver.db.core
  (:require
    [yesql.core :refer [defqueries]]
    [clojure.java.io :as io]
    [monger.core :as mg]
    [monger.collection :as mc]
    [monger.operators :refer :all]
    [monger.query :refer [with-collection find options paginate] ]

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

(defn getilldata []

  (mc/find-maps
    db "illdata" {} ["name" "depts"]
    )
  )

(defn getilldatapages [page limit]


  (with-collection db "illdata"
    (find {})
    (paginate :page page :per-page limit))

  )
(defn getcommondrugsbypage [page limit]


  (with-collection db "commondrugs"
    (find {})
    (paginate :page page :per-page limit))

  )

(defn getdrugdatapages [page limit]


  (with-collection db "drugdetail"
    (find {})
    (paginate :page page :per-page limit))

  )

(defn editilldata [item id]
  (mc/update db "illdata" {:_id id} {$set item} )
  )

(defn editdrugdata [item id]
  (mc/update db "drugdetail" {:_id id} {$set item} )
  )

(defn editommondrugdata [item id]
  (mc/update db "commondrugs" {:_id id} {$set item} )
  )

(defn insertdrugdatas [datas]
  (mc/insert-batch db "drugdetail" datas)
  )

(defn insertcommondrugdatas [datas]
  (mc/insert-batch db "commondrugs" datas)

  )


(defn getilldatanum []

  (mc/count
    db "illdata" {}
    )
  )

(defn get-hospitaldepts []
  (mc/find-maps
    db "hospitaldept" {} ["name" "deptcode"]
    )
  )

(defn getilldatabyid [illid]
  (mc/find-map-by-id
    db "illdata" illid
    )
  )

(defn update-doctor [cond modified]
  (mc/update db "doctors" cond {$set modified} )
  )

(defn make-new-doctor [patient]
  (mc/insert-and-return db "doctors" patient)
  )

(defn get-doctors-by-cond [cond]

  (mc/find-maps
    db "doctors" cond
    )

  )

(defn getcommondrugs-by-cond [cond]
  (mc/find-maps
    db "commondrugs" cond
    )

  )
(defn getdrugdetail-by-id[drugid]
  (mc/find-map-by-id
    db "drugdetail" drugid
    )
  )

(defn getaiddetail-by-id[aid]
  (mc/find-map-by-id
    db "aiddetail" aid
    )
  )
(defn getassaydetail-by-id[aid]
  (mc/find-map-by-id
    db "assaydetail" aid
    )
  )
(defn getaids-by-cond [cond fields]
  (mc/find-maps
    db "aiddetail" cond fields
    )
  )

(defn get-hospitaldeptclassify []
  (mc/find-maps
    db "hospitaldeptclassify"
    )
  )
(defn get-hospitaldept-by-cond [cond]
  (mc/find-maps
    db "hospitaldept" cond
    )
  )
(defn getassays-by-cond  [cond fields]
  (mc/find-maps
    db "assaydetail" cond fields
    )
  )
(defn get-drugsclassify-by-cond [cond]

  (mc/find-maps
    db "drugsclassify" cond
    )
  )

(defn del-classify-by-id [oid]
  (mc/remove-by-id db "drugsclassify" oid)
  )
(defn get-assayclassify-by-cond [cond]

  (mc/find-maps
    db "assayclassify" cond
    )
  )



(defn getaids-by-cond [cond fields]
  (mc/find-maps
    db "aiddetail" cond fields
    )
  )
(defn get-aidclassify-by-cond [cond]

  (mc/find-maps
    db "aidclassify" cond
    )
  )

(defn get-drugsclassifynum-by-cond [cond]

  (mc/count
    db "drugsclassify" cond
    )
  )
(defn get-assayclassifynum-by-cond [cond]

  (mc/count
    db "assayclassify" cond
    )
  )
(defn get-aidclassifynum-by-cond [cond]

  (mc/count
    db "aidclassify" cond
    )
  )

(defn getcommondrugsnum [cond]

  (mc/count
    db "commondrugs" cond
    )
  )

(defn getdrugs-by-cond [cond fields]
  (mc/find-maps
    db "drugdetail" cond fields
    )
  )

(defn create-message [message]
    (mc/insert-and-return db "messages" message)

)

(defn get-message [cond]
  (mc/find-maps db "messages" cond)

  )
(defn get-message-num [cond]
  (mc/count db "messages" cond)
  )
(defn getdrugdatanum []
  (mc/count
    db "drugdetail" {}
    )
  )

(defn update-message [cond modified]

(mc/update db "messages" cond {$set modified} {:multi true})

)

(defn get-doctor-byusername [username]
    (mc/find-one-as-map
         db "doctors" {:userinfo.username username}
     )
)
(defn get-patient-byusername [username]
    (mc/find-one-as-map
         db "patients" {:username username}
     )
)
(defn make-new-patient [patient]
  (mc/insert-and-return db "patients" patient)
  )
(defn get-doctors-byid [ids]
  (mc/find-maps
    db "doctors" {:_id {$in ids}}
    )
  )

(defn get-depts-by-code [codes]

  (mc/find-maps
    db "hospitaldept" {:deptcode {$in codes}}
    )

  )



(defn get-relation-doctor [cond]
  (mc/find-maps db "doctorsvsdoctors" cond )
  )

(defn get-relation-patient [cond]
  (mc/find-maps db "doctorsvspatients" cond )
  )
(defn get-experts []
  (mc/find-maps db "experts" {} )
  )

(defn get-patients-byid [ids]
  (mc/find-maps
    db "patients" {:_id {$in ids}}
    )
  )

(defn get-blaclist [cond]
  (mc/find-maps
    db "blacklist" cond
    )
  )

(defn get-patient-byid [oid]
  (mc/find-map-by-id
    db "patients" oid
    )
  )

(defn get-enumerate-by-type [type]

  (mc/find-maps
    db "enumerate" {:enumeratetype type}
    )

  )


(defn get-doctor-byid [oid]
  (mc/find-map-by-id db "doctors" oid)

  )

(defn makerecommend [cond recommend]

  (mc/update db "recommend" cond {$set recommend} {:upsert true})

  )

(defn updateclassify [oid classify]

  (mc/update-by-id db "drugsclassify" oid {$set classify})
  )
(defn insertclassify [ classify]

  (mc/insert db "drugsclassify" classify)
  )

(defn makedoctorsvspatients [cond recommend]

  (mc/update db "doctorsvspatients" cond {$set recommend} {:upsert true})

  )

(defn makedoctorsvsdoctors [data]

  (mc/insert db "doctorsvsdoctors" data)

  )

(defn findrecommend [cond]
  (mc/find-one-as-map
    db "recommend" cond
    )
  )

(defn findrecommends [cond]
  (mc/find-maps
    db "recommend" cond
    )
  )


(defn update-recommend [cond modified]

  (mc/update db "recommend" cond {$set modified} {:multi true})

  )

(defn update-recommend-return [data]
  (mc/save-and-return db "recommend" )
  )

(defn update-custompush [cond modified]
  (mc/update db "custompush" cond {$set modified} {:upsert true})
  )

(defn createblacklist [cond modified]
  (mc/update db "blacklist" cond {$set modified} {:upsert true})
  )

(defn create-applydoctors [cond modified]
  (mc/update db  "applydoctors" cond {$set modified} {:upsert true})
  )
(defn update-applydoctors [cond modified]
  (mc/update db  "applydoctors" cond {$set modified} )
  )

(defn get-applyingquick [cond]
  (mc/find-one-as-map db "applydoctors" cond)
  )

(defn get-applyingquick-list [cond]
  (mc/find-maps db "applydoctors" cond)
  )

(defn get-custompush  [cond]
  (mc/find-one-as-map
    db "custompush" cond
    )

  )
(defn get-apply-by-pid-dic [cond]

  (mc/find-one-as-map
    db "applyquick" cond
    )
  )

(defn get-apply-by-pid [cond]

  (mc/find-maps
    db "applyquick" cond
    )
  )

(defn make-apply-by-pid-dic [cond modified]

  (mc/update db "applyquick" cond {$set modified} {:upsert true})

  )

(defn get-money-byid [userid]
  (mc/find-one-as-map
    db "money" {:userid userid}
    )

  )

(defn update-money-byid [cond modified]
  (mc/update db "money" cond {$set modified} {:upsert true})
  )