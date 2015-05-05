(ns doctorserver.routes.home
  (:use clj.qrgen)
  (:require [doctorserver.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :as resp]
            [clojure.java.io :as io]
            [noir.io :as nio]

            [clj-time.coerce :as c]
            [clj-time.local :as l]
            [noir.response :as nresp]
            [org.httpkit.client :as http]
            [doctorserver.public.common :as commonfunc]
            [ring.util.response :refer [file-response]]

            ))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" req (do (println req) (home-page)) )

    (GET "/downloadtest/*" [] (resp/redirect "/cordova-app-hello-world-3.6.3.tar.gz"))


  (GET "/about" [] (about-page))



  (GET "/files/:filename" [filename]

    (file-response (str commonfunc/datapath "upload/" filename))

    )



(GET "/common/makeqrcode" [content]


  (let [
         timenow (c/to-long  (l/local-now))
         uploadpath  (str commonfunc/datapath "upload/")
         filename (str  timenow "QRCode.png")
         ]
    ;;(println filename)

    (io/copy (io/file (from content)) (io/file (str uploadpath filename)))
    (resp/redirect (str "/files/" filename))

    )
  )

  (GET "/common/geturlbywap" [url]
    (println 11)
    (http/get "http://wap.0575fy.com/"

      {:timeout 200             ; ms

       :user-agent "Mozilla/5.0 (Linux; Android 4.2.2; LGL22 Build/JDQ39B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.109 Mobile Safari/537.36"
       :headers {"user-agent" "Mozilla/5.0 (Linux; Android 4.2.2; LGL22 Build/JDQ39B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.109 Mobile Safari/537.36"}
       }


      (fn [{:keys [status headers body error]}] ;; asynchronous response handling
        (if error
          (println "Failed, exception is " error)
          (do (println "Async HTTP GET: " status) body )))
      )

    )

  (POST "/common/uploadfile"  [file ]

    (let [
          uploadpath  (str commonfunc/datapath "upload/")
          timenow (c/to-long  (l/local-now))
          filename (str timenow (:filename file))
          ]
      ;(println filename)
      (nio/upload-file uploadpath  (conj file {:filename filename}))
      (nresp/json {:success true :filename filename})
      )

    )


  )
