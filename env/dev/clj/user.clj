(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require
   [guide-me-v1.config :refer [env]]
    [clojure.pprint]
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [mount.core :as mount]
    [guide-me-v1.figwheel :refer [start-fw stop-fw cljs]]
    [guide-me-v1.core :refer [start-app]]
    [guide-me-v1.db.core]
    [conman.core :as conman]
    [luminus-migrations.core :as migrations]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(add-tap (bound-fn* clojure.pprint/pprint))

(defn start
  "Starts application.
  You'll usually want to run this on startup."
  []
  (mount/start-without #'guide-me-v1.core/repl-server))

(defn stop
  "Stops application."
  []
  (mount/stop-except #'guide-me-v1.core/repl-server))

(defn restart
  "Restarts application."
  []
  (stop)
  (start))

(defn restart-db
  "Restarts database."
  []
  (mount/stop #'guide-me-v1.db.core/*db*)
  (mount/start #'guide-me-v1.db.core/*db*)
  (binding [*ns* 'guide-me-v1.db.core]
    (conman/bind-connection guide-me-v1.db.core/*db* "sql/queries.sql")))

(defn reset-db
  "Resets database."
  []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate
  "Migrates database up for all outstanding migrations."
  []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback
  "Rollback latest database migration."
  []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration
  "Create a new up and down migration file with a generated timestamp and `name`."
  [name]
  (migrations/create name (select-keys env [:database-url])))


