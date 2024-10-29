(ns game.npc
  (:require [game.input :as input]
            [game.util :as util]
            [game.vector :as v]
            [game.canvas2D :as c]
            [game.entity :as entity]))

(defn create [x y image-key args-map]
  (-> {:pos [x y]
       :image-key image-key
       :image-scale 0.2}
      (entity/create)
      (merge args-map)
      (util/spy)))

(defn cat-set-state-eat [state]
  (entity/update-in-room state
                         (fn [entity]
                           (if (= (:plain-id entity) :cat)
                             (util/spy (assoc entity :state :eatting))
                             entity)))
  )

(defn draw-cat [state cat-entity]
  (if (= :eatting (:state cat-entity))
    (entity/draw-entity state (assoc cat-entity :image-key :cat-eatting))
    (entity/draw-entity state cat-entity))
  
  )


(defn create-cat [x y image-key args-map]
  (assoc (create x y image-key args-map)
         :draw-fn draw-cat))
