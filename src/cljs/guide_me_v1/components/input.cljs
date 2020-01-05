(ns guide-me-v1.components.input
  (:require
   [reagent.core :as r]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [markdown.core :refer [md->html]]
   [guide-me-v1.ajax :as ajax]
   [ajax.core :refer [GET POST]]
   [reitit.core :as reitit]
   [clojure.string :as string])
  (:import goog.History))

(defn input-text [input-value]
  [:input.input {:type "text"
           :value @input-value
           :on-change #(reset! input-value (-> % .-target .-value))}])

(defn input-button [handleSigninClick data]
  [:button.button.is-link {:disabled (let [{:keys [first_name last_name email pass]} data] 
                                       (or (empty? first_name) 
                                           (empty? last_name) 
                                           (empty? email) 
                                           (empty? pass)))
                           :on-click #(handleSigninClick data)} "Login"])


