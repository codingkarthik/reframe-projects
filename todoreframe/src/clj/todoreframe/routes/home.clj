(ns todoreframe.routes.home
  (:require [todoreframe.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [todoreframe.db.core :as db]
            [todoreframe.view :as views]))

(defn home-page []
  (layout/render "home.html"))



(defroutes home-routes
  (GET "/" []
       (home-page))
  (GET "/todo"
       [task username]
       (views/create-todo task username))
  (GET "/login"
       [username password]
       (views/login? username password)))
