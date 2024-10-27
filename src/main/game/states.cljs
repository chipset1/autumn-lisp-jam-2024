(ns game.states)

(def valid-game-states #{:dialog-running :in-room :playing-cutscene})


(defn get-state [state-key]
  (let [s (valid-game-states state-key)]
    (if (nil? s)
      (throw (js/Error. "Invalid game-state"))
      s)))

(defn set-state [state state-key]
  (assoc state :game-state-key (get-state state-key)))

(defn eq [state state-key]
  (= (:game-state-key state) state-key))

(defn dialog-running? [state]
  (eq state (get-state :dialog-running)))

(defn playing-cutscene? [state]
  (eq state (get-state :playing-cutscene)))
