(ns game.cutscene
  (:require [game.states :as states]
            [game.canvas2D :as c]
            [game.assets :as assets]
            ))

(defn get-current-cutscene [state]
  (get (:cutscenes (:assets state))
       (:cutscene/current state)))

(defn start-playing-cutscene [state name]
  (-> state
      (assoc :cutscene/current name
             :cutscene/frame 0)
      (states/set-state :playing-cutscene)))

(defn stop-playing [state]
  (if (and (states/playing-cutscene? state)
           (> (:cutscene/frame state)
              (dec (:max-frames (get-current-cutscene state)))))
    (states/set-state state :in-room)
    state))

(defn update-cutscene-frame [state]
  (if (and (states/playing-cutscene? state)
           (>= (- (:millis state)
                  (:cutscene/start-time state))
               (:cutscene/default-frame-time state)))
    (assoc state
           :cutscene/start-time (:millis state)
           :cutscene/frame (inc (:cutscene/frame state)))
    state)
  )

(defn draw-cutscene [state]
  (let [c (get-current-cutscene state)]
    (c/draw-image-full(nth (:images c)
                           (js/Math.min (dec (:max-frames c))
                                        (:cutscene/frame state))) )))



(defn update-cutscene [state]
  (-> state
      (update-cutscene-frame)
      (stop-playing)
      ))
