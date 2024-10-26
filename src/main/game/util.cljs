(ns game.util)

(defn spy [data]
  (print data)
  data)

;; duplicate in here avoid dependecy issue
(defn get-room [state]
  (get (:rooms state)
       (:current-room state)))
