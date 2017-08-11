(ns todoreframe.subscriptions
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :page
  (fn [db _]
    (:page db)))

(reg-sub
 :docs
 (fn [db _]
   (:docs db)))

(reg-sub
 :current-user
 (fn [db _]
   (:current-user db)))

(reg-sub
 :tasks
 (fn [db _]
   (:tasks db)))
