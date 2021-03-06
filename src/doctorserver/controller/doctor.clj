(ns doctorserver.controller.doctor
  (:use compojure.core  org.httpkit.server)
  (:require [doctorserver.db.core :as db]
            [doctorserver.public.common :as commonfunc]
            [noir.response :as resp]
            [clojure.data.json :as json]
            [clj-time.local :as l]
            [clj-time.core :as t]
            [monger.operators :refer :all]
            [monger.joda-time]
            )

    (:import [org.bson.types ObjectId]
              )
  )

(declare noreadrecommend-process sendrecommendconfirm chatprocess)
(defn noreadrecommend-process [noreadrecommend]
  (let [
         fromid (:fromid noreadrecommend)
         doctorid (:doctorid noreadrecommend)
         patientid (:patientid noreadrecommend)
         rectype (:rectype noreadrecommend)
         frominfo (if (= rectype 1) (db/get-doctor-byid  (ObjectId. fromid))
                    (db/get-patient-byid  (ObjectId. fromid))
                    )
         doctorinfo (db/get-doctor-byid (ObjectId. doctorid))

         patientinfo (db/get-patient-byid  (ObjectId. patientid))

         ]
    (conj noreadrecommend {:frominfo frominfo :patientinfo patientinfo :doctorinfo doctorinfo})

    )
  )
(defn getnoread [id readtype channel-hub-key]
  (let [
         noreadmessage  (db/get-message {:toid  id :isread false :fromtype 1})
         noreadmessage-patient  (db/get-message {:toid  id :isread false :fromtype 0})


         noreadrecommend (if (= 1 readtype)
                           (db/findrecommends {:doctorid id :isreadbydoctor false })
                           (db/findrecommends {:patientid id :isreadbypatient false })
                           )
         channel (get @channel-hub-key id)
        ;user (db/get-doctor-byid  (ObjectId. id))
        noreadmessage-userinfo (map #(conj % {:userinfo (:userinfo (db/get-doctor-byid  (ObjectId. (:fromid %))))}) noreadmessage)
         noreadmessage-patientinfo (map #(conj % {:userinfo (db/get-patient-byid  (ObjectId. (:fromid %)))}) noreadmessage-patient)
         noreadrecommend-userinfo (map #(noreadrecommend-process %) noreadrecommend)
        ]
    ;(println )
    (send! channel (json/write-str {:type "doctorchat" :data
    (concat noreadmessage-userinfo noreadmessage-patientinfo)} ) false)

    (send! channel (json/write-str {:type "recommend" :data noreadrecommend-userinfo} ) false)

    (db/update-message  {:toid id} {:isread true})
    (if (= 1 readtype) (db/update-recommend   {:doctorid id} {:isreadbydoctor true})
      (db/update-recommend   {:patientid id} {:isreadbypatient true}))
    )

  )

(defn updatedoctorlocation [lon lat doctorid]

  (println lon lat doctorid)

  (try
    (do
      (db/update-doctor {:_id (ObjectId. doctorid)} {:loc.coordinates
                                                     [ (read-string lon)
                                                       (read-string lat) ] })
      (resp/json {:success true })
      )
    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      ))

  )
(defn getquickapplying [doctorid channel-hub-key]
  (let [
         oldtime (t/plus (l/local-now) (t/minutes commonfunc/applyquicktime) )
         applynoread (db/get-applyingquick-list {:doctorid doctorid
                                          :applytime
                                          { "$gte" oldtime }
                                          :isread false })

         filterapply (filter (fn [x]
                               (nil? (db/get-applyingquick {:patientid (:patientid x)
                                                            :applytime
                                                            { "$gte" oldtime }
                                                            :isaccept true }) ))
                       applynoread)

         channel (get @channel-hub-key doctorid)

         ]



    (dorun (map #(do
             (send! channel (json/write-str {:type "patientquickapply"
                                            :data (conj % {:userinfo (db/get-patient-byid (ObjectId. (:patientid %)))})} ) false)
                   (db/update-applydoctors {:_id (:_id %)} {:isread true})
            )
      filterapply))


    )


  )

(defn acceptquickapply [rid patientid doctorid addmoney channel-hub-key]

  (try
    (let [

           oldtime (t/plus (l/local-now) (t/minutes commonfunc/applyquicktime) )

           applytrue (db/get-applyingquick {:patientid patientid
                                            :applytime
                                            { "$gte" oldtime }
                                            :isaccept true })

           addmoney (read-string addmoney)

           channel (get @channel-hub-key patientid)
           ]
      (if (nil? applytrue)
        (let [
               money (db/get-money-byid doctorid)
               totalmoney (:totalmoney money)
               totalmoney (if (nil? totalmoney) 0 totalmoney)
               patientmoney (db/get-money-byid patientid)
               ptotalmoney (:totalmoney patientmoney)
               ptotalmoney (if (nil? ptotalmoney) 0 ptotalmoney)
               needmoney (+ addmoney commonfunc/quickapplymoney)
               ]
          (db/update-applydoctors {:_id (ObjectId. rid)} {:isaccept true} )

          (db/update-money-byid {:userid doctorid} {:totalmoney (+ totalmoney needmoney)})
          (db/update-money-byid {:userid patientid} {:totalmoney (- ptotalmoney needmoney)})

          (db/make-apply-by-pid-dic {:applyid patientid :doctorid doctorid}
            {:applyid patientid :needmoney needmoney :isreply false :doctorid doctorid :applytime (l/local-now) :ispay true})

          (when-not (nil? channel)
            (send! channel (json/write-str {:type "quickaccept" :data (db/get-doctor-byid (ObjectId. doctorid))} ) false)
            )
          (resp/json {:success true})
          )
        (resp/json {:success false :msg "已被其他医生抢救了"})
        )


      )
    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      )
    )


  )




