(ns game.assets
  (:require [game.canvas2D :as c]
           [cljs.core.async :as async]))
            

(defn set-local-path [path]
  (str "./assets/" path))

(defn load-image-async! [path]
  (let [ch (async/chan)]
    (c/load-image! (set-local-path path)
                   #(this-as this
                      (async/put! ch this)))
    ch))

(defn load-image! [game-state-atom k path]
  (c/load-image! (set-local-path path)
                 #(this-as this
                   (swap! game-state-atom assoc-in [:assets :images k] this))))

(defn cutscene-keys [ks]
  (vec (concat [:assets] ks)))

(defn load-cutscene-ex! [game-state-atom ks path]
  (c/load-image! path
                 #(this-as this
                    (swap! game-state-atom assoc-in (cutscene-keys ks) this))))


(defn load-cutscene-strings [dir count]
  (map (fn [image-name]
         (str "./assets/cutscenes/" dir "/" image-name ".png"))
       (range 0 (inc count))))

(defn load-cutscene-images [dir count]
  (doall (map #(loadImage! game-state-atom) (load-cutscene-strings dir count))))

(defn load-cutscene-images! [game-state-atom dir max]
  (doseq [i (range 0 max)]))

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
