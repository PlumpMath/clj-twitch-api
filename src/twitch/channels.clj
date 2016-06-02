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

(ns twitch.channels
  (:require [twitch.core :as c])
  (:import [twitch.core Session]))

(defprotocol Channels
  (get        [session channel])
  (videos     [session channel])
  (follows    [session channel])
  (editors    [session channel])
  (teams      [session channel])
  (update!    [session channel status ; channel title
                               game   ; game category
                               delay  ; delay in secs
                               feed-enabled])
  (run-ad!    [session channel length]) ; 30,60,90,120,150,180
  (reset-key! [session channel]))


(extend-protocol Channels
  Session
  (get     [s ch] (c/get s (str "/channels/" ch)))
  (videos  [s ch] (c/get s (str "/channels/" ch "/videos")))
  (follows [s ch] (c/get s (str "/channels/" ch "/follows")))
  (editors [s ch] (c/get s (str "/channels/" ch "/editors")))
  (teams   [s ch] (c/get s (str "/channels/" ch "/teams")))
  (update! [s ch status game delay feed-enabled]
    (c/put s (str "/channels/" ch) {:channel {:status status
                                              :game game
                                              :delay delay
                                              :channel_feed_enabled feed-enabled}}))
  (run-ad! [s ch secs] (c/post s (str "/channels/" ch "/commercial") secs))
  (reset-key! [s ch] (c/delete s (str "/channels/" ch "/stream_key"))))
