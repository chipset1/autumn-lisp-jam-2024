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

