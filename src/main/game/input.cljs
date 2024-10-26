(ns game.input)

(def valid-keys #{:w :s :a :d :j :o :p})

(defn check [game-state k]
  (k (:input game-state)))

(defn talk-key? [state]
  (check state :j))


(defn move-up [game-state]
  (:w (:input game-state)))

(defn move-down [game-state]
  (:s (:input game-state)))

(defn move-left [game-state]
  (:a (:input game-state)))

(defn move-right [game-state]
  (:d (:input game-state)))
