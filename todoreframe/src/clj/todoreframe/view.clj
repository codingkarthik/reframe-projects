(ns todoreframe.view
  (:require [todoreframe.db.core :as db]
            [todoreframe.layout :as layout])
  (:import org.bson.types.ObjectId))


(defn create-todo
  [task username]
  (if (db/create-todo! task username)
    (layout/render-json (db/display-todos! username))
    false))


(defn login?
  [username password]
  (layout/render-json
   {:status (db/login? username password)
    :username username}))
