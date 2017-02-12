(ns dlambda.parser-tests
  (:require [dlambda.parser :refer [parse]])
  (:use [clojure.test]))

(deftest simple-values
  (is (= '(3) (parse "3")))
  (is (= '(:y) (parse "y")))
  )

(deftest simple-fns
  (let [simple-fn '([:FUN :x :x])]
    (is (= simple-fn (parse "fn x. x")))
    (is (= simple-fn (parse "fnx.x")))
    (is (= simple-fn (parse "( fn  x .x)")))
    (is (= simple-fn (parse "((fnx.x))")))
    ))

(deftest simple-apply
  (let [res '([:APP [:FUN :x :x] 3])]
    (is (= res (parse "(fnx.x)3")))
    (is (= res (parse "((fnx.x)3)")))
    ))

(deftest complex-expression
  (let [res '([:APP [:FUN :f [:FUN :x [:APP :f :x]]] [:FUN :y :y]])]
    (is (= res (parse "(fnf.fnx.fx)fny.y")))
    ))
