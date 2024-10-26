(ns game.util)

(defn get-room [state]
  (get (:rooms state)
       (:current-room state)))