(defn acceptrecommend [rid type channel-hub-key]
  (try
    (do
      (let [
              update (if (= 1 type) (db/update-recommend  {:_id (ObjectId. rid)} {:isdoctoraccepted true} )
                       (db/update-recommend  {:_id (ObjectId. rid)} {:ispatientaccepted true} )
                       )
              updateobj (db/findrecommend {:_id (ObjectId. rid)})
             ]
        (future (sendrecommendconfirm updateobj channel-hub-key))
        (resp/json {:success true})
        )

      )
    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      ))
  )

(defn sendrecommendconfirm [recommend channel-hub-key]

  (println "sendrecommendconfirm")
  (println recommend)
  (when (and (:isdoctoraccepted recommend) (:ispatientaccepted recommend))

     (let [
           patientid (:patientid recommend)

           doctorid (:doctorid recommend)

           info (noreadrecommend-process recommend)

           channelp (get @channel-hub-key patientid)

           channeld (get @channel-hub-key doctorid)

           ]

       (db/makedoctorsvspatients {:doctorid doctorid :patientid patientid} {:doctorid doctorid :patientid patientid})



       (chatprocess {:type "doctorchat" :fromtype 0
                     :from patientid :to doctorid
                     :content "已添加您作为我的医生" :imgid -1} channel-hub-key)


       (chatprocess {:type "doctorchat" :fromtype 1
                     :from doctorid :to patientid
                     :content "已添加您作为我的患者" :imgid -1} channel-hub-key)




       #_(when-not (nil? channelp)
         (send! channelp (json/write-str {:type "recommendconfirm" :data info} ) false)
         ;(db/update-recommend  {:_id recommendid} {:isreadbypatient true} )
         )

       #_(when-not (nil? channeld)
         (send! channeld (json/write-str {:type "recommendconfirm" :data info} ) false)
         ;(db/update-recommend  {:_id recommendid} {:isreadbydoctor true} )
         )

      )

    )



  )

(defn getmypatient [doctorid]
  (resp/json (db/get-relation-patient {:doctorid doctorid}))
  )


;;doctor recommend
(defn sendmypatientToDoctor [patientid doctorid fromdoctorid rectype channel-hub-key ]

    (try

      (do
        (if (> (count (db/get-relation-patient {:patientid patientid :doctorid doctorid})) 0)
          (resp/json {:success false :message "关系已建立，无需推荐"})
          (let [
                 channelp (get @channel-hub-key patientid)
                 channeld (get @channel-hub-key doctorid)
                 recommend (db/makerecommend {:patientid patientid :doctorid doctorid} {:patientid patientid :doctorid doctorid :fromid fromdoctorid
                                                                                        :isdoctoraccepted false :ispatientaccepted false :rectype rectype
                                                                                        :isreadbydoctor false :isreadbypatient false})

                 recommendmap (db/findrecommend {:patientid patientid :doctorid doctorid})
                 recommendid (:_id recommendmap)

                 patient (db/get-patient-byid (ObjectId. patientid))
                 doctor (db/get-doctor-byid  (ObjectId. doctorid))
                 ]

            (when-not (nil? channelp)
              (println "channelp")
              (send! channelp (json/write-str {:type "recommend" :data [(noreadrecommend-process recommendmap)]} ) false)
              (db/update-recommend  {:_id recommendid} {:isreadbypatient true} )
              )

            (when-not (nil? channeld)
              (println "channeld")
              (send! channeld (json/write-str {:type "recommend" :data [(noreadrecommend-process recommendmap)]} ) false)
              (db/update-recommend  {:_id recommendid} {:isreadbydoctor true} )
              )
            (resp/json {:success true})
            )

          )

        )
      (catch Exception ex
        (println (.getMessage ex))
        (resp/json {:success false :message (.getMessage ex)})
        ))
  )


