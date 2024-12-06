(ns game.room
  (:require [game.entity :as entity]
            [game.states :as states]))

(defn get-room [state]
  (get (:rooms state)
       (:current-room state)))

(defn create-exit [exit]
  (merge {:width 100 :height 100}
         exit))

(defn check-end-callback [state e]
  (let [end-callback-fn (:end-callback-fn e)]
    (if end-callback-fn
      (end-callback-fn state)
      state)))
        
(defn check-exits [state]
  (let [e (entity/get-player-overlap-ex state (:exits (get-room state)))]
    (if (not (nil? e))
      (-> state
          (check-end-callback e)
          (assoc :current-room (:goto e))
          (assoc-in [:player :pos] (:goto-pos e)))
      state)))

(defn emacs-exit-end [state]
  (states/set-state state :special-work-1))

;; (defn update-particles [state particles ]
;;   (map particles))

;; (defn special-work-update [state]
;;   (if (states/is-special-work-1? state)
;;     (-> state
;;         (update :particles (partial update-particles state)))))

(defn update-room [state]
  (check-exits state)
  )
