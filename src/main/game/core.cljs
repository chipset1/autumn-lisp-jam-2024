(ns game.core
  (:require [game.canvas2D :as c]
            [cljs.core.async :as async]
            [game.assets :as assets]
            [game.vector :as v]
            [game.player :as player]
            [game.entity :as entity]
            [game.npc :as npc]
            [game.dialog :as dialog]
            [game.room :as room]
            [game.input :as input]
            [game.util :as util]
            [game.math :as math]
            [goog.events :as events]))

(def image-dims {:player-image {:width 113
                                  :height 258}})

(defn set-entity-dims [entity]
  (merge entity
         (get image-dims (:image-key entity))))

(def screen-width 1980)
(def screen-height 1020)
(def debug true)
(defonce game-state
  (atom {:last-frame-time 0
         :debug? debug
         :screen-width screen-width
         :screen-height screen-height

         :dialog/take-index 0
         :dialog/index 0
         :dialog/character-time 50

         :player (player/create 0 0)
         :current-room :start
         :game-state-key :start
         :rooms {:start {:entities [(set-entity-dims (npc/create 500 900 :player-image))
                                    (entity/create {:pos [0 0]
                                                    :image-key :grey-house})

                                    (entity/create {:pos [400 0]
                                                    :image-key :grey-house})

                                    (entity/create {:pos [800 0]
                                                    :image-key :grey-house})


                                    (entity/create {:pos [1200 0]
                                                    :image-key :grey-house})
                                    ]
                         :exits [(room/create-exit {:pos [64 344]
                                                    :goto :room2})
                                 (room/create-exit {:pos [-2000 0]
                                                    :goto :bathroom})]}
                 :room2 {:entities [(entity/create {:pos [0 0]
                                                    :image-key :player-image})

                                    (entity/create {:pos [0 100]
                                                    :image-key :player-image})

                                    (entity/create {:pos [0 200]
                                                    :image-key :player-image})
                                    ]
                         :exits [(room/create-exit {:pos [0 300]
                                                    :goto :start})]}}
         }))

(defn init []
  ;(c/set-size! js/window.innerWidth js/window.innerHeight)
  (c/setup-canvas! game-state screen-width screen-height)
  (assets/load-image! game-state :player-image "npcBody.png" )
  (assets/load-image! game-state :grey-house "greyHouse1.png" )
  (assets/load-image! game-state :background "background.jpg" )
  (assets/load-sound! game-state :background "audio/background.wav" )

  )

(defn debug-start-game []
  #_(assets/loop-sound @game-state :background)
  )


(defn update-dt [game-state current-time]
  (let [delta-time (/ (- current-time (:last-frame-time game-state))
                      1000)]
    (assoc game-state
           :last-frame-time current-time
           :millis current-time
           :dt delta-time)))

(defn update-game [game-state current-time]
  (-> game-state
      (update-dt current-time)
      (dialog/update-dialog)
      (player/update-player)
      (room/update-room)

      )
  )

(defn camera-update [state]
  (c/translate (+ (- (- (v/x (:pos (:player state))))
                     (math/half (:width (:player state))))
                  (math/half (c/get-screen-width)))
               (+ (- (- (v/y (:pos (:player state))))
                     (math/half (:height (:player state))))
                  (math/half (c/get-screen-height)))))

(defn update-components [state entity]
  (-> entity
      (entity/if-run :dialog (fn [e]
                               (dialog/draw-dialog-box state e)
                               (dialog/draw-interact-pop-up state e)))))

(defn draw [current-time]

  (let [state (swap! game-state #(update-game % current-time))]


    (c/save)

    (c/background "grey")
    (c/fill "blue")
    (c/draw-debug-text (str "fps:" (int (/ 1 (:dt state)))) 20 20)
    (c/draw-debug-text (str "player pos:" (:pos (:player state))) 20 30)
    (camera-update state)


    #_(c/draw-image (assets/get-image state :grey-house) 0.3)
    (doseq [e (:entities (room/get-room state))]
      (entity/draw-entity state e)
      (update-components state e)
      )
    (doseq [exit (:exits (room/get-room state))]
      (c/fill "green")
      (entity/draw-debug-entity exit))

    (player/draw-player state)
    (c/restore)
    )
  )
(cond-> {:a 1} :a (assoc :b true))
(defn run-debug []
  (when (input/check @game-state :o)
    (assets/sound-pause @game-state :background))
  (when (input/check @game-state :p)
    (assets/loop-sound @game-state :background))
  )

(defn js-tick [current-time]
  (draw current-time)
  (when debug (run-debug))
  (js/requestAnimationFrame js-tick)
  )

(defn swap-if-valid-key! [key key-state]
  (let [keyword-key (if (= " " key)
                      :space
                      (keyword key))]
    (when (input/valid-keys keyword-key)
      (swap! game-state
             assoc-in
             [:input keyword-key] key-state))))

;; run once per key press
(defn key-pressed []
  (swap! game-state (fn [state]
                      (-> state
                          (dialog/key-pressed)
                          ))))


(defn listen-for-keys []
  ;; https://stackoverflow.com/questions/54514261/how-to-make-a-2d-character-run-and-jump-in-javascript
  (let [key-listen (fn [event]
                     (swap-if-valid-key! event.key (= event.type "keydown"))
                     (key-pressed)
                     )]
    (events/listen js/window "keyup" key-listen)
    (events/listen js/window "keydown" key-listen)))

(defn listen-mouse []
  (js/window.addEventListener "click" debug-start-game))

(events/listen js/window "load"
      #(do (init)
           (defonce d (do (js-tick 0)))
           (listen-for-keys)
           (events/listen js/window "resize"
                          (fn []
                            (c/set-size! game-state js/window.innerWidth js/window.innerHeight)))
           (listen-mouse)))


(comment
  (cljs.pprint/pprint (dissoc @game-state :assets))
  (c/get-screen-width) ; 1188
  (c/get-screen-height) ;612

  )
