(ns guide-me-v1.validation
  (:require [struct.core :as st]))

(def user-schema
  [[:first_name st/required st/string]
   [:last_name st/required st/string]
   [:email st/required st/email]
   [:pass st/required st/string]])

(def login-data-schema
   [[:email st/required st/email]
    [:pass st/required st/string]])


(defn is-valid-user? [user]
  (st/valid? user user-schema))

(defn is-valid-login? [login-data]
  (st/valid? login-data login-data-schema))

