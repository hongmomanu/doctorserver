(ns doctorserver.controller.hospital
  (:use compojure.core)
  (:require [doctorserver.db.core :as db]
            ;[doctorserver.public.common :as common]
            [noir.response :as resp]
            [clojure.data.json :as json]
            [monger.json]
            [clj-http.client :as client]
            )
  (:import [org.bson.types ObjectId]
           )
  )


(defn getappointmentcategory []

  #_(resp/json [{:name "常约医生"} {:name "外科"} {:name "内科"}
              {:name "生殖中心"} {:name "五官"} {:name "皮肤"}
              {:name "中医"} {:name "妇产"} {:name "儿科"}
              {:name "疼痛"} {:name "肿瘤"} {:name "医学美容"}

              ])

  (resp/json (db/get-hospitaldeptclassify ))

  )
(defn getappointmentcategorychild [pid]

  #_(resp/json [{:name "胃肠外科"} {:name "aaa"} {:name "bbb"}
              {:name "bbbb"} {:name "胃肠外科"} {:name "胃肠外科"}
              {:name "cc"} {:name "eee"} {:name "ffff"}
              {:name "gggg"} {:name "hhh"} {:name "iiii"}

              ])

  (resp/json (db/get-hospitaldept-by-cond {:parentid pid}))

  )

(defn getappointmentcategorydoctors [pid]

  (resp/json [
               {:name "jack" :info "ssss" :time "05-07 上午" :num "20/30"} {:name "lucy" :info "ssss" :time "05-07 上午" :num "20/30"}
               {:name "mike" :info "ssss" :time "05-07 上午" :num "20/30"} {:name "Lily" :info "ssss" :time "05-07 上午" :num "20/30"}
               {:name "Jim" :info "ssss" :time "05-07 上午" :num "20/30"} {:name "Tom" :info "ssss" :time "05-07 上午" :num "20/30"}
              {:name "Steven" :info "ssss" :time "05-07 上午" :num "20/30"} {:name "House" :info "ssss" :time "05-07 上午"  :num "20/30"}
               {:name "Sam" :info "ssss" :time "05-07 上午" :num "20/30"} {:name "Angel" :info "ssss" :time "05-07 上午" :num "20/30"}
               {:name "Danel" :info "ssss" :time "05-07 上午" :num "20/30"} {:name "Howard" :info "ssss" :time "05-07 上午" :num "20/30"}
              ])

  )

(defn getmenusbytype [type]

  (resp/json (case type
    "功能配置" [{:text "疾病管理" :value "possibleillmanager"}]

   [])
    )

  )

(defn getreservedoctortimes [pid]

  (resp/json [
               {:num 11  :time "08:34" } {:num 12  :time "08:37" }  {:num 13  :time "08:40" }
               {:num 14  :time "08:44" } {:num 15  :time "08:47" } {:num 16  :time "08:51" }
               {:num 17  :time "09:00" } {:num 18  :time "09:05" } {:num 19  :time "09:09" }
               {:num 20  :time "09:15" } {:num 21  :time "09:20" } {:num 22  :time "09:25" }
               ])

  )

(defn getpossibleills []
  (resp/json  (db/getilldata))
  )

(defn getpossibleillsbypage [rowsname totalname page limit]

  (let [
         nums  (db/getilldatanum)
         results (db/getilldatapages (read-string page) (read-string limit))
         ]

    (resp/json (assoc {} rowsname results totalname nums))
    )



  )
(defn getilldetailbyid [illid]
    (resp/json  (db/getilldatabyid (ObjectId. illid)))

  )

(defn getcommondrugs []
  (resp/json  (db/getcommondrugs-by-cond {} ))

  )

(defn getdrugsbypid [pid]
  (resp/json  (db/getdrugs-by-cond {:parentids pid} ["name"]))
  )
(defn getdrugclassifybypid [pid]
  (let [
         datas (db/get-drugsclassify-by-cond {:parentid pid})
         mapdatas (map #(conj {:counts (db/get-drugsclassifynum-by-cond {:parentid (str (:_id %))})} %) datas)
         ]
    (resp/json mapdatas)
    )

  )

(defn getassayclassifybypid [pid]
  (let [
         datas (db/get-assayclassify-by-cond {:parentid pid})
         mapdatas (map #(conj {:counts (db/get-assayclassifynum-by-cond {:parentid (str (:_id %))})} %) datas)
         ]
    (resp/json mapdatas)
    )

  )
(defn getaidclassifybypid [pid]
  (let [
         datas (db/get-aidclassify-by-cond {:parentid pid})
         mapdatas (map #(conj {:counts (db/get-aidclassifynum-by-cond {:parentid (str (:_id %))})} %) datas)
         ]
    (resp/json mapdatas)
    )

  )

(defn getaiddetailbyid [pid]

  (resp/json  (db/getaiddetail-by-id (ObjectId. pid)))

  )
(defn getassaydetailbyid [pid]

  (resp/json  (db/getassaydetail-by-id (ObjectId. pid)))

  )

(defn log-sendsoap [url content action]
  (let [
         ;h {"SOAPAction" action}
         content (client/post url {:body content  :content-type  "application/soap+xml; charset=utf-8"   :socket-timeout 10000
                                   :conn-timeout 10000})       ;:form-params (dissoc query-params "url")
         ]
    (:body content)
    )
  ;(resp/json {:success true})

  )

(defn getaidsbypid [pid]

   (resp/json  (db/getaids-by-cond {:parentids pid} ["name"]))

  )
(defn getassaysbypid [pid]

   (resp/json  (db/getassays-by-cond {:parentids pid} ["name"]))

  )
(defn getdrugdetailbyid [drugid]
  (resp/json  (db/getdrugdetail-by-id (ObjectId. drugid)))
  )






