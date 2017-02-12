(ns dlambda.parser
  (:require [instaparse.core :as insta]))

(def transform-options
  {:NAME keyword
   :VAL read-string
   })

(def parser
  (insta/parser
   "<program> = lparen program rparen | sexpr
    <lparen> = <'('>
    <rparen> = <')'>
    <expr> = sfun / sapp / sname / sval
    <sexpr> = space expr
    APP = program program
    <sapp> = space APP
    FUN = <'fn'> sname space <'.'> program
    <sfun> = space FUN
    NAME = #'[a-zA-Z]'
    <sname> = space NAME
    VAL = #'[0-9]+'
    <sval> = space VAL
    <space> = <#'[ ]*'>
"))

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))

(defn visual [in] (insta/visualize (parser in)))

;; THIS ONE DOES NOT WORK! why?
(parse "(fn n . fn a. (fn b . a))")

(parse "((fn x . x)3)")

(insta/parses parser "((fnx.x)3)")

(parse "(fn f . fn x . fx)fn y . y")

(parse "((fnx.fny.x)y)z")

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
