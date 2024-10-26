(ns game.entity
(:require [game.vector :as v]
            [game.canvas2D :as c]
            [game.assets :as assets]
            [game.input :as input]
   )
  )

(defn get-entity-image [game-state entity]
  (assets/get-image game-state (:image-key entity)))

(defn get-dimensions [game-state-map entity]
  
  (let [image (get-entity-image game-state-map entity)]

    (when image
      {:width (* (.-width image)
                (:image-scale entity))
      :height (* (.-height image)
                 (:image-scale entity))
      })))

(defn create [merge-map]
  (merge merge-map
         {:id (random-uuid)}))

#_(defn aabb-bb?? [game-state-map player bbpos width height]
  (aabb? ))

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
