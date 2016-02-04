(ns webdev.core
  ;; We import our model code 
  (:require [webdev.item.model :as items]
            [webdev.item.handler :refer [handle-index-items]]
            [webdev.item.handler :refer [handle-create-item]])
  (:require
            ;; ring.adapter.jetty is an adapter we can use in dev and production
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            ;; handle-dump helps us to see the request in a nice format
            ;; It's useful as a debugging tool
            [ring.handler.dump :refer [handle-dump]]))

;; We store the connection url for the database
(def db "jdbc:postgresql://localhost/webdev")

;; handler
(defn greet [req]
  {:status 200
   :body "Hello, world!"
   :headers {}})

;; handler
(defn goodbye [req]
  {:status 200
   :body "Goodbye, cruel world!"
   "headers" {}})

;; handler
(defn about [req]
  {:status 200
   :body "Hello, I'm Mi-Mi Na. I love the beauty of Geometry"
   :headers {}})

;; handler
(defn yo [req]
  (let [name (get-in req [:route-params :name])]
   {:status 200
   :body (str "Yo!" name "!")
   :headers {}}))

(def ops
  {"+" +
   "-" -
   "*" *
   ":" /})

;; handler
(defn calc [req]
  (let [a (Integer. (get-in req [:route-params :a]))
        b (Integer. (get-in req [:route-params :b]))
        op (get-in req [:route-params :op])
        f (get ops op)]
   (if f
     {:status 200
      :body (str (f a b))
      :headers {}}
     {:status 404
      :body (str "Unknown operator: " op)
      :headers {}})))

;; compojure routes
(defroutes routes
  (GET "/" [] greet)
  (GET "/goodbye" [] goodbye)

  ;; Compojure can also have variable paht elements in a routing path
  ;; we specify a variable segment by starting it with a colon
  ;; They will be added to the key :route-params, always as a string
  (GET "/yo/:name" [] yo)
  
  (GET "/calc/:a/:op/:b" [] calc)
  (GET "/about" [] about)
  (GET "/items" [] handle-index-items)
  (POST "/items" [] handle-create-item)
  
  ;; para la siguiente ruta usamos un handler que no definimos aquí
  ;; sino que está definido ya en la librería ring.handler.dump
  ;; handle-dump nos permite ver de forma bonita la request
  (ANY "/request" [] handle-dump)
  (not-found "Page not found"))

;; middleware stack

;; In our model.clj functions we have to pass a db parameter that represents
;; the connection to the database. We will want to pass that into the handler
;; To do that we create a middleware that adds the db to the Ring request
;; Middleware are higher order functions
;; That way we will ensure that the database is available to all handlers
;; Este middleware transforma la request antes de pasársela al handler
(defn wrap-db [hdlr]
  (fn [req]
    (hdlr (assoc req :webdev/db db))))

;; Este middleware actúa después de que el handler ha actuado sobre la request
;; es decir, actúa sobre una Ring response
(defn wrap-server [hdlr]
  (fn [req]
    (assoc-in (hdlr req)  [:headers "Server"] "Geometrica 9000"  )))


(def app
  (wrap-server
   (wrap-file-info
    (wrap-resource
     (wrap-db
      (wrap-params
       routes))
     "static"))))

;; This is our main function and it runs the jetty adapter
;; run-jetty takes a handler and an options map.
(defn -main [port]
  ;; We add  a call to the create-table function in the -main to create
  ;; the table if it doesn't exist before we start the adapter
  (items/create-table db)
  (jetty/run-jetty app                 {:port (Integer. port)}))

;; Este -main will be only called in development
;; We use here wrap-reload to wrap the handler. We refer to the app
;; var and not its value directly, so that when it gets redefined
;; the changes will be available to the adapter
(defn -dev-main [port]
  (items/create-table db)
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
