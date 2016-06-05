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

(ns twitch.chat
  (:require [twitch.core :as c])
  (:import [twitch.core Session]))

(defprotocol Chat
  (all-emotes     [session])
  (emote-sets     [session sets])
  (channel-badges [session channel]))

(extend-protocol Chat
  Session
  (all-emotes [s] (c/api-get s (str "/chat/emoticons") "emoticons"))
  (emote-sets [s sets]
    (c/api-get s (str "/chat/emoticon_images") "emoticons"
               (if sets {"emotesets" sets}) nil))
  (channel-badges [s ch] (c/api-get s (str "/chat/" ch "/badges"))))
