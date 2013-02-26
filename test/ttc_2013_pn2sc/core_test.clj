(ns ttc-2013-pn2sc.core-test
  (:require [clojure.string :as str])
  (:use clojure.test funnyqt.emf funnyqt.protocols funnyqt.query
        ttc-2013-pn2sc.core
        [ttc-2013-pn2sc-validation.core :only [validate
                                               test-case-1-result-spec
                                               test-case-2-result-spec
                                               test-case-3-result-spec
                                               test-case-4-result-spec
                                               test-case-5-result-spec
                                               test-case-6-result-spec
                                               test-case-7-result-spec
                                               test-case-8-result-spec
                                               test-case-9-result-spec
                                               test-case-10-result-spec
                                               test-case-11-result-spec
                                               performance-test-cases]]))

(load-metamodel "metamodel/PetriNets.ecore")
(load-metamodel "metamodel/StateCharts.ecore")

(def normal-test-cases
  {1 test-case-1-result-spec
   2 test-case-2-result-spec
   3 test-case-3-result-spec
   4 test-case-4-result-spec
   5 test-case-5-result-spec
   6 test-case-6-result-spec
   7 test-case-7-result-spec
   8 test-case-8-result-spec
   9 test-case-9-result-spec
   10 test-case-10-result-spec
   11 test-case-11-result-spec})

(deftest test-init-and-reduction
  (println)
  (println "Initialization & Reduction")
  (println "==========================")
  (doseq [f ["models/testcase1-in.petrinet"
             "models/testcase2-in.petrinet"
             "models/testcase3-in.petrinet"
             "models/testcase4-in.petrinet"
             "models/testcase5-in.petrinet"
             "models/testcase6-in.petrinet"
             "models/testcase7-in.petrinet"
             "models/testcase8-in.petrinet"
             "models/testcase9-in.petrinet"
             "models/testcase10-in.petrinet"
             "models/testcase11-in.petrinet"
             "models/performance/sp200-pvg.petrinet"
             "models/performance/sp300-pvg.petrinet"
             "models/performance/sp400-pvg.petrinet"
             "models/performance/sp500-pvg.petrinet"
             "models/performance/sp1000-pvg.petrinet"
             "models/performance/sp2000-pvg.petrinet"
             "models/performance/sp3000-pvg.petrinet"
             "models/performance/sp4000-pvg.petrinet"
             "models/performance/sp5000-pvg.petrinet"
             "models/performance/sp10000-pvg.petrinet"
             "models/performance/sp20000-pvg.petrinet"
             "models/performance/sp40000-pvg.petrinet"
             "models/performance/sp80000-pvg.petrinet"
             "models/performance/sp100000-pvg.petrinet"
             "models/performance/sp200000-pvg.petrinet"]]
    (System/gc)
    (println "==========================================================================")
    (println "Performance Test of Model:" f)
    (print "Load Time: ")
    (let [m (time (load-model f))
          num (Integer/parseInt (second (re-matches #"\D*(\d+)\D*" f)))]
      (print "Transformation Time: ")
      (let [sc (time (create-statechart m))]
        (if (re-matches #".*/performance/.*" f)
          (do
            (validate sc (performance-test-cases num))
            (save-model sc (str "results/"
                                (str/replace (str/replace f ".petrinet" ".statechart")
                                             "models/performance/" ""))))
          (let [of (str "results/"
                        (str/replace (str/replace f ".petrinet" ".")
                                     "models/" ""))]
            (validate sc (normal-test-cases num))
            (print-model sc (str of "pdf"))
            (save-model sc (str of "statechart")))))))
  (println))
