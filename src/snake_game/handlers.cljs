(ns snake-game.handlers
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-handler
                                   register-sub
                                   dispatch]]
            [goog.events :as events]
            [snake-game.utils :as utils]))

;; Define app data. We need to define our board, points and snake. In our snake vector is in map desctibed position :position of every 'snake body' part. First elemeny in vector id head of snake. Key :direction is the direction of the next move.

(def board [35 25])

(def snake {:direction [1 0]
            :body [[3 2] [2 2] [1 2] [0 2]]})

(def initial-state {:board board
                    :snake snake
                    :point (utils/rand-free-position snake board)
                    :points 0
                    :game true})

(register-handler                  ;; setup initial state
 :initialize                       ;; usage (submit [:initialize])
 (fn
   [db _]
   (merge db initial-state)))      ;; what it returns becomes the new @db state

(register-handler
 :next-state
 (fn
   [{:keys [snake board] :as db} _]
   (if (:game db)
     (if (utils/collisions snake board)
       (assoc-in db [:game] false)
       (-> db
           (update-in [:snake] utils/move-snake)
           (as-> after-move
               (utils/process-move after-move))))
     db)))

(register-handler
 :change-direction
 (fn [db [_ new-direction]]
   (update-in db [:snake :direction]
              (partial utils/change-snake-direction new-direction))))

;;Register global event listener for keydown event.
;;Processes key strokes according to `utils/key-code->move` mapping
(defonce key-handler
  (events/listen js/window "keydown"
                 (fn [e]
                   (let [key-code (.-keyCode e)]
                     (when (contains? utils/key-code->move key-code)
                       (dispatch [:change-direction (utils/key-code->move key-code)]))))))

;; ---- Subscription Handlers ----

(register-sub
 :board
 (fn
   [db _]                         ;; db is the app-db atom
   (reaction (:board @db))))      ;; wrap the computation in a reaction

(register-sub
 :snake
 (fn
   [db _]
   (reaction (:body (:snake @db)))))

(register-sub
 :point
 (fn
   [db _]
   (reaction (:point @db))))

(register-sub
 :points
 (fn
   [db _]
   (reaction (:points @db))))

(register-sub
 :game
 (fn
   [db _]
   (reaction (:game @db))))
