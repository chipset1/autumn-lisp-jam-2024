(ns game.vector)

(defn x [v]
  (first v))

(defn y [v]
  (second v))

(defn set-x [v new-x]
  (assoc v 0 new-x))

(defn set-y [v new-y]
  (assoc v 1 new-y))

(defn mag [v]
  (js/Math.sqrt (+ (* (x v)
                      (x v))
                   (* (y v)
                      (y v)))))

(defn add [v1 v2]
  [(+ (x v1)
      (x v2))
   (+ (y v1)
      (y v2))])

(defn sub [v1 v2]
  [(- (x v1)
      (x v2))
   (- (y v1)
      (y v2))])

(defn mult-vec [v1 v2]
  [(* (x v1)
      (x v2))
   (* (y v1)
      (y v2))])

(defn mult [v1 s]
  [(* s
      (x v1))
   (* s
      (y v1))])

(defn div [v1 s]
  [(/ s
      (x v1))
   (/ s
      (y v1))])

(defn normalize
  [v]
  (let [m (mag v)]
    (if (not= m 0)
      (mult v (/ 1 m))
      v)))

(defn mag-sq
  [v]
  (+ (* (x v) (x v))
     (* (y v) (y v))))

(defn limit
  [max v ]
  (if (> (mag-sq v)
         (* max max))
    (mult max (normalize v))
    v))

(defn dist [v1 v2]
  (mag (sub v1 v2)))

;; (defn dist
;;   [v1 v2]
;;   (q/dist (x v1) (y v1)
;;           (x v2) (y v2)))
