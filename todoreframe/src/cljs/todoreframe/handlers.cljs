(ns todoreframe.handlers
  (:require [todoreframe.db :as db]
            [re-frame.core :refer [dispatch reg-event-db]]))

(reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :page page)))

(reg-event-db
 :set-docs
 (fn [db [_ docs]]
   (assoc db :docs docs)))

(reg-event-db
 :current-user
 (fn [db [_ user]]
   (assoc db :current-user user)))

(reg-event-db
 :tasks
 (fn [db [_ tasks]]
   (assoc db :tasks tasks)))
