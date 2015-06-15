(ns doctorserver.controller.pay
  (:use compojure.core)
  (:require [doctorserver.db.core :as db]
            ;[doctorserver.public.common :as common]
            [noir.response :as resp]
            [clojure.data.json :as json]
            [monger.json]
            [clj-http.client :as client]
            [doctorserver.layout :as layout]
            )
  (:import [org.bson.types ObjectId]
           [com.chinaums.pay.api.entities OrderEntity QueryEntity]
           [com.chinaums.pay.api.impl DefaultSecurityService UMSPayServiceImpl]
           [java.text SimpleDateFormat]
           [java.util Date Locale]
           )
  )



(def creatOrderUrl (str "https://116.228.21.162:8603/merFrontMgr/orderBusinessServlet"))

(def queryOrderUrl (str "https://116.228.21.162:8603/merFrontMgr/orderQueryServlet"))

(def payOrderUrl (str "http://116.228.21.162:9127/umsFrontWebQmjf/umspay"))

(def verifyKeyMod (str "cff6f75dfb7b3f32aca8c81442d142512684ad55372bf965512e337d47f785fb0e247f11d91f0c2517ebf3a4d456693c6a994eb39b3456102889818fd26f3732e3595e4f22ba3f4e0e77969d25a793d0eb00d011e7982d57f663a81463a0efce5ccdf8dc4534e70bdbfe2e961ab9edfcb373c72b6343400c838ecb4347c88911"))
(def verifyKeyExp (str "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010001"))
(def merId (str "898000093990001"))

(def mchantUserCode (str "13250000000961278"))

(def merTermId (str "99999999"))

(def signKeyMod (str "83beb97d3aa44b696b2e1633d6d6fe5ec2b86d2d8ba8437c5c4bcac0530b7d50f03af18dee28f7ebd8859d7063254f3751c1c3594a6146e430ea442489b8fb46dc38c34f42241b0783044b459ce8b377006bc7b1a3b58f41ad772ff65846f4946e9d68e1d78564f89b70b2c713c0e6efbb03100e317eb3214d9ed072fbee3a07"))
(def signKeyExp (str "1e4c5e9c4e403a97a3ee956c969c1b23efe43a379f46b33e867b67c59353b11e4c21422c41f96a0af360c7347198c2ff15ee59decf1c50116aae75bd716ef95a9dffd055bc872dc840a53f1d8fdbf08430efa394f8fe7ffc708ccbf4b9d46f6c833a415e57abd811d4b2b1aee64f59e1b87a74986fc7bd04514f924b5550a901"))
(def merNoticeUrl (str ""))
(def respUrl (str "http://192.168.2.100:3000/"))
(def bankName (str "huaxia"))
(def cardType (str "d"))

(declare testCreateOrder)
(defn makeunionpay [money]
    (let [

         my-cs (clj-http.cookies/cookie-store)
        orderobj (testCreateOrder money)
         params (map #(conj {} {:name  (name %) :value (get orderobj %)}) (keys orderobj))
        ]
      ;(println "hahaha" orderobj)
      (println params)
      #_(client/post "http://192.168.2.100:3000/pay/test" {:form-params {:name "jack"}
                                                          :force-redirects true
                                                        ;:follow-redirects true
                                                        ;:as :auto
                                                         :cookie-store my-cs
                                                        })
      #_(println (client/post payOrderUrl {:form-params orderobj
                                ;:force-redirects false
                                 ;:follow-redirects true
                                :cookie-store my-cs
                                ;:as :auto
                             ;:socket-timeout 5000
                             ;:conn-timeout 5000
                                }))

      (layout/render "proxyauto.html"
        {:content {:params params :method "post" :loginurl payOrderUrl}
         })




    )
)

(defn testCreateOrder [money]
  (let [ss (new DefaultSecurityService)
        service (new UMSPayServiceImpl)
        sf (new SimpleDateFormat "yyyyMMddHHmmss" Locale/US)
        curreTime (.format sf (new Date))
        order (new OrderEntity)
        respOrder (new OrderEntity)
        ]

    (.setSignKeyModHex ss signKeyMod);// 签名私钥 Mod
    (.setSignKeyExpHex ss signKeyExp);// 签名私钥 Exp
    (.setVerifyKeyExpHex ss verifyKeyExp)
    (.setVerifyKeyModHex ss verifyKeyMod)
    (.setSecurityService service ss)
    (.setOrderServiceURL service creatOrderUrl)

    (.setMerId order merId);// 商户号
    (.setMerTermId order merTermId);// 终端号
    (.setMerOrderId order curreTime);// 订单号,商户根据自己的规则生成,最长 32 位
    (.setOrderDate order (.substring curreTime 0 8));// 订单日期
    (.setOrderTime order (.substring curreTime 8));// 订单时间
    (.setTransAmt order money);// 订单金额(单位分)
    (.setOrderDesc order "e医通充值");// 订单描述
    (.setNotifyUrl order merNoticeUrl);// 通知商户地址,保证外网能够访问
    (.setTransType order "NoticePay");// 固定值
    (.setEffectiveTime order "0");// 订单有效期期限(秒),值小于等于 0 表示订单长期有效
    (.setMerSign order (.sign ss (.buildSignString order)));
    (println (str "下单请求数据:"  order));

    (try
      (do
        (let [
               respOrder (.createOrder service order)
               ]

          {:merSign (.sign ss(str (.getTransId respOrder) (.getChrCode respOrder)))
           :chrCode   (.getChrCode respOrder)
           :tranId (.getTransId respOrder)
           :url respUrl
           :mchantUserCode mchantUserCode
           ;:bankName bankName
           ;:cardType cardType
           ;:success true
                      }
          )

        )
      (catch Exception ex
        (println (.getMessage ex))
        {:success false}

        ))

    )
  )







