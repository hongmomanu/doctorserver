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
  #_(resp/json [{:name "乳房胀痛"} {:name "便秘、便血"} {:name "内、外痔"}
              {:name "凸眼"} {:name "口吃"} {:name "口臭"}
              {:name "吞咽困难"} {:name "咳嗽"} {:name "咳血"}
              {:name "喉咙异物"} {:name "喉咙痛、扁桃体发炎"} {:name "呕吐、吐血"}
              {:name "坐股神经痛"} {:name "压力引起之身心不适"} {:name "多(手)汗"}
              {:name "大便失禁"} {:name "失眠(多眠)症"} {:name "小便浑浊或气泡"}
              {:name "小儿腹泻、发烧、腹胀、便秘 "} {:name "尿失禁"} {:name "心悸"}
              {:name "心窝灼热感"} {:name "性病"} {:name "感冒"}
              {:name "手脚麻痹"} {:name "打鼾"} {:name "掉头发"}
              {:name "更年期障碍"} {:name "气促、喘不过气"} {:name "水肿"}
              {:name "消化不良、胃酸过多"} {:name "灰指甲、鸡眼"} {:name "狐臭"}
              {:name "疲劳、倦怠 癫痫"} {:name "发育不良"} {:name "皮屑疹"}
              {:name "眩晕(天旋地转)"} {:name "眼前小黑影(飞蚊症)"} {:name "眼睛干"}
              {:name "眼睛疲劳、红、氧、疼痛"} {:name "耳朵痛、耳朵塞住、流鼻血"} {:name "耳鸣、鼻塞、流鼻涕"}
              {:name "肌力减退或丧失"} {:name "肌肉压痛"} {:name "肌肉抽搐"}
              {:name "肝功能异常"} {:name "肝硬化"} {:name "肥胖"}
              {:name "肩背酸痛"} {:name "胸痛"} {:name "腰酸背痛"}
              {:name "腹痛"} {:name "腹胀、腹泻"} {:name "蜂窝组织炎"}
              {:name "血尿、尿频、解尿困难"} {:name "贫血"} {:name "身体表面长硬块"}
              {:name "过敏性鼻炎"} {:name "关节酸痛"} {:name "阴道分泌物增加"}
              {:name "静脉曲张"} {:name "头痛、头晕"} {:name "颈部肿大(甲状腺肿大、淋巴腺肿大)"}
              {:name "骨质疏松"} {:name "体重减轻"} {:name "高血压"} {:name "黄疸"} {:name "糖尿病"}

              ])



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






