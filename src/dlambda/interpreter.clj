(ns dlambda.interpreter
  (:require [clojure.core.match :refer [match]]
            [dlambda.parser :refer [parse]]
            [clojure.walk :as walk])
  (:use [clojure.walk]))

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

(defn pop-dl-n [stack n]
  (loop [i n s stack t []]
    (if (= i 0)
      [s t]
      (recur (dec i) (pop s) (conj t (peek s))))))

(defn elab [op args]
  (let [rargs (vec (rseq args))]
    (case op
      :APP (match rargs
                  [[:FUN x M] N] (betareduce M x N)
                  :else (vec (concat [:APP] rargs)))
      (vec (concat [op] rargs)))))

(defn build-dl-traversal [tree]
  (loop [stack [tree] traversal []]
    (if (empty? stack)
      traversal
      (let [e (peek stack)
            s (pop stack)]
        (if (coll? e)
          (recur (into s (rest e))
                 (conj traversal {:op (first e) :count (count (rest e))}))
          (recur s (conj traversal {:arg e})))))))

(defn eval-dl-traversal [traversal]
  (loop [op-stack traversal arg-stack []]
    (if (empty? op-stack)
      (peek arg-stack)
      (let [o (peek op-stack)
            s (pop op-stack)]
        (if-let [a (:arg o)]
          (recur s (conj arg-stack a))
          (let [[args op-args] (pop-dl-n arg-stack (:count o))]
            (recur s (conj args (elab (:op o) op-args)))
            ))))))

(defn eval-dl-tree [tree] (-> tree build-dl-traversal eval-dl-traversal))

(defn dleval [expr]
  (let [tree (first (parse expr))]
    (loop [res (eval-dl-tree tree)]
      (let [res2 (eval-dl-tree res)]
        (if (= res res2)
          res
          (recur res2))))))

;; (dleval "(fn n . (fn b .a))")

;; (interpr "(fn n . (fn a . (fn b . a (n a b))))3")

;; (dleval "(fnx.fny.x)y") ;; I think the problem is with alpha reducing...

(dleval "(fna.a)3")

(dleval "fna.b")

(dleval "fn a . ((fn b . b) 3)")

(dleval "((fn s . (fn z . z))3)2")

(dleval "(fn a . a3)(fn s . (fn z . z))")

(dleval "(fna.(fnb.b)(fnc.c)3)")
;; get 1 from 0
(dleval "(fnn.(fna.(fnb.a((na)b))))(fns.(fnz.z))")
;; get 2 from 1
(dleval "(fnn.(fna.(fnb.a((na)b))))(fns.(fnz.sz))")
