(ns ^:figwheel-no-load todoreframe.app
  (:require [todoreframe.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
