;; Copyright 2016 Matt Shanker
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;; http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns twitch.core
  (:require [clj-http.client :as http]
            [clojure.data.json :as json]
            [dire.core :refer [with-postcondition! with-handler!]]))

(def endpoint "https://api.twitch.tv/kraken")
(def api-version ".v3")
(def mime-type (str "application/vnd.twitchtv" api-version "+json"))

(defrecord Session
    [endpoint     ; Base http url
     mime-type    ; mime-type header
     client-id    ; Twitch app id
     access-token ; Auth token
  ])

(defn make-session [client-id access-token]
  (->Session endpoint mime-type client-id access-token))

(defn get [session url]
  (try
    (let [rsp (http/get (str (:endpoint session) url)
                        {:headers {"Accept" (:mime-type session)}})]
      (assoc rsp :body (json/read-str (:body rsp))))
    (catch Exception e (ex-data e))))

(defn put [session url data]
  (try
    (http/put (str (:endpoint session) url)
              {:headers {"Accept" (:mime-type session)}
               :body (str data)})
    (catch Exception e (ex-data e))))

(defn post [session url data]
  (try
    (http/post (str (:endpoint session) url)
               {:headers {"Accept" (:mime-type session)}
                :body (str data)})
    (catch Exception e (ex-data e))))

(defn delete [session url]
  (try
    (http/delete (str (:endpoint session) url)
                 {:headers {"Accept" (:mime-type session)}})
    (catch Exception e (ex-data e))))

(with-postcondition! #'get
  :http-200
  (fn [rsp & args] (= (:status rsp) 200)))

(with-postcondition! #'put
  :http-200
  (fn [rsp & args] (= (:status rsp) 200)))

(with-postcondition! #'post
  :http-200
  (fn [rsp & args] (= (:status rsp) 200)))

(with-postcondition! #'delete
  :http-200
  (fn [rsp & args] (= (:status rsp) 200)))
(defprotocol Blocks
  (blocks        [session])
  (block-user!   [session target])
  (unblock-user! [session target]))

(defprotocol ChannelFeed
  (channel-posts    [session channel])
  (channel-post     [session channel postid])
  (create-post!     [session channel content share])
  (delete-post!     [session channel postid])
  (create-reaction! [session channel postid emoteid])
  (delete-reaction! [session channel postid emoteid]))

(defprotocol Chat
  (chat-endpoints [session channel])
  (all-emotes     [session])
  (emote-sets     [session & {:keys [sets]}])
  (channel-badges [session channel]))

(defprotocol Follows
  (channel-followers [session channel])
  (follows?  [session user channel])
  (follow!   [session channel notify])
  (unfollow! [session channel]))

(defprotocol Games
  (top-games [session]))

(defprotocol Ingests
  (ingest-endpoints [session]))

(defprotocol Search
  (search-channels [session query])
  (search-streams  [session query])
  (search-games    [session query & {:keys [live-only]}]))

(defprotocol Streams
  (channel-stream [session channel])
  (streams [session & {:keys [summary  ; return summary api results
                              game     ; game category
                              channel  ; csv list of channels
                              clientid ; only streams from app
                              type     ; 'all', 'playlist', 'live'
                              lang     ; language
                              ]}])
  (featured-streams [session]))

(defprotocol Subscriptions
  (channel-subscribers [session channel])
  (subscribed?         [session user channel]))

(defprotocol Teams
  (all-teams [session])
  (team      [session team]))

(defprotocol Users
  (user [session & {:keys [user]}])
  (enabled-emotes [session user])
  (channels-followed [session]))
