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

(defn get-dialog [entity]
  (if (vector? (:dialog entity))
    (nth (:dialog entity)
         (:current-dialog-index entity))
    (:dialog entity)))

;; bounding box for aabb
(defn get-dialog-bb [entity]
  (let [pos (:pos entity)
        offset 100]
    {:pos [(- (v/x pos) offset)
           (- (v/y pos) offset)]
     :width (+ (:width entity) offset)
     :height (+ (:height entity) offset)}))

(defn get-player-overlap [state]
  (first (filter #(entity/aabb? (:player state) (get-dialog-bb %))
                 (:entities (util/get-room state)))))

(defn check-dialog [state]
  (let [n (get-player-overlap state)]
   (if (and (input/talk-key? state)
            (not (:run-already? (get-dialog n)))
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
              (count (nth (:text (get-dialog (entity/get-entity (:dialog/entity-id state) state)))
                          (:dialog/index state)))))
    (-> state
        (update :dialog/index inc)
        (assoc :dialog/take-index 0))
  state))

(defn inc-vector-dialog [state]
  (entity/update-in-room state
                         (fn [entity]
                           (if (= (:id entity) (:dialog/entity-id state))
                             (if (vector? (:dialog entity))
                               (update entity :current-dialog-index (fn [i]
                                                                      (js/Math.min (dec (count (:dialog entity)))
                                                                                   (inc i))))
                               entity)
                             entity))))

(defn update-end-dialog [state]
  (let [e (entity/get-entity (:dialog/entity-id state) state)
        dialog (get-dialog e)
        end-callback-fn (or (:end-callback-fn dialog) identity)]
    (if (and (states/dialog-running? state)
             (not (nil? e))
             (>= (:dialog/index state)
                 (count (:text dialog))))
      (-> state
          (end-callback-fn)
          (inc-vector-dialog)
          (assoc :dialog/entity-id nil
              :dialog/index 0
              :dialog/take-index 0)
          (states/set-state :in-room)
          )
     state)))

(defn run-once [state]
  (entity/update-in-room state
                         (fn [entity]
                           (if (= (:id entity) (:dialog/entity-id state))
                             (if (not (vector? (:dialog entity)))
                               (assoc-in entity [:dialog :run-already?] true)
                               (assoc-in entity [:dialog (:current-dialog-index entity) :run-already?] true))
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
             (not (:run-already? (get-dialog entity)))
             (entity/aabb? (get-dialog-bb entity) (:player state)))
    (c/fill "black")
    (c/draw-text (str ">" (:interact-pop-up-str (get-dialog entity)))
                 (v/x (:pos entity))
                 (- (js/Math.min (v/y (:pos (:player state)))
                                 (v/y (:pos entity))) 20))
    ))

(defn draw-dialog-box [state entity]
  (when (and (states/dialog-running? state)
             (= (:id entity) (:dialog/entity-id state)))
    (let [text-string (nth (:text (get-dialog entity))
                           (:dialog/index state))
          box-width (* (count text-string) 23)
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
                                    (nth (:text (get-dialog entity))
                                         (:dialog/index state))))
               (+ x 20) (+ y 40))
      (c/restore))))

