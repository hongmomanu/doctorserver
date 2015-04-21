(ns doctorserver.routes.home
  (:require [doctorserver.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.response :as resp]
            [clojure.java.io :as io]
            [noir.io :as nio]
            [clj-time.coerce :as c]
            [clj-time.local :as l]
            [noir.response :as nresp]
            [doctorserver.public.common :as commonfunc]
            [ring.util.response :refer [file-response]]

            ))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))

    (GET "/downloadtest/*" [] (resp/redirect "/cordova-app-hello-world-3.6.3.tar.gz"))


  (GET "/about" [] (about-page))



  (GET "/files/:filename" [filename]

    (file-response (str schema/commonfunc "upload/" filename))

    )

  (POST "/common/uploadfile"  [file ]


    ;(println "file up loaddd")

    ;(println file)

    (let [
          uploadpath  (str commonfunc/datapath "upload/")
          timenow (c/to-long  (l/local-now))
          filename (str timenow (:filename file))
          ]
      ;(println filename)
      (nio/upload-file uploadpath  (conj file {:filename filename}))
      (nresp/json {:success true :filename (:filename file)})
      )

    )


  )
