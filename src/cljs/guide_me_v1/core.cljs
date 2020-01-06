(ns guide-me-v1.core
  (:require
   [reagent.core :as r]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [markdown.core :refer [md->html]]
   [guide-me-v1.ajax :as ajax]
   [ajax.core :refer [GET POST]]
   [reitit.core :as reitit]
   [clojure.string :as string]
   [guide-me-v1.components.input :refer [input-text input-button]]
   [guide-me-v1.validation :refer [is-valid-user? is-valid-login?]])
  (:import goog.History))

(defn fetch-users! [users-state]
  (GET "/users"
    {:handler #(reset! users-state %)}))

(defn post-user! [data]
   (POST "/users"
     {:format :json
      :params data
      :headers {"Accept" "application/transit+json"
                "x-csrf-token" (goog.object/get js/window "csrfToken")}
      :error-handler (fn [r] (prn r))}))

(defonce session (r/atom {:page :home}))

(defn nav-link [uri title page expanded]
  [:a.navbar-item
   {:href   uri
    :on-click #(swap! expanded not)
    :class (when (= page (:page @session)) "is-active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}} "guide-me-v1"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click #(swap! expanded? not)
        :class (when @expanded? :is-active)}
       [:span] [:span] [:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link "#/" "Home" :home expanded?]
       [nav-link "#/login" "Login" :login expanded?]
       [nav-link "#/signup" "Signup" :signup expanded?]
       [nav-link "#/users" "Users" :users expanded?]
       [nav-link "#/about" "About" :about expanded?]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(defn json-to-clj [json]
  (js->clj (.parse js/JSON json) :keywordize-keys true))


(defn set-hash! [loc]
  (set! (.-hash js/window.location) loc))

(defn handleSigninClick [data]
  (post-user! data)
  (set-hash! "/users"))

(defn login-page []
  (let [password (r/atom "")
        email (r/atom "")]
    (fn []
      [:section.section>div.container>div.content
       [:div.columns.is-mobile.is-centered
        [:div.columns.is-half
         [:fieldset
          [:div.field
           [:div.control.has-icons-left.has-icons-right
            [:label.label "email"]
            [input-text email "email"]]]
          [:div.field
           [:label.label "password"]
           [input-text password "password"]]
          [:div.field
           [:p.control
            [input-button
             handleSigninClick
             {:email @email :pass @password}
             "Login"
             (not (is-valid-login? {:email @email :pass @password}))]]]]]]])))

(defn signup-page []
  (let [firstname (r/atom "")
        lastname (r/atom "")
        password (r/atom "")
        email (r/atom "")]
    (fn []
      [:section.section>div.container>div.content
       [:div.columns.is-mobile.is-centered
        [:div.columns.is-half
          [:fieldset
            [:div.field
            [:label.label "first name"]
            [:div.control
              [input-text firstname "text"]]]
            [:div.field
            [:label.label "last name"]
            [input-text lastname "text"]]
            [:div.field
            [:div.control.has-icons-left.has-icons-right
              [:label.label "email"]
              [input-text email "email"]]]
            [:div.field
            [:label.label "password"]
            [input-text password "password"]]
            [:div.field
            [:p.control
              [input-button handleSigninClick
               {:first_name @firstname :last_name @lastname :email @email :pass @password}
               "Sign up"
               (not (is-valid-user? {:first_name @firstname 
                                    :last_name @lastname 
                                    :email @email 
                                    :pass @password}))
               ]]]]]]])))

(defn users-page []
  (let [users (r/atom {})]
    (fetch-users! users)
    (fn []
      [:div
       (map (fn [u] 
          [:div.card {:key (:id u)}
            [:div.card-content
            [:div.content (:first_name u) ]]]) (:body (json-to-clj @users)))])))

(defn home-page []
  [:section.section>div.container>div.content
   (when-let [docs (:docs @session)]
     [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])])

(def pages
  {:home #'home-page
   :about #'about-page
   :users #'users-page
   :signup #'signup-page
   :login #'login-page})

(defn page []
  [(pages (:page @session))])


;; -------------------------
;; Routes

(def router
  (reitit/router
    [["/" :home]
     ["/signup" :signup]
     ["/users" :users]
     ["/login" :login]
     ["/about" :about]]))

(defn match-route [uri]
  (->> (or (not-empty (string/replace uri #"^.*#" "")) "/")
       (reitit/match-by-path router)
       :data
       :name))
;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (swap! session assoc :page (match-route (.-token event)))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(swap! session assoc :docs %)}))

(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (ajax/load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
