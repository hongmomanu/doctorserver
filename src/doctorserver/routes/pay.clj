(ns doctorserver.routes.pay
  (:require [doctorserver.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.java.io :as io]
            [ring.util.response :as resp]
            [noir.response :as nresp]
            [doctorserver.controller.pay :as pay]
            ))



(defroutes pay-routes

  (GET "/pay/unionpay" [money] (pay/makeunionpay money))

  (POST "/pay/test" [name](do
                            (println name)
                            (resp/redirect "/")

                            ))
  (GET "/pay/test" [name](
                           nresp/json {:success true}

                            ))

 )
