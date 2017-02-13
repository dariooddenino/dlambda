(ns dlambda.parser
  (:require [instaparse.core :as insta]))

;; (def transform-options
;;   {:NAME keyword
;;    :VAL read-string
;;    })

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

;; (def oldparser
;;   (insta/parser
;;    "<program> = lparen program rparen | program
;;     <lparen> = space <'('>
;;     <rparen> = <')'> space
;;     <expr> = sfun / sname / sval
;;     <sexpr> = space expr
;;     APP = program program
;;     <sapp> = space APP
;;     FUN = <'fn'> sname space <'.'> program
;;     <sfun> = space FUN
;;     NAME = #'[a-zA-Z]'
;;     <sname> = space NAME
;;     VAL = #'[0-9]+'
;;     <sval> = space VAL
;;     <space> = <#'[ ]*'>
;; "))

(def parser
  (insta/parser
   "
    <program> = lparen sexpr rparen | sexpr
    <lparen> = space <'('>
    <rparen> = <')'> space
    <expr> = FUN / NAME / VAL / APP
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

;; (defn parse [input]
;;   (->> (parser input) (insta/transform transform-options)))

(defn parse [input]
  (insta/parse parser input))

(defn visual [in] (insta/visualize (parser in)))


(insta/parses parser "123")
(parse "( abcde)")
(parse "((fn x . x)3)")

(insta/parses parser "abc" :trace true)

(parse "(fn f . fn x . fx) fn y . y")

(parse "((fnx.fny.x)y)z")

(parse "abc")
(parse "a(b)c")

(insta/parse parser "abc" :trace true)

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
