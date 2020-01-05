(ns guide-me-v1.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[guide-me-v1 started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[guide-me-v1 has shut down successfully]=-"))
   :middleware identity})
