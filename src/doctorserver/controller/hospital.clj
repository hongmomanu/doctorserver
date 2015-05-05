(ns doctorserver.controller.hospital
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


(defn getappointmentcategory []

  (resp/json [{:name "常约医生"} {:name "外科"} {:name "内科"}
              {:name "生殖中心"} {:name "五官"} {:name "皮肤"}
              {:name "中医"} {:name "妇产"} {:name "儿科"}
              {:name "疼痛"} {:name "肿瘤"} {:name "医学美容"}

              ])

  )
(defn getappointmentcategorychild [pid]

  (resp/json [{:name "胃肠外科"} {:name "aaa"} {:name "bbb"}
              {:name "bbbb"} {:name "胃肠外科"} {:name "胃肠外科"}
              {:name "cc"} {:name "eee"} {:name "ffff"}
              {:name "gggg"} {:name "hhh"} {:name "iiii"}

              ])

  )

(defn getappointmentcategorydoctors [pid]

  (resp/json [
               {:name "jack" :info "ssss" :time "05-07 上午" :num "20/30"} {:name "lucy" :info "ssss" :time "05-07 上午" :num "20/30"}
               {:name "mike" :info "ssss" :time "05-07 上午" :num "20/30"} {:name "Lily" :info "ssss" :time "05-07 上午" :num "20/30"}
               {:name "Jim" :info "ssss" :time "05-07 上午" :num "20/30"} {:name "Tom" :info "ssss" :time "05-07 上午" :num "20/30"}
              {:name "Steven" :info "ssss" :time "05-07 上午" :num "20/30"} {:name "House" :info "ssss" :num "20/30"}
               {:name "Sam" :info "ssss" :time "05-07 上午" :num "20/30"} {:name "Angel" :info "ssss" :time "05-07 上午" :num "20/30"}
               {:name "Danel" :info "ssss" :time "05-07 上午" :num "20/30"} {:name "Howard" :info "ssss" :time "05-07 上午" :num "20/30"}
              ])

  )






