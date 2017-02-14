(ns dlambda.parser
  (:require [instaparse.core :as insta]))

(def transform-options
  {:NAME keyword
   :VAL read-string
   })

(def addition
  (insta/parser
   "plus = plus <'+'> plus | num
    num = #'[0-9]'+"))

(def addition-right
  (insta/parser
   "plus = num <'+'> plus | num
    num = #'[0-9]'+"))

(def addition-left
  (insta/parser
   "plus = plus <'+'> num | num
    num = #'[0-9]'+"))

(def parser
  (insta/parser
   "<program> = lparen expr rparen / expr
    <expr> = sfun / sname / sval / sapp
    APP = lparen sfun rparen program / sapp program / program program
    <sapp> = space APP
    FUN = <'fn'> sname space <'.'> program
    <sfun> = space FUN
    NAME = #'[a-zA-Z]'
    <sname> = space NAME
    VAL = #'[0-9]+'
    <sval> = space VAL
    <lparen> = space <'('>
    <rparen> = <')'> space
    <space> = <#'[ ]*'>
"))

(def oldparser
  (insta/parser
   "
    <program> = lparen sexpr rparen | sexpr
    <lparen> = space <'('>
    <rparen> = <')'> space
    <expr> = FUN / APP / NAME / VAL
    <sexpr> = space expr | lparen expr rparen
    APP = sexpr sexpr | lparen sexpr sexpr rparen
    <sapp> = space APP
    FUN = <'fn'> sname space <'.'> sexpr
    <sfun> = space FUN | lparen FUN rparen
    NAME = #'[a-zA-Z]'
    <sname> = space NAME | lparen NAME rparen
    VAL = #'[0-9]+'
    <sval> = space VAL | lparen VAL rparen
    <space> = <#'[ ]*'>
   "))

;; applications are left associative
;; applications have higher precedence over abstractions

;; correct applications:
;; a b
;; a 3
;; 3 a
;; app app
;; (fun ) app

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))

;; (defn parse [input]
;;   (insta/parse parser input))

(defn visual [in] (insta/visualize (parser in)))


(insta/parses parser "abc")
(parse "abcde")
(parse "(fn x . xyz)")

(insta/parses parser "abc" :trace true)

(visual "(fn a . a3)(fn s. (fn z . z))")

;;(visual "(fn f . fn x . (fx)) fn y . y")

(parse "fn f . f x")

(parse "((fnx.fny.x)y)z")

(parse "abc")
(parse "a(b)c")

(insta/parse parser "abc" :trace true)

;; (visual "(fn n . fn a . fn b . a (n a b))3")

;; (insta/parses parser "abcde")

;; (interpr "(fna.(fnb.b)(fnc.c)3)")

;; (interpr "(fnb.(fns.s)a)")

;; (interpr "(fnx.(fny.y))3")

;; (interpr "(fnx.x)(fny.y)3")

;; (interpr "(fnx.(fny.y))3")

;; (dreduce [:VAL "3"])
;; (dreduce [:NAME :x])
;; (dreduce [:APP [:FUN [:NAME "x"] [:NAME "x"]] 3])
;; (dreduce [:APP [:APP [:FUN [:NAME "x"] [:NAME "x"]] [:NAME "y"]] [:VAL "3"]])
;; (dreduce [:APP [:NAME "x"] [:VAL "3"]])

;; @TODO
;; Optional parentheses
;; Multiple args syntax
