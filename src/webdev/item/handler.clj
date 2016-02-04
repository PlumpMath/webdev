(ns webdev.item.handler
  (:require [webdev.item.model :refer [create-item
                                       read-items
                                       update-item
                                       delete-item]]))
;; We need a form and an end point to post to.
(defn handle-index-items [req]
  (let [db (:webdev/db req)
        items (read-items db)]
    {:status 200
     :headers {}
     :body (str "<html><head></head><body><div>"
                (mapv :name items)
                ;; We create a form. This form will post to /request because we
                ;; want to debug it a bit  before we point it to a real end point
                ;; "</div><form method=\"POST\" action=\"/request\">"
                ;; Now we point to a real end point
                "</div><form method=\"POST\" action=\"/items\">"
                "<input type=\"text\" name=\"name\" placeholder=\"name\">"
                "<input type=\"text\" name=\"description\" placeholder=\"description\">"
                "<input type=\"submit\">"
                "</body></html>")}))

;; Here we create new items. We get the name parameter and the description parameter
;; from the the form-parameters, wich is where the middleware put them
(defn handle-create-item [req]
  (let [name (get-in req [:params "name"])
        description (get-in req [:params "description"])
        db (:webdev/db req)
        item-id (create-item db name description)]
    ;; 302 status is for redirect. 302 usually goes with an empty body
    ;; and it's the standard way of responding to a POST 
    {:status 302
     ;; We put a location header to indicate where the browser should
     ;; redirect to.
     :headers {"Location" "/items"}
     :body ""}))
