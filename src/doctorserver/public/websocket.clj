(ns doctorserver.public.websocket
      (:use org.httpkit.server)
  (:require
            [clojure.data.json :as json]
            [doctorserver.controller.doctor :as doctor]
            [doctorserver.controller.patient :as patient]
            )
)

(def channel-hub (atom {}))
(def channel-hub-key (atom {}))

(defn handler [request]
  (with-channel request channel
    ;; Store the channel somewhere, and use it to sent response to client when interesting event happened
    ;;(swap! channel-hub assoc channel nil)
    (on-receive channel (fn [data]
                            (let [cdata  (json/read-str data)
                                  type    (get cdata "type")
                                  content (get cdata "content")
                            ]
                            (cond (= "doctorconnect" type) (do
                                                        (swap! channel-hub assoc channel content )
                                                        (swap! channel-hub-key assoc content channel )
                                                        (doctor/getnoread content 1 channel-hub-key)
                                                        (doctor/getquickapplying content channel-hub-key)
                                                        )
                                (= "patientconnect" type)(
                                                           do
                                                           (swap! channel-hub assoc channel content )
                                                           (swap! channel-hub-key assoc content channel )
                                                           (println content)
                                                           (doctor/getnoread content 0 channel-hub-key)
                                                           (patient/getquickapplying content channel-hub-key)
                                                           (patient/getquickaccept content channel-hub-key)

                                                           )

                              (= "doctorchat" type)(do
                                                     (doctor/chatprocess cdata channel-hub-key)
                                                     )

                                 :else (doctor/chatprocess cdata channel-hub-key))
                               (println "mumumu" channel)


                            )

                               ;(println request)
                              ;(send! channel data)
                              ))
    (on-close channel (fn [status]
                        ;; remove from hub when channel get closed
                        (let [chanel-key (get @channel-hub channel)]


                        (println channel " disconnected. status: " status " channel-key" chanel-key)
                        (swap! channel-hub dissoc channel)
                        (swap! channel-hub-key dissoc chanel-key)
                        )


                        ))))




(defn start-server [port]
  (run-server handler {:port port})
  )


;(future (loop []
;          (println (keys @channel-hub))
;          (doseq [channel (keys @channel-hub)]
;            (println "ok")
;            (send! channel (json/write-str
;                                  {:happiness (rand 10)})
;              false)
;            )
;          (Thread/sleep 5000)
;          (recur)))







