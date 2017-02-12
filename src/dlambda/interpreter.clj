(ns dlambda.interpreter
  (:require [clojure.core.match :refer [match]]
            [dlambda.parser :refer [parse]]))


(defn isFree [y N]
  (= y N)) ;; Is this enough? Probably not...

(defn nextK [id]
  (keyword (str (name id) "'"))) ;; won't work for more complex cases!

(defn alphareduce [M x z]
  (match M
         x z
         y y
         [:FUN x e] [:FUN x e]
         [:FUN y e] [:FUN y (alpha e x z)]
         [:APP e1 e2] [:APP (alpha e1 x z) (alpha e2 x z)]
         ))

(defn betareduce [M x N]
  (match M
         x N
         [:FUN x _] M
         [:FUN y e] (if (isFree y N)
                      [:FUN (nextK y) (betareduce (alphareduce e y (nextK y)) x N)]
                      [:FUN y (betareduce e x N)])
         [:APP e1 e2] [:APP (betareduce e1 x N) (betareduce e2 x N)]
         :else M))

(defn dreduce [expr]
  (match expr
         ;;  [:VAL x] (read-string x)
         ;;  [:NAME x] (keyword x)
         [:FUN x ([:APP y z] :as w)] [:FUN x (dreduce w)]
         [:APP [:NAME x] [:NAME y]] [:APP [:NAME x] [:NAME y]]
         [:APP [:NAME x] [:VAL y]] [:VAL y]
         [:APP [:FUN x M] N] (dreduce [:APPFUN M x N])
         [:APPFUN M x N] (betareduce M x N)
         [:APP e N] (dreduce [:APP (dreduce e) N])
         :else expr))

(defn interpr [x] (dreduce (first (parse x))))

(interpr "(fn n . (fn )")

(parse "(fn n . (fn a . (fn b . a (n a b)))")

(interpr "((fnx.fny.x)y)z")

(interpr "(fna.(fnb.b)(fnc.c)3)")
(interpr "(fnn.(fna.(fnb.a(nab)))(fns.(fnz.z)))")
