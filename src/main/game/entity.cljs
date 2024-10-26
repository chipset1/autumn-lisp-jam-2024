(ns game.entity
  (:require [game.vector :as v]
            [game.canvas2D :as c]
            [game.assets :as assets]
            [game.input :as input]
            [game.util :as util]))

(defn aabb?
  ([e1 e2]
   (aabb? (:pos e1) (:width e1) (:height e1)
          (:pos e2) (:width e2) (:height e2)))
  ([pos1 size1 pos2 size2] (aabb? pos1 size1 size1 pos2 size2 size2))
  ([[x1 y1] width1 height1 [x2 y2] width2 height2]
   (and (< x1 (+ x2 width2))
        (> (+ x1 width1) x2)
        (< y1 (+ y2 height2))
        (> (+ y1 height1) y2))))

(defn get-entity-image [game-state entity]
  (assets/get-image game-state (:image-key entity)))

(defn get-all-in-room [state]
  (:entities (util/get-room state)))

(defn get-entity [id state]
  (first (filter #(= (:id %) id) (get-all-in-room state))))

(defn get-player-overlap [state]
  (first (filter #(aabb? (:player state) %)
                 (get-all-in-room state))))

(defn get-player-overlap-ex [state entities]
  (first (filter #(aabb? (:player state) %)
                 entities)))

(defn create [merge-map]
  (merge {:id (random-uuid)
          :image-scale 0.3}
         merge-map))

(defn if-run [entity comp-key func]
  (if (comp-key entity)
    (func entity)
    entity))

(defn draw-entity [game-state entity & callback]
  (let [pos (:pos entity)
        img-scale (:image-scale entity)
        image (get-entity-image game-state entity)]
      (c/save)
      (c/translate (v/x pos)
                   (v/y pos))
      (when callback (callback))
      (c/draw-image image img-scale)
      (c/restore)))

(defn draw-debug-entity [entity]
  (let [pos (:pos entity)]
      (c/draw-rect (v/x pos)
                   (v/y pos)
                   (:width entity)
                   (:height entity))))
