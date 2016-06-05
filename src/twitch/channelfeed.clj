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

(ns twitch.channelfeed
  (:require [twitch.core :as c])
  (:import [twitch.core Session]))

(defprotocol ChannelFeed
  (get-all      [session channel])
  (get-post     [session channel postid])
  (new-post!    [session channel content share])
  (delete-post! [session channel postid])
  (create-reaction! [session channel postid emoteid])
  (delete-reaction! [session channel postid emoteid]))

(extend-protocol ChannelFeed
  Session
  (get-all   [s ch] (c/api-get s (str "/feed/" ch "/posts") "posts"))
  (get-post  [s ch id] (c/api-get s (str "/feed/" ch "/posts/" id)))
  (new-post! [s ch content share]
    (c/api-post s (str "/feed/" ch "/posts") {:content content
                                              :share share}))
  (delete-post! [s ch id]
    (c/api-delete s (str "/feed/" ch "/posts/" id)))
  (create-reaction! [s ch id emote]
    (c/api-post s (str "/feed/" ch "/posts/" id "/reactions")
                {:emote_id emote}))
  (delete-reaction! [s ch id emote]
    (c/api-delete s (str "/feed/" ch "/posts/" id "/reactions")
                  {:emote_id emote}))
  )
