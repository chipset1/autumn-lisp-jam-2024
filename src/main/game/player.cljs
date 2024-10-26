(ns game.player
  (:require [game.vector :as v]
            [game.canvas2D :as c]
            [game.assets :as assets]
            [game.input :as input]
   ))

(defn create [w h]
  {:pos [(/ w 2) (/ h 2)]
   :image-scale 0.2
   :image-key :player-image
   :width 113
   :height 258
   :speed (* 5 60)})

(defn- move-player
  "this isn't correct since dt is multiplied twice when moving in 2 direction"
  [player vel-direction game-state]
  (update player
          :pos
          (fn [pos]
            (-> vel-direction
                (v/mult (:speed player))
                (v/mult (:dt game-state))
                (v/add pos)))))

(defn movement [game-state player]
  (cond-> player
    (input/move-up game-state) (move-player [0 -1] game-state)
    (input/move-down game-state) (move-player [0 1] game-state)
    (input/move-left game-state) (move-player [-1 0] game-state)
    (input/move-right game-state) (move-player [1 0] game-state)
    ))

(defn update-player [game-state]
  (update game-state :player #(movement game-state %)))

(defn draw-player [game-state]
  
  (when-let [player-img (assets/get-image game-state :player-image)]
    (let [pos (:pos (:player game-state))
          img-scale (:image-scale (:player game-state))
          player-width (* (.-width player-img) img-scale)
          player-height (* (.-height player-img) img-scale)]
      
      (c/save)
      (c/translate (v/x pos) (v/y pos))
      #_(c/translate (+ (v/x pos)
                      (- (/ (:canvas/device-width game-state) 2))
                      (/ player-width 2))
                   (+ (v/y pos)
                      (/ (:canvas/device-height game-state) 2)
                      (- (/ player-height 2)))
                   )
      (c/draw-image player-img img-scale)
      (c/restore)))
  )
