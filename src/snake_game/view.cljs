(ns snake-game.view
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :refer [subscribe dispatch]]
            [snake-game.utils :as utils]))

(defn render-board
  "Renders game board area with snake and point to catch"
  []
  (let [board (subscribe [:board])
        snake (subscribe [:snake])
        point (subscribe [:point])]
    (fn []
      (let [[width height] @board
            snake-positions (utils/positions @snake)
            current-point @point
            cells (for [y (range height)]
                    (into [:tr]
                          (for [x (range width)
                                :let [current-pos [x y]]]
                            (cond
                              (snake-positions current-pos) [:td.snake-on-cell]
                              (= current-pos current-point) [:td.point]
                              :else [:td.cell]))))]
        (into [:table.stage {:style {:height 377
                                     :width 527}}]
              cells)))))

(defn game-over
  "Renders game over overlay if game is finished"
  []
  (let [game-state (subscribe [:game])]
    (fn []
      (if @game-state
        [:div]
        [:div.overlay
         [:div.play {:on-click #(dispatch [:initialize])}
          [:h1 "↺" ]]]))))

(defn score
  "Renders player's score"
  []
  (let [points (subscribe [:points])]
    (fn []
      [:div.score (str "Score: " @points)])))

(defn game
  "Main rendering function"
  []
  [:div
   [game-over]
   [score]
   [render-board]])
