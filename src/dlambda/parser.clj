(ns dlambda.parser
  (:require [instaparse.core :as insta]
            [clojure.core.match :refer [match]]))

(insta/set-default-output-format! :hiccup)

(def parser
  (insta/parser
   "<EXPR> = APP / FUN / NAME / VAL
    FUN = <'('> <'fn'> NAME <'.'> EXPR <')'> | <'fn'> NAME <'.'> EXPR
    APP = EXPR EXPR
    NAME = #'[a-zA-Z]'
    VAL = #'[0-9]+'
"))

(defn isFree [y N]
  (= y N)) ;; Is this enough? Probably not...

(defn nextk [id]
  (keyword (str (name id) "'")))

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


(def transform-options
  {;;:NAME keyword
   ;;:VAL read-string
   })

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))

(defn visual [in] (insta/visualize (parser in)))

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

(interpr "(fnn.(fna.(fnb.anab)))(fns.(fnz.z))")

(interpr "(fnb.(fns.s)a)")

(interpr "(fnx.(fny.y))3")

(interpr "(fnx.x)(fny.y)3")

(interpr "(fnx.(fny.y))3")

(dreduce [:VAL "3"])
(dreduce [:NAME :x])
(dreduce [:APP [:FUN [:NAME "x"] [:NAME "x"]] 3])
(dreduce [:APP [:APP [:FUN [:NAME "x"] [:NAME "x"]] [:NAME "y"]] [:VAL "3"]])
(dreduce [:APP [:NAME "x"] [:VAL "3"]])

;; @TODO
;; Optional parentheses
;; Multiple args syntax
