(ns guide-me-v1.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [guide-me-v1.core-test]))

(doo-tests 'guide-me-v1.core-test)

