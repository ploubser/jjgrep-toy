(ns jjgrep.core
  (:use [clojure.string :as string :only (split join)])
  (:require [clojure.data.json :as json]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.cli :refer [cli]])
  (:import java.io.BufferedReader)
  (:gen-class))

(defn lookup-value
  "Look up the value in the json structure"
  [tokens
   json-value]
  (if-not (first tokens)
     json-value
     (if (contains? json-value (first tokens))
       (recur (rest tokens) (json-value (first tokens)))
       nil)))

(defn split-location-string
  "Split the string describing the location in the json doc into a vector"
  [loc-string]
  (string/split loc-string #"\."))

(defn evaluate
  "Evaluate a jgrep sentance"
  [sentance
   json]
  (case (get sentance 0)
    "=" (= (lookup-value (split-location-string (get sentance 1)) json) 
           (get sentance 2))
    ">" (> (lookup-value (split-location-string (get sentance 1)) json) 
           (get sentance 2))
    "<" (< (lookup-value (split-location-string (get sentance 1)) json) 
           (get sentance 2))
    ">=" (>= (lookup-value (split-location-string (get sentance 1)) json) 
           (get sentance 2))
    "<=" (<= (lookup-value (split-location-string (get sentance 1)) json) 
           (get sentance 2))
    "!=" (not= (lookup-value (split-location-string (get sentance 1)) json) 
           (get sentance 2))
    "and" (and (evaluate (get sentance 1) json) 
               (evaluate (get sentance 2) json))
    "or" (or (evaluate (get sentance 1) json) 
             (evaluate (get sentance 2) json))))

(defn jgrep
  "Run the jgrep"
  [query
   json-doc]
  (if (evaluate query json-doc)
    (pprint json-doc)
    (pprint [])))

(defn -main
  "jjgrep - for kids!"
  [& args]
  (let [[opts args banner] 
        (cli args
             ["-h" "--help" "Print this help"
              :default false :flag true]
             ["-s" "--start" "Start at another point in the json document"])]
    (when (:help opts)
      (println banner)
      (System/exit 0))
    (def json-string (json/read *in*))
    (when (:start opts)
      (def json-string (lookup-value (split-location-string (opts :start)) json-string)))
    (jgrep (load-string (first args)) json-string)))
