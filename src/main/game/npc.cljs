(ns game.npc
  (:require [game.input :as input]
            [game.util :as util]
            [game.vector :as v]
            [game.canvas2D :as c]
            [game.entity :as entity]))

(defn create [x y image-key]
  (entity/create {:pos [x y]
                  :image-key image-key
                  :image-scale 0.2
                  :dialog ["hello"]}))

(defn check-dialog [state]
  (let [n (first (filter #(entity/aabb? (:player state) %)
                         (:npcs (util/get-room state))))]
   (if (and (input/talk-key? state)
            (not (= (:game-state-key state) :npc-talking))
            (not (nil? n)))
     (assoc state
            :talking-npc-id (:id n)
            :dialog-take-index 0
            :game-state-key :npc-talking)
     state)))
    

(defn draw-dialog-box [state npc]
  (when (and (= (:game-state-key state) :npc-talking)
             (= (:id npc) (:talking-npc-id state)))
    (let [box-width 500
          box-height 150
          x (- (v/x (:pos (:player state)))
               (/ box-width 2))
          y (+ (v/y (:pos (:player state)))
               140)]
      (print "hjello")
      (c/save)
      (c/fill "rgba(0)")
      (c/draw-rect x y box-width box-height)
      #_(js/textSize 32)
      (c/fill "white")
      (c/draw-text (apply str (take (:dialog-take-index state)
                                (nth (:dialog npc) (:dialog-index state))))
               (+ x 20) (+ y 40))
      (c/restore))))


