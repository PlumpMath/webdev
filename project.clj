(defproject webdev "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 ;; [ring "1.4.0"] includes all 4 ring libraries:
                 ;; ring-core, ring devel, ring-servlet and ring-jetty-adapter
                 [ring "1.4.0"]
                 ;; Compojure takes care of routing and HTTP methods
                 [compojure "1.4.0"]
                 ;; clojure.java.jdbc is a wrapper for JDBC-based access to DB.
                 [org.clojure/java.jdbc "0.4.2"]
                 [postgresql/postgresql "9.1-901-1.jdbc4"]]

  ;; We need to specify that we want at least leiningen 2
  ;; wich has better support on heroku
  :min-lein-version "2.0.0"
  
  ;; We also need to specify the name of the jar file, wich is the
  ;; java file. When we deploy heroku will this file for us. It will
  ;; include all dependencies.
  :uberjar-name "webdev.jar"
  
  ;; When we type in the terminal, "lein run 8000" runs the server in port 8000
  ;; and it runs the project's -main function.
  ;; It calls the -main function in the namespace specified as :main.
  ;; If the main function is not called -main, you can use a
  ;; namespaced symbol like clojure.main/main
  :main webdev.core

  ;; We create a dev profile and specify where to find -dev-main.
  :profiles {:dev
             {:main webdev.core/-dev-main}})
