(ns guide-me-v1.routes.home
  (:require
   [guide-me-v1.layout :as layout]
   [guide-me-v1.db.core :as db]
   [clojure.java.io :as io]
   [guide-me-v1.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [clojure.data.json :as json]))

(defn home-page [request]
  (layout/render request "home.html"))

(defn users-page [res]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (str (json/write-str res))})

(defn login-page []
  {:status  200})

(defn save-user! [request]
  (let [{params :params} request]
    (db/create-user! params)
    {:status 201}))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/login" {:get (fn [_]
                    (login-page))}]
   ["/users" {:get (fn [_]
                      (-> (response/ok (db/get-users))
                          (users-page)))
              :post (fn [request]
                      (save-user! request))}]
   ["/docs" {:get (fn [_]
                    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]])

