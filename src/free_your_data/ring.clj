 (ns free-your-data.ring
  (:require [clojure.pprint :refer [pprint]]
            [free-your-data.data :refer (find-talk)]
            [ring.adapter.jetty :refer (run-jetty)]
            [ring.middleware.params :refer (wrap-params)])
  (:import java.util.TimeZone
           java.text.SimpleDateFormat
           java.util.Locale
           java.util.Date))


(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "a event"})

(def app
  (-> #'handler
      (wrap-params)))

(defonce jetty (run-jetty #'app {:port 3000 :join? false}))

(defn handler [{m :request-method}]
  (if (= :get m)
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body "Hello World2"}
    {:status 501
     :headers {"Content-Type" "text/plain"}
     :body "Not implemented"}))


(defn http-date-format []
  (let [df (new SimpleDateFormat
                "EEE, dd MMM yyyy HH:mm:ss z"
                Locale/US)]
    (do (.setTimeZone df (TimeZone/getTimeZone "GMT"))
        df)))

(defn begin-of-last-minute []
  (java.util.Date.
   (* (int (/ (System/currentTimeMillis) 600000))
      600000)))

(defn in-30-seconds []
  (java.util.Date. (+ (System/currentTimeMillis)
                      3000000)))


(defn handler [{m :request-method
                {id "id"} :params}]
  (if (= :get m)
    {:status 200
     :headers {"Content-Type" "text/plain; charset=UTF-8"}
     :body (pr-str (find-talk id))}
    
    {:status 405
     :body "Method not allowed."}))







(defn handler [{m :request-method
                {id "id"} :params}]
  (if (= :get m)
    (if-let [e (find-talk id)]
      {:status 200
       :headers {"Content-Type" "text/plain; charset=UTF-8"}
       :body (pr-str (find-talk id))}
      {:status 404
       :body "Not found."})    
    {:status 405
     :body "Method not allowed."}))






(defn handler [{m :request-method
                {id "id"} :params
                {accept "accept"} :headers}]
  (if (= :get m)
    (if-let [e (find-talk id)]
      (if-let [mt (some #(when (or (nil? accept) (= accept "*/*") (.contains accept %)) %)
                        ["application/clojure" "text/html"])]
        (condp = mt
          "application/clojure"
          {:status 200
           :headers {"Content-Type" "application/clojure; charset=UTF-8"}
           :body (pr-str e)}
          "text/html"
          {:status 200
           :headers {"Content-Type" "text/html; charset=UTF-8"}
           :body (apply format
                        "<html><h1>%s</h1><p>%s</p><p><small>%s</small></p></html>"
                        ((juxt :title :time :speaker) e)) })
        {:status 406 
         :body "Requested media type not acceptable."})
      {:status 404
       :body "Not found."})
    {:status 505
     :body "Method not allowed."}))









(defn handler [{m :request-method
                {id "id"} :params
                {accept "accept"
                 ims    "if-modified-since"} :headers}]
  (if (#{:get :post :put :delete :head :options :trace} m)
    (if (= :get m)
      (if-let [e (find-talk id)]
        (if-let [mt (some #(when (or (nil? accept) (= accept "*/*") (.contains accept %)) %)
                          ["application/clojure" "text/html"])]
          (condp = mt
            "application/clojure"
            {:status 200
             :headers {"Content-Type" "application/clojure; charset=UTF-8"}
             :body (pr-str e)}
            "text/html"
            {:status 200
             :headers {"Content-Type" "text/html; charset=UTF-8"}
             :body (apply format
                          "<html><h1>%s</h1><p>%s</p><p><small>%s</small></p></html>"
                          ((juxt :title :time :speaker) e)) })
          {:status 406 
           :body "Requested media type not acceptable."})
        {:status 404
         :body "Not found."})
      {:status 501
       :body "Not implemented."})
    {:status 405
     :body "Method not allowed."}))







(defn handler [{m :request-method
                {id "id"} :params
                {accept "accept"
                 ims    "if-modified-since"} :headers}]
  (if (#{:get :post :put :delete :head :options :trace} m)
    (if (= :get m)
      (if-let [e (find-talk id)]
        (if-let [mt (some #(when (or (nil? accept) (= accept "*/*") (.contains accept %)) %)
                          ["application/clojure" "text/html"])]
          (let [last-modified (begin-of-last-minute) 
                cache-headers {"Last-Modified"  (.format (http-date-format) last-modified)
                               "Cache-Control" "max-age=30, must-revalidate"
                               "Expires" (.format (http-date-format)
                                                  (in-30-seconds))
                               "Vary" "Accept"}]
            (if (and ims
                     (let [imsd (.parse (http-date-format) ims)]
                       (= last-modified imsd)))
              {:status 304
               :headers cache-headers}
              (condp = mt
                "application/clojure"
                {:status 200
                 :headers (assoc cache-headers "Content-Type" "application/clojure; charset=UTF-8")
                 :body (pr-str e)}
                "text/html"
                {:status 200
                 :headers (assoc cache-headers "Content-Type" "text/html; charset=UTF-8")
                 :body (apply format
                              "<html><h1>%s</h1><p>%s</p><p><small>%s</small></p></html>"
                              ((juxt :title :time :speaker) e)) })))
          {:status 406 
           :body "Requested media type not acceptable."})
        {:status 404
         :body "Not found."})
      {:status 501
       :body "Not implemented."})
    {:status 405
     :body "Method not allowed."}))








