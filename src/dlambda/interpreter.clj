(ns dlambda.interpreter
  (:require [clojure.core.match :refer [match]]
            [dlambda.parser :refer [parse]]))


(defn isFree [y N]
  (= y N)) ;; Is this enough? Probably not...

(defn nextK [id]
  (keyword (str (name (second id)) "'"))) ;; won't work for more complex cases!

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

(defn olddreduce [expr]
  (match expr
         ;;  [:VAL x] (read-string x)
         ;;  [:NAME x] (keyword x)
         [:FUN x ([:APP y z] :as w)] (dreduce [:FUN x (dreduce w)])
         [:FUN x [:VAL y]] [:VAL y]
         [:FUN x [:NAME y]] [:NAME y]
         [:FUN x y] [:FUN x (dreduce y)]
         [:APP [:NAME x] [:NAME y]] [:APP [:NAME x] [:NAME y]]
         [:APP [:NAME x] [:VAL y]] [:VAL y]
         [:APP [:FUN x M] N] (let [res (betareduce M x N)] (dreduce res)) ;; (dreduce (betareduce M x N))
  ;;       [:APPFUN M x N] (betareduce M x N)
         [:APP e N] (dreduce [:APP (dreduce e) N])
         [:APP x] (dreduce x)
         :else expr))

(defn dreduce [expr]
  (match expr
       ;;  [:FUN _ [:VAL x]] [:VAL x]
       ;;  [:FUN _ [:NAME x]] [:NAME x]
         [:FUN x y] [:FUN x (dreduce y)]
         [:APP [:FUN x M] N] (dreduce [:APPFUN M x N])
         [:APP ([:APP _ _] :as a) e] (dreduce [:APP (dreduce a) e])
         [:APP e ([:APP _ _] :as a)] (dreduce [:APP e (dreduce a)])
         [:APPFUN M x N] (dreduce (betareduce M x N))
         :else expr))


(defn interpr [x] (dreduce (first (parse x))))

(interpr "(fn n . (fn b .a))")

;; (interpr "(fn n . (fn a . (fn b . a (n a b))))3")

(interpr "((fnx.fny.x)y)z")

(interpr "(fna.a)3")

(interpr "fna.b")

(interpr "fn a . ((fn b . b) 3)")

(interpr "((fn s . (fn z . z))3)2")

(interpr "(fn a . a3)(fn s . (fn z . z))")

(interpr "(fna.(fnb.b)(fnc.c)3)")
;; get 1 from 0
(interpr "(fnn.(fna.(fnb.a((na)b))))(fns.(fnz.z))")
;; get 2 from 1
(interpr "(fnn.(fna.(fnb.a((na)b))))(fns.(fnz.sz))")
i
