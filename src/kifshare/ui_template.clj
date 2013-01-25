(ns kifshare.ui-template
  (:use hiccup.core
        [hiccup.page :only [include-css include-js html5]]
        [kifshare.common :only [layout]])
  (:require [kifshare.config :as cfg]
            [clostache.parser :as prs]
            [clojure.tools.logging :as log]
            [cheshire.core :as json])
  (:import [org.apache.commons.io FileUtils]))

(defn clear
  []
  (log/debug "entered kifshare.ui-template/clear")
  (html [:div {:class "clear"}]))

(defn section-spacer [] (html [:div {:class "section-spacer"}]))

(defn irods-avu-row
  [mmap]
  (log/debug "entered kifshare.ui-template/irods-avu-row")
  
  (html
   [:tr 
    [:td (:attr mmap)] 
    [:td (:value mmap)] 
    [:td (:unit mmap)]]))

(defn irods-avu-table
  [metadata]
  (log/debug "entered kifshare.ui-template/irods-avu-table")
  
  (if (pos? (count metadata))
    (html
     [:div {:id "irods-avus"} 
      [:div {:id "irods-avus-header"}
       [:h2 "Metadata"]]
      [:table {:id "irods-avus-data"}
       [:thead
        [:tr 
         [:th "Attribute"] 
         [:th "Value"] 
         [:th "Unit"]]]
       [:tbody
        (map irods-avu-row metadata)]]
      (section-spacer)])))

(defn lastmod
  [ticket-info]
  (log/debug "entered kifshare.ui-template/lastmod")
  
  (html
   [:div {:id "lastmod-detail"}
    [:div {:id "lastmod-label"} 
     [:p "Last Modified:"]]
    [:div {:id "lastmod"} 
     [:p (:lastmod ticket-info)]]]))

(defn filesize
  [ticket-info]
  (log/debug "entered kifshare.ui-template/filesize")
  
  (html
   [:div {:id "size-detail"}
    [:div {:id "size-label"} 
     [:p "File Size:"]]
    [:div {:id "size"} 
     [:p (FileUtils/byteCountToDisplaySize 
          (Long/parseLong (:filesize ticket-info)))]]]))

(defn ui-ticket-info
  [ticket-info]
  (assoc ticket-info
    :wget_template (cfg/wget-flags)
    :curl_template (cfg/curl-flags)
    :iget_template (cfg/iget-flags)))

(defn template-map
  [ticket-info]
  (log/debug "entered kifshare.ui-template/template-map")
  (html
   [:span {:id "ticket-info" :style "display: none;"}
    [:div {:id "ticket-info-map"}
     (json/generate-string
      (ui-ticket-info ticket-info))]]))

(defn input-display
  [id]
  (log/debug "entered kifshare.ui-template/input-display")
  
  (html
   [:div {:id id}]
   #_[:input
      {:id id
       :type "text"
       :size 70
       :maxlength 500
       :readonly false
       :value ""}]))

(defn irods-instr
  [ticket-info]
  (log/debug "entered kifshare.ui-template/irods-instr")
  
  (html
   [:div {:id "irods-instructions"}
    [:div {:id "irods-instructions-label"} 
     [:h2 "iRODS icommands"]]
    
    [:div {:id "clippy-irods-instructions"}
     (input-display "irods-command-line")
     [:span {:title "copy to clipboard"}
      [:button {:id "clippy-irods-wrapper"
                :class "clippy-irods"
                :title "Copy"}
       "Copy"]]]]))

(defn downloader-instr
  [ticket-id ticket-info]
  (log/debug "entered kifshare.ui-template/downloader-instr")
  
  (html
   [:div {:id "wget-instructions"}
    [:div {:id "wget-instructions-label"} 
     [:p "Wget"]]
    [:div {:id "clippy-wget-instructions"}
     (input-display "wget-command-line")
     [:span  {:title "copy to clipboard"}
      [:button {:id "clippy-wget-wrapper"
                :class "clippy-wget"
                :title "Copy"}
       "Copy"]]]]

   [:div {:id "curl-instructions"}
    [:div {:id "curl-instructions-label"} 
     [:p "cURL"]]
    [:div {:id "clippy-curl-instructions"}
     (input-display "curl-command-line")
     [:span {:title "copy to clipboard"}
      [:button {:id "clippy-curl-wrapper"
                :class "clippy-curl"
                :title "Copy"}
       "Copy"]]]]))

(defn menu
  [ticket-info]
  (html
   [:div {:id "menu"}
    [:ul
     [:li [:div {:id "logo-container"}
           [:img {:id "logo" :src "../img/powered_by_iplant_logo.png"}]]]
     [:li [:div [:h1 {:id "filename"} (:filename ticket-info)]]]
     [:li [:div {:id "download-container"}
           [:a {:href (str "d/" (:ticket-id ticket-info) "/" (:filename ticket-info))
                :id "download-link"}
            [:div {:id "download-link-area"}
             "Download!"]]]]]]))

(defn details
  [ticket-info]
  [:div {:id "details"}
   [:a {:name "details-section"}]
   [:div {:id "details-header"}
    [:h2 "File Details"]]
   (lastmod ticket-info)
   (filesize ticket-info)
   (section-spacer)])

(defn alt-downloads
  [ticket-info]
  (log/debug "entered kifshare.ui-template/alt-downloads")
  
  (html
   [:div {:id "alt-downloads-header"} 
    [:h2 "Downloading via Command-Line"]]
   [:div {:id "alt-downloads"}
    (irods-instr ticket-info)
    (downloader-instr (:ticket-id ticket-info) ticket-info)
    (section-spacer)]))

(defn footer
  []
  (html
   [:div {:id "footer"}
    [:p "The iPlant Collaborative is funded by a grant from the National Science Foundation Plant Science Cyberinfrastructure Collaborative (#DBI-0735191)."]]))

(defn landing-page
  [ticket-id metadata ticket-info]
  (log/debug "entered kifshare.ui-template/landing-page")
  
  (html
   [:head
    [:title (:filename ticket-info)]
    (map include-css (cfg/css-files))
    (map include-js (cfg/javascript-files))]
   [:body
    (template-map ticket-info)
    (menu ticket-info)
    [:div#wrapper {:id "page-wrapper" :class "container_12"}
     (details ticket-info)
     (alt-downloads ticket-info)
     (irods-avu-table metadata)
     (footer)]]))
