(ns todoreframe.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [todoreframe.ajax :refer [load-interceptors!]]
            [todoreframe.handlers]
            [todoreframe.subscriptions])
  (:import goog.History))

(def server "http://localhost:3000/")

(defn log [& params] (.log js/console (apply str params)))

(defn get-by-id
  [id]
  (.getElementById js/document id))

(defn error-handler
  []
  (log "Error Handler"))

(defn show-output
  [response]
  (rf/dispatch [:tasks response])
  (str "Show output" @(rf/subscribe [:tasks])))

(defn show-tasks
  []
  (log "#23232323" @(rf/subscribe [:tasks]))
  [:div
   [:ul
    (doall (map (fn [a]
                  [:li (:task a)])
                @(rf/subscribe [:tasks])))]])

(defn logout
  []
  (do
    (js/alert "Logged out Succesfully")
    (rf/dispatch [:current-user ""])
    (secretary/dispatch! "/login")))

(defn show-output-login
  [response]
  (if (:status response)
    (do
      #_(js/alert (str response))
      #_(reset! current-page "home-page")
      #_(reset! current-user (:username response))
      (rf/dispatch [:current-user (:username response)] )
      (js/alert (str response "@@@" ))
      (secretary/dispatch! "/todo"))
    (do
      (js/alert "Better luck next time"))))



(defn nav-link [uri title page collapsed?]
  (let [selected-page (rf/subscribe [:page])]
    [:li.nav-item
     {:class (when (= page @selected-page) "active")}
     [:a.nav-link
      {:href uri
       :on-click #(reset! collapsed? true)} title]]))

(defn navbar []
  (r/with-let [collapsed? (r/atom true)]
    [:nav.navbar.navbar-dark.bg-primary
     [:button.navbar-toggler.hidden-sm-up
      {:on-click #(swap! collapsed? not)} "☰"]
     [:div.collapse.navbar-toggleable-xs
      (when-not @collapsed? {:class "in"})
      [:a.navbar-brand {:href "#/"} "todoreframe"]
      [:ul.nav.navbar-nav
       [nav-link "#/" "Home" :home collapsed?]
       [nav-link "#/about" "About" :about collapsed?]
       [nav-link "#/login" "Login" :login collapsed?]]]]))

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src (str js/context "/img/warning_clojure.png")}]]]])

(defn home-page
  []
  [:div.container
   [:h2 "TODO LIST"]
   [:form {:action "#"
           :on-submit (fn [e]
                        (let [s1 (.-value (get-by-id "input-box"))]
                          (log s1)
                          (GET (str server "todo")
                               {:params {:task s1
                                         :username @(rf/subscribe [:current-user])}
                                :format :json
                                :response-format :json
                                :keywords? true
                                :handler show-output
                                :error-handler error-handler})))}
    [:div [:input {:type "text"
                   :id "input-box"}]
     [:div.row [:input {:class "input1"
                        :type "submit"
                        :value "Submit"}]]]
    [:input {:type "button"
             :class "logout"
             :value "Log out"
             :onClick logout}]]
   [show-tasks]])

(defn login-page
  []
  [:div.container
   [:h2 "Login Page"]
   [:form {:action "#"
           :on-submit (fn [e]
                        (let [username (.-value (get-by-id "username"))
                              password (.-value (get-by-id "password"))]
                          #_ (log (str username password))
                          (GET (str server "login")
                               {:params {:username username
                                         :password password}
                                :format :json
                                :response-format :json
                                :keywords? true
                                :handler show-output-login
                                :error-handler error-handler})))}
    [:div
     [:div.row
      [:input {:type "text"
               :id "username"
               :placeholder "Username"
               :class "username"}]]
     [:div.row
      [:input {:placeholder "Password"
               :type "password"
               :id "password"
               :class "password"}]]
     [:input {:type "submit"
              :value "Login"
              :class "login"}]]]])

(def pages
  {:home #'home-page
   :about #'about-page
   :login #'login-page})

(defn page []
  [:div
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :home]))

(secretary/defroute "/login" []
  (rf/dispatch [:set-active-page :login]))



(secretary/defroute "/about" []
  (rf/dispatch [:set-active-page :about]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(rf/dispatch [:set-docs %])}))

(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