;; send message to my patient
(defn sendmsgtopatient [channel-hub-key doctorid patientid message]
  (let [
         channel (get @channel-hub-key patientid)
         message {:content message :fromid doctorid :fromtype 1 :toid patientid :msgtime (l/local-now) :isread false}
         newmessage (db/create-message message)
         messagid (:_id newmessage)
         user (db/get-doctor-byid  (ObjectId. doctorid))
         ]

    (try
      (do
          (when-not (nil? channel)
            (send! channel (json/write-str {:type "doctorchat" :data [(conj message {:userinfo (:userinfo user)})]} ) false)
            (db/update-message  {:_id messagid} {:isread true} )
            )
        {:success true}
        )
      (catch Exception ex
        (println (.getMessage ex))
        {:success false :message (.getMessage ex)}
        ))

    )

  )

;; add black list
(defn addblacklist [patientid doctorid]
  (try
    (do
      (db/createblacklist {:doctorid doctorid :patientid patientid} {:doctorid doctorid :patientid patientid})

      (resp/json {:success true})
      )
    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      ))

  )

;; chat process func begin here
(defn chatprocess [data  channel-hub-key]
  (println "111111")
;;{type chatdoctor, from 551b4cb83b83719a9aba9c01, to 551b4e1d31ad8b836c655377, content 1212}
    (let [ ctype (get data "ctype")
           ctype (if (nil? ctype )(get data (keyword "ctype"))ctype)

           from (get data "from")
           from (if (nil? from )(get data (keyword "from"))from)

           to   (get data "to")
           to   (if (nil? to )(get data (keyword "to"))to)

           content (get data "content")
           content  (if (nil? content )(get data (keyword "content"))content)

           fromtype (get data "fromtype")
           fromtype (if (nil? fromtype )(get data (keyword "fromtype"))fromtype)

           imgid (get data "imgid")
           imgid (if (nil? imgid )(get data (keyword "imgid"))imgid)

           message {:content content :fromid from :toid to
                    :msgtime (l/local-now) :isread false
                    :fromtype fromtype :type ctype
                    }
        ]
      (println "data" message)
     (try
          (do

             (let [
                 newmessage (db/create-message message)
                 messagid (:_id newmessage)
                 user (if (= fromtype 1) (:userinfo (db/get-doctor-byid  (ObjectId. from))) (db/get-patient-byid  (ObjectId. from)))
                 channel (get @channel-hub-key to)
                 channelfrom (get @channel-hub-key from)
             ]
               (println "channel" "channelfrom" channel  channelfrom)
               (when-not (nil? channel)
                (send! channel (json/write-str {:type "doctorchat" :data [(conj newmessage {:userinfo  user})]} ) false)

                 (db/update-message  {:_id messagid} {:isread true} )
               )

               (when-not (nil? channelfrom)
                 (send! channelfrom (json/write-str {:type "chatsuc" :data {:imgid imgid :toid to}} ) false)
                 ;(db/update-message  {:_id messagid} {:isread true} )
                 )


             )
            (resp/json {:success true})
            )
          (catch Exception ex
          (println (.getMessage ex))
            (resp/json {:success false :message (.getMessage ex)})
            ))

    ;;(json/write-str a :value-fn common/write-ObjectId)
    ;(resp/json [{:foo "bar"}])

    )

  )

(defn newdoctor [req]

  (try
    (let [
           formdata(:form-params req)
          ; formdata (apply merge (map #(hash-map % (get formdata %)) (keys formdata)))


           userinfodata (dissoc formdata "loc" )

           doctor (db/get-doctor-byusername (get  userinfodata "username"))

           user  {:userinfo userinfodata :isconfirmed false :loc (json/read-str (get formdata "loc")) }

           ]
      (if (nil? doctor) (resp/json {:success true :message "等待审核" :data (db/make-new-doctor user)})
        (resp/json {:success false :message "用户名已存在"})
        )

      )

    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      )

    )



  )


(defn adddoctorbyid [fromid toid channel-hub-key]
  (println 111111111111)
  (try
    (let [
           rels (db/get-relation-doctor {$or [{:doctorid fromid :rid  toid} {:doctorid toid :rid fromid}]})


           channel (get @channel-hub-key toid)


           ]
      (println 2222222222222222)

      (if (> (count rels) 0) (resp/json {:success false :message  "关系已经存在"} ) (

                  do

                    (db/makedoctorsvsdoctors {:doctorid fromid :rid toid :rtime (l/local-now)} )

                    (future (send! channel (json/write-str {:type "scanadd"  :data (conj {:fromtype 1} (db/get-doctor-byid (ObjectId. fromid)))} ) false))

                  (future (do (chatprocess {:type "doctorchat" :fromtype 1
                                :from fromid :to toid
                                :content "已添加您为医生好友!" :imgid -1} channel-hub-key)
                            (chatprocess {:type "doctorchat" :fromtype 1
                                          :from toid :to fromid
                                          :content "已添加您为医生好友!" :imgid -1} channel-hub-key)

                            ))



                    (resp/json {:success true})



                                                                                ))


      )

    (catch Exception ex
      (println (.getMessage ex))
      (resp/json {:success false :message (.getMessage ex)})
      )

    )

  )


