(ns jjgrep.core-test
  (:require [clojure.test :refer :all]
            [jjgrep.core :refer :all]))

(def json {"foo" {"bar" "foo-bar-1"} "bar" 1 "baz" [1 2 3 4]})

(deftest lookup-value-test
  (testing "find shallow value"
    (is (= (lookup-value ["bar"] json) 1)))
  (testing "find deep value"
    (is (= (lookup-value ["foo" "bar"] json) "foo-bar-1")))
  (testing "find no shallow value"
    (is (= (lookup-value ["boop"] json) nil)))
  (testing "find no deep value"
    (is (= (lookup-value ["foo" "boop"] json) nil)))
  (testing "find complex value"
    (is (= (lookup-value ["baz"] json) [1 2 3 4]))))

(deftest split-location-string-test
  (testing "split direct location"
    (is (= (split-location-string "foo") ["foo"])))
  (testing "split nested location"
    (is (= (split-location-string "foo.bar.baz") ["foo" "bar" "baz"]))))

(deftest evaluate-equality-test
  (testing "= positive"
    (is (evaluate ["=", "bar" 1] json)))
  (testing "= negative"
    (is (not (evaluate ["=", "bar" 2] json))))
  (testing "> positive"
    (is (evaluate [">", "bar" 0] json)))
  (testing "> negative"
    (is (not (evaluate [">", "bar" 2] json))))
  (testing "< positive"
    (is (evaluate ["<", "bar" 2] json)))
  (testing "< negative"
    (is (not (evaluate ["<", "bar" 0] json))))
  (testing ">= positive"
    (is (evaluate [">=", "bar" 1] json)))
  (testing ">= negative"
    (is (not (evaluate [">=", "bar" 2] json))))
  (testing "<= positive"
    (is (evaluate ["<=", "bar" 2] json)))
  (testing "<= negative"
    (is (not (evaluate ["<=", "bar" 0] json))))
  (testing "!= positive"
    (is (evaluate ["!=", "bar" 3] json)))
  (testing "!= negative"
    (is (not (evaluate ["!=", "bar" 1] json)))))

; The into function is O(n) which sucks. Don't do this IRL
(deftest evaluate-logical-connectives
  (def TT1 [["=" "foo.bar" "foo-bar-1"] ["=" "bar" 1]])
  (def TT2 [["=" "foo.bar" "foo-bar-2"] ["=" "bar" 1]])
  (def TT3 [["=" "foo.bar" "foo-bar-1"] ["=" "bar" 2]])
  (def TT4 [["=" "foo.bar" "foo-bar-2"] ["=" "bar" 2]])
  (testing "and"
    (is (evaluate (into ["and"] TT1) json))
    (is (not (evaluate (into ["and"] TT2) json)))
    (is (not (evaluate (into ["and"] TT3) json)))
    (is (not (evaluate (into ["and"] TT4) json))))
  (testing "or"
    (is (evaluate (into ["or"] TT1) json))
    (is (evaluate (into ["or"] TT2) json))
    (is (evaluate (into ["or"] TT3) json))
    (is (not (evaluate (into ["or"] TT4) json)))))
