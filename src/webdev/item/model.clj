(ns webdev.item.model
  (:require [clojure.java.jdbc :as db]) )

;; Given a database connection and a vector containing SQL and optional parameters, 
;; execute! performs a general (non-select) SQL operation.
;; The return value of execute! is a list containing the number of rows affected
;; We use execute! when we want to modify data in a DB

(defn create-table [db]
  ;; We need to create a uuid extension because our ID will be a uuid
  ;; UUID stands for universally unique identifiers
  (db/execute!
   db
   ["CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\""])
  ;; We create the table if it doesn't exist
  (db/execute!
   db
   ["CREATE TABLE IF NOT EXISTS items
         (id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
          name TEXT NOT NULL,
          description TEXT NOT NULL,
          checked BOOLEAN NOT NULL DEFAULT FALSE,
          date_created TIMESTAMPTZ NOT NULL DEFAULT now())"]))

;; We use query mainly for SELECT statements and also for commands
;; that returns rows:
;; "Given a database connection and a vector containing SQL and
;; optional parameters, perform a simple database query."

;; create-item will create a new item. Given a name and a description
;; db/query will return a list of maps. Each map represents a row. The column
;; names are turned into keywords and they are associated with the column values.

(defn create-item [db name description]
  (:id (first (db/query
           db
           ["INSERT INTO items (name, description)
            VALUES (?, ?)
            RETURNING id"
            name
            description]))))

;; update-item will change the status of an existing item
;; execute! return the number of rows that were changed inside of a list
;; so by comparing it to a list of 1, we can know if we were successful
;; It returns true if a single row was updated.
;; false means id not found
(defn update-item [db id checked]
  (= [1] (db/execute!
          db
          ["UPDATE items
            SET checked = ?
            WHERE id = ?"
            checked
            id])))

(defn delete-item [db id]
  (= [1] (db/execute!
          db
          ["DELETE FROM items
            WHERE id = ?"
           id])))

(defn read-items [db]
  (db/query
   db
   ["SELECT id, name, description, checked, date_created
     FROM items
     ORDER BY date_created"]))
