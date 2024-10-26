(ns game.util)

(defn get-room [state]
  (get (:rooms state)
       (:current-room state)))

(defn game-state-npc-talking? [state]
  (= (:game-state-key state) :npc-talking))

(defn spy [data]
  (print data)
  data)
