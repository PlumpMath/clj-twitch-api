(ns twitch.core)

(def endpoint "https://api.twitch.tv/kraken")
(def api-version ".v3")
(def mime-type (str "application/vnd.twitchtv" api-version "+json"))

(defrecord Session
    [endpoint     ; Base http url
     client-id    ; Twitch app id
     access-token ; Auth token
    ])


(defn make-session [client_id acc_token]
  (->Session endpoint
             client_id
             acc_token))


(defprotocol Blocks
  (blocks        [session])
  (block-user!   [session target])
  (unblock-user! [session target]))

(defprotocol Channels
  (channel         [session channel])
  (channel-videos  [session channel])
  (channel-follows [session channel])
  (channel-editors [session channel])
  (update-channel! [session & {:keys [status ; channel title
                                      game   ; game category
                                      delay  ; delay in secs
                                      feed_enabled]}])
  (reset-key!      [session channel])
  (run-ad!         [session channel length]) ; 30,60,90,120,150,180
  (channel-teams   [session channel]))

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
  (featured-streams []))

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
