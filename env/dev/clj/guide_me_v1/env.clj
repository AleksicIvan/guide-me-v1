(ns guide-me-v1.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [guide-me-v1.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[guide-me-v1 started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[guide-me-v1 has shut down successfully]=-"))
   :middleware wrap-dev})
