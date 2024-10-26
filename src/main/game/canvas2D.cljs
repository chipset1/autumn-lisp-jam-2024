(ns game.canvas2D
  (:require [game.math :as math]))

;;var scaleFitNative = Math.max(deviceWidth / nativeWidth, deviceHeight / nativeHeight);
(defonce context
  (let [canvas (js/document.querySelector "canvas")
        context (.getContext canvas "2d")]
    context))


(defn set-size! [game-state width height]
  (set! context.canvas.width width)
  (set! context.canvas.height height)
  (swap! game-state assoc
           :canvas/device-width width
           :canvas/device-height height
         )

  )
(defn get-screen-width []
  context.canvas.width)

(defn get-screen-height []
  context.canvas.height)

(defn setup-canvas! [game-state native-width native-height]
  (let [native-width 1980 ;; 1387
        native-height 1020 ;; 714
        ]
    (set-size! game-state (* native-width 0.6) (* native-height 0.6)
               )
    
    #_(swap! game-state
           assoc
           :canvas/scale-fill-native scale-fill-native
           :canvas/set-transform
           #(.setTransform context
                           scale-fill-native 0
                           0 scale-fill-native
                           (js/Math.floor (/ device-width 2))
                           (js/Math.floor (/ device-height 2)))
           )))

(defn load-image! [path callback]
  (let [img (js/Image.)
        _ (set! img.src path)
        _ (set! img.onload callback)
        _ (set! img.onerror (fn [e] (print "error" e)))]))

(defn draw-image
  ([img]
   (.drawImage context img 0 0))
  ([img percent]
   (when (nil? percent) (js/console.error "image percent is nil"))
   (if (not (nil? img))
     (draw-image img
                (* percent img.width)
                (* percent img.height))
     (js/console.log "image is nil")))
  ([img width height]
   (if (not (nil? img))
     (.drawImage context img 0 0 width height)
     (js/console.log "image is nil"))
   ))

(defn fill [str]
  (set! (.-fillStyle context) str))

(defn stroke-style [str]
  (set! context.strokeStyle str))

(defn draw-stroke-rect [x y w h]
  (.strokeRect context x y w h))

(defn save []
  (.save context))

(defn restore []
  (.restore context))

(defn translate [x y]
  (.translate context x y))

(defn draw-rect [x y w h]
  (.beginPath context)
  (.rect context x y w h)
  (.fill context))

(defn draw-text [str x y]
  (set! context.font "32px ProggyFont")
  (.fillText context str x y))

(defn draw-debug-text [str x y]
  (set! context.font "16px ProggyFont")
  (.fillText context str x y))

(defn background [color]
  (fill color)
  (draw-rect 0 0 context.canvas.width context.canvas.height))
