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

(defn input-text [input-value type]
  [:input.input {:type type
           :value @input-value
           :on-change #(reset! input-value (-> % .-target .-value))}])

(defn input-button [handleSigninClick data label disabled]
  [:button.button.is-link {:disabled disabled
                           :on-click #(handleSigninClick data)} label])


