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

(ns twitch.blocks
  (:require [twitch.core :as c])
  (:import [twitch.core Session]))

(defprotocol Blocks
  (get-blocks    [session user])
  (add-block!    [session user target])
  (remove-block! [session user target]))

(extend-protocol Blocks
  Session
  (get-blocks    [s u] (c/api-get s (str "/users/" u "/blocks")) "blocks")
  (add-block!    [s u t] (c/api-put s (str "/users/" u "/blocks/" t)))
  (remove-block! [s u t] (c/api-delete (str "/users/" u "/blocks/" t))))
