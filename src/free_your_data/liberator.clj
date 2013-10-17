(ns free-your-data.liberator
  (:require [clojure.pprint :refer [pprint]]
            [free-your-data.data :refer (find-talk update-talk)]
            [ring.adapter.jetty :refer (run-jetty)]
            [ring.middleware.params :refer (wrap-params)]
            [liberator.core :refer (resource defresource log!)]
            [liberator.dev :refer (wrap-trace)]
            [clojure.string :refer (join)]
            [clojure.edn :as edn]
            [clojure.java.io :refer (reader)])
  (:import java.util.TimeZone
           java.text.SimpleDateFormat
           java.util.Locale
           java.util.Date
           java.io.PushbackReader))

















;;; 1

;; create a ring handler and bind to var "event"

(def event (liberator.core/resource))


;; start a jetty instance for it, parsing request
;; parameters will be handy later

(def app
  (-> #'event
      (wrap-trace :ui)
      (wrap-params)))

(defonce jetty (run-jetty #'app {:port 4000 :join? false}))


























;;; 2

;; we'd better tell which media types we offer

(def event
  (resource :available-media-types ["text/html"]))


























;;; 3

;; or a litle bit more compact

(defresource event
  :available-media-types ["text/html"])




























;;; 4

;; add some body text as a constant

(defresource event
  :available-media-types ["text/html"]
  :handle-ok "Here comes the event")

























;;; 5

;; ...or as a function

(defresource event
  :available-media-types ["text/html"]
  :handle-ok (fn [_] "Here comes the event"))

























;;; 6

;; now for some real data

(defresource event
  :available-media-types ["text/html"]
  :handle-ok (fn [context]
               (find-talk
                (get-in context [:request :params "id"]))))

;; http://localhost:4000/?id=10
;; http://localhost:4000/?id=100


















;;; 7

;; an existential questions

(defresource event
  :available-media-types ["text/html"]
  :exists? (fn [context]
             (find-talk
              (get-in context [:request :params "id"])))
  :handle-ok (fn [context]
               (find-talk
                (get-in context [:request :params "id"]))))

;; http://localhost:4000/?id=100


















;;; 8

;; DRY!

(defresource event
  :available-media-types ["text/html"]
  :exists? (fn [context]
             (if-let [talk
                      (find-talk
                       (get-in context
                               [:request :params "id"]))]
               [true (assoc context ::talk talk)]
               [false context]))
  :handle-ok (fn [context]
               (::talk context)))

;; http://localhost:4000/?id=10
;; http://localhost:4000/?id=100















;;; 8b

;; Simplify!

(defresource event
  :available-media-types ["text/html"]
  :exists? (fn [context]
             (if-let [talk
                      (find-talk
                       (get-in context
                               [:request :params "id"]))]
               (assoc context ::talk talk)
               nil))
  :handle-ok (fn [context]
               (::talk context)))

;; http://localhost:4000/?id=10
;; http://localhost:4000/?id=100
















;;; 8c

;; Simplify even more

(defresource event
  :available-media-types ["text/html"]
  :exists? (fn [context]
             (if-let [talk
                      (find-talk
                       (get-in context
                               [:request :params "id"]))]
               (assoc context ::talk talk)))
  :handle-ok (fn [context]
               (::talk context)))

;; http://localhost:4000/?id=10
;; http://localhost:4000/?id=100
















;;; 9

;; Map will be merged with context

(defresource event
  :available-media-types ["text/html"]
  :exists? (fn [context]
             (if-let [talk
                      (find-talk
                       (get-in context
                               [:request :params "id"]))]
               {::talk talk}))
  :handle-ok (fn [context]
               (::talk context)))















;;; 10

;; A keyword is a function

(defresource event
  :available-media-types ["text/html"]
  :exists? (fn [context]
             (if-let [talk
                      (find-talk
                       (get-in context
                               [:request :params "id"]))]
               {::talk talk}))
  :handle-ok ::talk)

















;;; 11

;; ...destructuring

(defresource event
  :available-media-types ["text/html"]
  :exists? (fn [{{{id "id"} :params} :request}]
             (if-let [talk (find-talk id)]
               {::talk talk}))
  :handle-ok ::talk)




















;;; 12

;; more media types?

(defresource event
  :available-media-types ["text/html"
                          "application/json"
                          "application/edn"]
  :exists? (fn [{{{id "id"} :params} :request}]
             (if-let [talk (find-talk id)]
               {::talk talk}))
  :handle-ok ::talk)

;; > curl -HAccept:application/json -i 'http://localhost:4000/?id=10'
;; > curl -HAccept:application/edn -i 'http://localhost:4000/?id=10'
;; > curl -HAccept:application/edn -i 'http://localhost:4000/?id=400'
;; > curl -HAccept:image/json -i 'http://localhost:4000/?id=10'














;;; 13

;; more handlers

(defresource event
  :available-media-types ["text/html"
                          "application/json"
                          "application/edn"]
  :exists? (fn [{{{id "id"} :params} :request}]
             (if-let [talk (find-talk id)]
               {::talk talk}))
  :handle-ok ::talk
  :handle-not-acceptable
  (fn [_] (str "Available media types are: "
               (join ", "
                     ["text/html"
                      "application/json"
                      "application/edn"]))))

;; > curl -HAccept:image/json -i 'http://localhost:4000/?id=10'












;;; 14

;; self-awareness

(defresource event
  :available-media-types ["text/html"
                          "application/json"
                          "application/edn"]
  :exists? (fn [{{{id "id"} :params} :request}]
             (if-let [talk (find-talk id)]
               {::talk talk}))
  :handle-ok ::talk
  :handle-not-acceptable
  (fn [{{types :available-media-types} :resource :as ctx}]
    (str "Available media types are: "
         (join ", " (types ctx)))))

;; > curl -HAccept:image/json -i 'http://localhost:4000/?id=10'













;;; 15

;; last-modified

(defn begin-of-last-minute []
  (* (int (/ (System/currentTimeMillis) 60000)) 60000))


(defresource event
  :available-media-types ["text/html"
                          "application/json"
                          "application/edn"]
  :exists? (fn [{{{id "id"} :params} :request}]
             (if-let [talk (find-talk id)]
               {::talk talk}))
  :handle-ok ::talk
  :last-modified (fn [_] (begin-of-last-minute)))

;; > curl -i -H Accept:application/edn 'http://localhost:4000/?id=6'
;; > curl -i -H If-Modified-Since:'Mon, 14 Oct 2013 11:30:00 GMT' -H Accept:application/edn  'http://localhost:4000/?id=6'


























;;; 16

;; etag

(defresource event
  :available-media-types ["text/html"
                          "application/json"
                          "application/edn"]
  :exists? (fn [{{{id "id"} :params} :request}]
             (if-let [talk (find-talk id)]
               {::talk talk}))
  :handle-ok ::talk
  :last-modified (fn [_] (begin-of-last-minute))
  :etag (fn [{{{id "id"} :params} :request}]
          (apply str (reverse (str id id)))))


;; > curl -i -H Accept:application/edn 'http://localhost:4000/?id=16'
;; > curl -i -H 'If-None-Match:"6161"' -H Accept:application/edn  'http://localhost:4000/?id=16'











;;; 17

;; more content-negotiation

(defresource event
  :available-media-types ["text/html"
                          "application/json"
                          "application/edn"]
  :available-languages ["de" "en"]
  :exists? (fn [{{{id "id"} :params} :request}]
             (if-let [talk (find-talk id)]
               {::talk talk}))
  :handle-ok (fn [{{l :language t :media-type} :representation
                   talk ::talk}]
               (let [i18n {"en" {:speaker "Speaker"
                                 :title "Title"
                                 :time "Time"}
                           "de" {:speaker "Sprecher"
                                 :title "Titel"
                                 :time "Uhrzeit"}}]
                 (into {}
                       (map (fn [field]
                              [(get-in i18n [l field] field)
                               (get talk field field)])
                            (keys talk)))))
  :last-modified (fn [_] (begin-of-last-minute))
  :etag (fn [{{{id "id"} :params} :request}]
          (apply str (reverse (str id id)))))

;; > curl -i -H Accept:application/edn -H Accept-Language:de 'http://localhost:4000/?id=16'
















;;; 18

;; more methods: put

(defresource event
  :allowed-methods [:get :put]
  :available-media-types ["text/html"
                          "application/json"
                          "application/edn"]
  :exists? (fn [{{{id "id"} :params} :request}]
             (if-let [talk (find-talk id)]
               {::talk talk}))
  :handle-ok ::talk)

;; > echo '{:speaker "Philipp Meier (@ordnungswprog)"}' | curl -i -X PUT http://localhost:4000\?id\=6
;; > curl 'http://localhost:4000?id=6'





















