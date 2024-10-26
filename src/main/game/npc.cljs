(ns game.npc
  (:require [game.input :as input]
            [game.util :as util]
            [game.vector :as v]
            [game.canvas2D :as c]
            [game.entity :as entity]))


;; TODO: refactor so dialog is independent from npc

(defn create [x y image-key]
  (entity/create {:pos [x y]
                  :image-key image-key
                  :image-scale 0.2
                  :interact-pop-up-str "talk"
                  :dialog ["hello" "this is a test"]}))

(defn get-npc [id state]
  (first (filter #(= (:id %) id) (:npcs (util/get-room state))))
  )

(defn update-end-dialog [state]
  (let [n (get-npc (:talking-npc-id state) state)]
    (if (and (util/game-state-npc-talking? state)
             (not (nil? n))
             (>= (:dialog-index state)
                 (count (:dialog n))))
      (assoc state
             :game-state-key :in-room
             :talking-npc-id nil
             :dialog-index 0
             :dialog-take-index 0)
     state))
  )

(defn get-player-overlap-npc [state]
  (first (filter #(entity/aabb? (:player state) %)
                 (:npcs (util/get-room state)))))

(defn check-dialog [state]
  (let [n (get-player-overlap-npc state)]
   (if (and (input/talk-key? state)
            (not (= (:game-state-key state) :npc-talking))
            (not (nil? n)))
     (assoc state
            :talking-npc-id (:id n)
            :dialog-take-index 0
            :dialog-index 0
            :game-state-key :npc-talking)
     state)))

(defn update-next-dialog [state]
  (if (and (= (:game-state-key state) :npc-talking)
           (input/talk-key? state)
           (> (:dialog-take-index state)
              (count (nth (:dialog (get-npc (:talking-npc-id state) state))
                          (:dialog-index state)))))
    (-> state
        (update :dialog-index inc)
        (assoc :dialog-take-index 0))
  state))


(defn update-dialog-take [state]
  (if (and (= (:game-state-key state) :npc-talking)
           (>= (- (:millis state)
                  (:character-pop-up-dialog-start-time state))
               (:npc-dialog-character-time state)))
    (assoc state
           :character-pop-up-dialog-start-time (:millis state)
           :dialog-take-index (inc (:dialog-take-index state)))
    state))

(defn update-npc [state]
  (-> state
      (update-dialog-take)
      ))

(defn key-pressed [state]
  (-> state
      (check-dialog)
      (update-next-dialog)
      (update-end-dialog)))

(defn draw-interact-pop-up [npc state]
  (let [n (get-player-overlap-npc state)]
    (when (and (not (util/game-state-npc-talking? state))
               (not (nil? n)))
      (c/fill "black")
      (c/draw-text (str ">" (:interact-pop-up-str npc))
                   (v/x (:pos npc))
                   (- (v/y (:pos npc)) 20))
     )))

(defn draw-dialog-box [state npc]
  (when (and (= (:game-state-key state) :npc-talking)
             (= (:id npc) (:talking-npc-id state)))
    (let [box-width 500
          box-height 150
          x (- (v/x (:pos (:player state)))
               (/ box-width 2))
          y (+ (v/y (:pos (:player state)))
               (* 0.43 (c/get-screen-height)))]
      (c/save)
      (c/fill "rgba(0,0,0,0.8)")
      (c/draw-rect x y box-width box-height)
      (c/fill "white")
      (c/draw-text (apply str (take (:dialog-take-index state)
                                    (nth (:dialog npc) (:dialog-index state))))
               (+ x 20) (+ y 40))
      (c/restore))))


