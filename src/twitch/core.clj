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
            [clojure.string :as s]))

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

(defn make-url [session url]
  "If url starts with https:// then assume
   it is a full url and ignore the session endpoint"
  (if (s/starts-with? url "https://") url (str (:endpoint session) url)))

(defn api-get
  ([s path rkey]
   (api-get s path rkey nil {}))

  ([s path rkey args hdrs]
   (let [hdrs (merge {"Accept" (:mime-type s)})
         params (if args {:query-params args})
         params (assoc params :headers hdrs)
         params (assoc params :debug true)
         r (http/get (make-url s path) params)
         body (json/read-str (:body r))
         xs (get body rkey)]
     (if-let [x (first xs)]
       (lazy-seq (cons x (api-get s path rkey args hdrs
                                  (assoc body rkey (rest xs))))))))
  
  ([s path rkey args hdrs body]
   (let [xs (get body rkey)]
     (if-let [x (first xs)]
       (lazy-seq (cons x (api-get s path rkey args hdrs
                                  (assoc body rkey (rest xs)))))
       (if-let [next-url (get-in body ["_links" "next"])]
         (api-get s next-url rkey args hdrs)
         (if-let [cursor (get body "_cursor")]
           (api-get s path rkey (assoc args "cursor" cursor) hdrs)))))))

(defn put
  ([session url data] (put session url data {}))
  ([session url data hdrs]
   (let [hdrs (merge {"Accept" (:mime-type session)})]
     (http/put (str (:endpoint session) url)
               {:headers hdrs :body (str data)}))))

(defn post
  ([session url data] (post session url data {}))
  ([session url data hdrs]
   (let [hdrs (merge {"Accept" (:mime-type session)})]
     (http/post (str (:endpoint session) url)
                {:headers hdrs :body (str data)}))))

(defn delete
  ([session url] (delete session url {}))
  ([session url hdrs]
   (let [hdrs (merge {"Accept" (:mime-type session)})]
         (http/delete (str (:endpoint session) url)
                      {:headers hdrs}))))

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
