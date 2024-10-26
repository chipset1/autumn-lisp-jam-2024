(ns game.assets
  (:require [game.canvas2D :as c]))

(defn set-local-path [path]
  (str "./assets/" path))

(defn load-image! [game-state-atom k path]
  (c/load-image! (set-local-path path)
                 #(this-as this
                   (swap! game-state-atom assoc-in [:assets :images k] this))))

(defn get-image [game-state-map key]
  (get-in game-state-map [:assets :images key]))

(defn load-sound! [game-state-atom key sound-path]
  (swap! game-state-atom assoc-in [:assets :audio key] (js/Audio. (set-local-path sound-path))))

(defn play-sound [game-state-map key]
  (.play (get-in game-state-map [:assets :audio key])))

(defn loop-sound [game-state-map key]
(let [audio (get-in game-state-map [:assets :audio key])]
  (set! audio.loop true)
  (.play audio)))

(defn sound-pause [game-state-map key]
(let [audio (get-in game-state-map [:assets :audio key])]
  (.pause audio)))
