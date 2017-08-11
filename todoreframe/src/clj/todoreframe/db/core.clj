(ns todoreframe.db.core
    (:require [monger.core :as mg]
              [monger.collection :as mc]
              [monger.operators :refer :all]
              [mount.core :refer [defstate]]
              [todoreframe.config :refer [env]]
              [clojure.string :as string]
              [todoreframe.layout :as layout]
              [clojure.data.json :as json]
              [monger.result :refer [acknowledged?]])
    (:import org.bson.types.ObjectId))

(def coll "Todos")
(def login "User-Details")

(defstate db*
  :start (-> env :database-url mg/connect-via-uri)
  :stop (-> db* :conn mg/disconnect))

(defstate db
  :start (:db db*))

(defn create-user-password [username password]
  (mc/insert db
             login
             {:username username
              :password password}))

(defn update-user [id first-name last-name email]
  (mc/update db "users" {:_id id}
             {$set {:first_name first-name
                    :last_name last-name
                    :email email}}))

(defn get-user [id]
  (mc/find-one-as-map db "users" {:_id id}))

(defn create-todo!
  "Creates a todo with status and the task"
  [task username]
  (dissoc (mc/insert-and-return db
                                coll
                                {:task task
                                 :status false
                                 :username username})
          :_id))

(defn display-todos!
  "Display all the remaining or done todos.Give no parameters to see all the todos"
  ([username] (map #(select-keys % [:task :status])
                   (mc/find-maps db
                                 coll
                                 {:username username}))))


(defn login?
  [username password]
  (= password (:password (mc/find-one-as-map db
                                             login
                                             {:username username}))))
