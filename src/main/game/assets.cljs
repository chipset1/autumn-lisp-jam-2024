(ns game.assets
  (:require [game.canvas2D :as c]
            [cljs.core.async :as async]))
            

(defn- set-local-path [path]
  (str "./assets/" path))


(defn load-image! [game-state-atom k path]
  (c/load-image! (set-local-path path)
                 #(this-as this
                    (swap! game-state-atom assoc-in [:assets :images k] this))))


(defn load-image-ex [path]
(let [img (js/Image.)
        _ (set! img.src path)
      _ (set! img.onerror (fn [e] (js/console.error "error" path e.message)))]
  img))


(defn load-cutscene-strings [dir count]
  (map (fn [image-name]
         (str "./assets/cutscenes/" dir "/" image-name ".jpg"))
       (range 0 count)))

(defn load-cutscene! [game-state-atom k data-map]
  (swap! game-state-atom
         assoc-in
         [:assets :cutscenes k]
         {:images (mapv load-image-ex (load-cutscene-strings (:dir data-map) (:max-frames data-map)))
          :max-frames (:max-frames data-map)}))

(defn get-image [game-state-map key]
  (get-in game-state-map [:assets :images key]))

(defn get-cutscene-images [game-state-map key]
  (get-in game-state-map [:assets :cutscenes key :images]))

(defn load-sound! [game-state-atom k sound-path]
  (swap! game-state-atom assoc-in [:assets :audio k]
         (js/Audio. (set-local-path (str "audio/" sound-path)))))

(defn play-sound [game-state-map key]
  (.play (get-in game-state-map [:assets :audio key])))

(defn loop-sound [game-state-map key]
(let [audio (get-in game-state-map [:assets :audio key])]
  (set! audio.loop true)
  (.play audio)))

(defn sound-pause [game-state-map key]
(let [audio (get-in game-state-map [:assets :audio key])]
  (.pause audio)))
