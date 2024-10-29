(ns game.dialog
  (:require [game.input :as input]
            [game.util :as util]
            [game.vector :as v]
            [game.states :as states]
            [game.canvas2D :as c]
            [game.entity :as entity]))

;; example dialog component
;; {:text ["hello" "something"]
;;  :interact-pop-up-str "talk"}

(defn check-dialog [state]
  (let [n (entity/get-player-overlap state)]
   (if (and (input/talk-key? state)
            (not (:run-already? (:dialog n)))
            (not (states/dialog-running? state))
            (not (nil? n)))
     (-> state
         (assoc :dialog/entity-id (:id n)
                :dialog/take-index 0
                :dialog/index 0)
         (states/set-state :dialog-running))
     state)))


(defn update-dialog-take [state]
  (if (and (states/dialog-running? state)
           (>= (- (:millis state)
                  (:dialog/character-pop-up-start-time state))
               (:dialog/character-time state)))
    (assoc state
           :dialog/character-pop-up-start-time (:millis state)
           :dialog/take-index (inc (:dialog/take-index state)))
    state))

(defn update-next-dialog [state]
  
  (if (and (states/dialog-running? state)
           (input/talk-key? state)
           (> (:dialog/take-index state)
              (count (nth (:text (:dialog (entity/get-entity (:dialog/entity-id state) state)))
                          (:dialog/index state)))))
    (-> state
        (update :dialog/index inc)
        (assoc :dialog/take-index 0))
  state))

(defn update-end-dialog [state]
  (let [e (entity/get-entity (:dialog/entity-id state) state)
        end-callback-fn (or (:end-callback-fn (:dialog e)) identity)]
    (if (and (states/dialog-running? state)
             (not (nil? e))
             (>= (:dialog/index state)
                 (count (:text (:dialog e)))))
      (-> state
          (end-callback-fn)
          (assoc :dialog/entity-id nil
              :dialog/index 0
              :dialog/take-index 0)
          (states/set-state :in-room))
     state)))

(defn run-once [state]
  (entity/update-in-room state
                         (fn [entity]
                           (if (= (:id entity) (:dialog/entity-id state))
                             (assoc-in entity [:dialog :run-already?] true)
                             entity))))

(defn update-dialog [state]
  (-> state
      (update-dialog-take)
      ))

(defn key-pressed [state]
  (-> state
      (check-dialog)
      (update-next-dialog)
      (update-end-dialog)))



(defn draw-interact-pop-up [state entity]
  (when (and (not (states/dialog-running? state))
             (not (:run-already? (:dialog entity)))
             (entity/aabb? entity (:player state)))
    (c/fill "black")
    (c/draw-text (str ">" (:interact-pop-up-str (:dialog entity)))
                 (v/x (:pos entity))
                 (- (v/y (:pos entity)) 20))
    ))

(defn draw-dialog-box [state entity]
  (when (and (states/dialog-running? state)
             (= (:id entity) (:dialog/entity-id state)))
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
      (c/draw-text (apply str (take (:dialog/take-index state)
                                    (nth (:text (:dialog entity))
                                         (:dialog/index state))))
               (+ x 20) (+ y 40))
      (c/restore))))

