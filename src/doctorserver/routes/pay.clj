(ns doctorserver.routes.pay
  (:require [doctorserver.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.java.io :as io]
            [ring.util.response :as resp]
            [noir.response :as nresp]
            [doctorserver.public.websocket :as websocket]
            [doctorserver.controller.pay :as pay]
            ))



(defroutes pay-routes

  (GET "/pay/unionpay" [money patientid] (pay/makeunionpay money patientid))

  (POST "/pay/test" [name](do
                            (println name)
                            (resp/redirect "/")

                            ))
  (GET "/pay/test" [name](
                           nresp/json {:success true}

                            ))

  (GET "/pay/payfinish" [respCode amount patientid] (do
                              (pay/payfinish respCode amount  patientid websocket/channel-hub-key)
                                                      ;(println respCode amount patientid)


                              (nresp/json {:success true})
                              ))

 )
