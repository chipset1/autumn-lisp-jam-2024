(ns game.room
  (:require [game.entity :as entity]))

(defn get-room [state]
  (get (:rooms state)
       (:current-room state)))

(defn create-exit [exit]
  (merge {:width 100 :height 100}
         exit))

(defn check-exits [state]
  (let [e (entity/get-player-overlap-ex state (:exits (get-room state)))]
    (if (not (nil? e))
      (-> state
          (assoc :current-room (:goto e))
          (assoc-in [:player :pos] (:next-player-pos e)))
      state)))


(defn update-room [state]
  (check-exits state)
  )
