(ns user
  (:require [mount.core :as mount]
            [todoreframe.figwheel :refer [start-fw stop-fw cljs]]
            todoreframe.core))

(defn start []
  (mount/start-without #'todoreframe.core/repl-server))

(defn stop []
  (mount/stop-except #'todoreframe.core/repl-server))

(defn restart []
  (stop)
  (start))


