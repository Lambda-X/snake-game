(ns snake-game.utils
  (:require [reagent.core :as reagent :refer [atom]]))

(def key-code->move
  "Mapping from integer key code to direction vector corresponding to that key"
  {38 [0 -1]
   40 [0 1]
   39 [1 0]
   37 [-1 0]})

(def positions (comp (partial into #{}) (partial map :position)))

(defn rand-free-position
  "Fuction takes snake and board-size as arguments.,
  and returns random position not colliding wit snake body"
  [snake [x y]]
  (let [snake-positions (positions snake)
        board-positions (for [x-pos (range x)
                              y-pos (range y)]
                          [x-pos y-pos])]
    (when-let [free-positions (seq (remove snake-positions board-positions))]
      (rand-nth free-positions))))

(defn collisions
  "Returns true if snake collision with board edges or itself (snake body) is detected"
  [{:keys [snake board] :as db}]
  (let [[{:keys [position direction]} & rest-of-snake] snake
        [x y] board
        border-x #{x -1}
        border-y #{y -1}
        future-x (+ (first direction) (first position))
        future-y (+ (second direction) (second position))]
    (or (contains? border-x future-x)
        (contains? border-y future-y)
        (contains? (positions rest-of-snake) [future-x future-y]))))

(defn change-snake-direction
  "Changes snake head direction, only when it's perpendicular to the old head direction"
  [[new-x new-y] [x y]]
  (if (or (= x new-x)
          (= y new-y))
    [x y]
    [new-x new-y]))

(defn move-snake
  "Move the whole snake based on positions and directions for each snake body segments"
  [snake]
  (let [snake-with-new-position (map (fn [{[x y] :position [a b] :direction}]
                                       {:position [(+ x a) (+ y b)] :direction [a b]})
                                     snake)]
    (into [] (conj (map (fn [{[x y] :position} [n-a n-b]]
                          {:position [x y] :direction [n-a n-b]})
                        (rest snake-with-new-position)
                        (map :direction snake-with-new-position))
                   (first snake-with-new-position)))))

(defn grow-snake
  "Append new tail body segment to snake"
  [snake]
  (let [{[x y] :position [d-x d-y] :direction} (last snake)]
    (conj snake {:position [(- x d-x) (- y d-y)]
                 :direction [d-x d-y]})))

(defn process-move
  "Evaluates new snake position in context of the whole game"
  [{:keys [snake point board] :as db}]
  (let [[{:keys [position]}] snake]
    (if (= point position)
      (-> db
          (update-in [:snake] grow-snake)
          (update-in [:points] inc)
          (assoc :point (rand-free-position snake board)))
      db)))
