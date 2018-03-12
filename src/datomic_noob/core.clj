(ns datomic-noob.core
  (:gen-class)
  (:require [datomic.client.api :as d]))

;; assumes you are running a dev transactor.  Current transactor properties file located
;; at Documents/datomic-pro-0.9.5661/__STEVEN/dev-transactor-template.properties

;; assumes you have created a database at datomic:dev://localhost:4334/hello

;; assumes you have started the Datomic Peer Server that is referencing the hello database
;; using a command like bin/run -m datomic.peer-server -h localhost -p 8998 -a myaccesskey,mysecret -d hello,datomic:dev://localhost:4334/hello

(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :endpoint "localhost:8998"})

(def client (d/client cfg))

(def conn (d/connect client {:db-name "hello"}))

(def movie-schema [{:db/ident :movie/title
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The title of the movie"}

                   {:db/ident :movie/genre
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The genre of the movie"}

                   {:db/ident :movie/release-year
                    :db/valueType :db.type/long
                    :db/cardinality :db.cardinality/one
                    :db/doc "The year the movie was released in theaters"}])

(defn transact-a-schema [schema]
  (d/transact conn {:tx-data schema}))

(defn add-movie [title genre year]
  (let [movie {:movie/title title
               :movie/genre genre
               :movie/release-year year}]
    (d/transact conn {:tx-data [movie]})))

(defn get-movies []
  (let [db (d/db conn)
        query '[:find ?title ?genre ?year
               :where [?e :movie/title ?title]
                      [?e :movie/genre ?genre]
                      [?e :movie/release-year ?year]]]
    (d/q query db)))



