(set-env!
 :source-paths #{"src"}
 :resource-paths #{"html"}
 :dependencies '[; Boot deps
                 [adzerk/boot-cljs        "1.7.170-1"]
                 [pandeiro/boot-http      "0.7.1-SNAPSHOT"]
                 [adzerk/boot-reload      "0.4.4"]
                 ;;repl
                 [adzerk/boot-cljs-repl   "0.3.0"]
                 [com.cemerick/piggieback "0.2.1"  :scope "test"]
                 [weasel                  "0.7.0"  :scope "test"]
                 [org.clojure/tools.nrepl "0.2.12" :scope "test"]
                 ;; App deps
                 [org.clojure/clojure       "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async    "0.2.374"]
                 [reagent                   "0.5.0"]
                 [re-frame                  "0.6.0"]])

(task-options!
 pom {:project "snake-game"
      :version "0.1.0-SNAPSHOT"})

(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-reload :refer [reload]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]])


(deftask dev []
  (set-env! :source-paths #{"src"})
  (comp (serve :dir "target/")
        (watch)
        (speak)
        (reload :on-jsload 'snake-game.core/run)
        (cljs-repl)
        (cljs :source-map true :optimizations :none)))


(deftask build []
  (set-env! :source-paths #{"src"})
  (comp (cljs :optimizations :advanced)))
