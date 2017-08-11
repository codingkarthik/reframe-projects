(ns todoreframe.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [todoreframe.core-test]))

(doo-tests 'todoreframe.core-test)

