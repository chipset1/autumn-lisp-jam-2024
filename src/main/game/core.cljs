(ns game.core
  (:require [game.canvas2D :as c]
            [game.assets :as assets]
            [game.vector :as v]
            [game.player :as player]
            [game.input :as input]
            [goog.events :as events]))

(def screen-width 1980)
(def screen-height 1020)
(def debug true)
(defonce game-state (atom {:last-frame-time 0
                           :screen-width screen-width
                           :screen-height screen-height
                           :player (player/create 0 0)}))

(defn init []
  ;(c/set-size! js/window.innerWidth js/window.innerHeight)
  (c/setup-canvas! game-state screen-width screen-height)
  (print js/window.innerWidth js/window.innerHeight)
  (assets/load-image! game-state :player-image "/assets/npcBody.png" )
  (assets/load-image! game-state :grey-house "/assets/greyHouse1.png" )
  (assets/load-image! game-state :background "/assets/background.jpg" )
  (assets/load-sound! game-state :background "/assets/audio/background.wav" ))

(defn debug-start-game []
  (assets/loop-sound @game-state :background)
  )


(defn update-dt [game-state current-time]
  (let [delta-time (/ (- current-time (:last-frame-time game-state))
                      1000)]
    (assoc game-state
           :last-frame-time current-time
           :dt delta-time)))

(defn update-game [game-state current-time]
  (-> game-state
      (update-dt current-time)
      (player/update-player))
  )


(defn camera-update [game-state]
  (c/translate (- (- (v/x (:pos (:player game-state))) 800))
               (- (v/y (:pos (:player game-state)))))
  )

(defn draw [current-time]

  (let [state (swap! game-state #(update-game % current-time))]


    (c/save)


      (:canvas/set-tranform game-state)
    (c/background "grey")
    (c/fill "blue")
    (c/draw-text (str "fps:" (int (/ 1 (:dt state)))) 20 20)
    (camera-update state)
    (c/draw-image (assets/get-image state :grey-house) 0.3)
    (player/draw-player state)
    (c/restore)
    )
  )

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


(defn listen-for-keys []
  ;; https://stackoverflow.com/questions/54514261/how-to-make-a-2d-character-run-and-jump-in-javascript
  (let [key-listen (fn [event]
                     (swap-if-valid-key! event.key (= event.type "keydown")))]
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
