(ns kifshare.views.download
  (:require [kifshare.views.common :as common]
            [kifshare.provenance :as prov]
            [kifshare.tickets :as tickets]
            [clojure.tools.logging :as log]
            [clj-jargon.jargon :as jargon])
  (:use [noir.core :only [defpage]]
        [noir.response :only [status redirect]]
        [clojure-commons.error-codes]
        [slingshot.slingshot :only [try+]]
        [clojure.data.json :only [json-str]]
        [clojure-commons.error-codes]
        [kifshare.config :only [jargon-config]]))


(defpage "/d/:ticket-id/:filename"
  {:keys [ticket-id filename]}
  (try+
    (jargon/with-jargon (jargon-config) [cm]
      (let [ticket-info (tickets/ticket-info cm ticket-id)]
        (tickets/download cm ticket-id)))
    (catch error? err
      (log/error (format-exception (:throwable &throw-context)))
      (status 500 (json-str err)))
    (catch Exception e
      (log/error (format-exception (:throwable &throw-context)))
      (status 500 (json-str (unchecked &throw-context))))))

(defpage "/d/:ticket-id"
  {:keys [ticket-id]}
  (jargon/with-jargon (jargon-config) [cm]
    (let [ticket-info (tickets/ticket-info cm ticket-id)]
      (redirect (str "/d/" ticket-id "/" (:filename ticket-info))))))

