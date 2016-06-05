(ns twitch.auth
  (:require [twitch.core :as c]
            [clojure.string :as s]
            [clojure.data.json :as json]
            [clj-http.client :as http]))

(defn make-auth-code-url
  [& {:keys [client-id redirect-url scope state-token force-verify]}]
  (str c/endpoint "/oauth2/authorize"
       "?response_type=code"
       "&client_id=" client-id
       "&redirect_uri=" redirect-url
       "&scope=" (s/join "+" scope)
       "&state=" state-token
       (if force-verify (str "&force_verify=" force-verify))))

(defn redeem-access-token
  [& {:keys [client-id client-secret redirect-url code state]}]
  (let [url (str c/endpoint "/oauth2/token")
        raw (http/post url {:form-params {:client_id client-id
                                          :client_secret client-secret
                                          :grant_type "authorization_code"
                                          :redirect_uri redirect-url
                                          :code code
                                          :state state}})]
    (json/read-str (:body raw))))
