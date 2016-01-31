(defproject webdev "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 ;; [ring "1.4.0"] includes all 4 ring libraries:
                 ;; ring-core, ring devel, ring-servlet and ring-jetty-adapter
                 [ring "1.4.0"]
                 [compojure "1.4.0"]]

  :min-lein-version "2.0.0"
  
  :uberjar-name "webdev.jar"
  
  ;; When we type in the terminal, "lein run 8000" runs the server in port 8000
  ;; and it runs the project's -main function.
  ;; It calls the -main function in the namespace specified as :main.
  ;; If the main function is not called -main, you can use a
  ;; namespaced symbol like clojure.main/main
  :main webdev.core

  :profiles {:dev
             {:main webdev.core/-dev-main}})
