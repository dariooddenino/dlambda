(ns dlambda.core
  (:require [dlambda.parser :as parser])
  (:gen-class))

(defn repl []
  (do
    (print "dLambda> ")
    (flush))
  (let [input (read-line)]
    (println (parser/parse input))
    (recur)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello dLambda!")
  (flush)
  (repl))
