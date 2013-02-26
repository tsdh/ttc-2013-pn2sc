(ns ttc-2013-pn2sc.init-test
  (:use clojure.test funnyqt.emf funnyqt.query ttc-2013-pn2sc.init))

(load-metamodel "metamodel/PetriNets.ecore")
(load-metamodel "metamodel/StateCharts.ecore")

(deftest test-init-transformation
  (println "Now running the initialization tests")
  (println "====================================")
  (doseq [file ["models/testcase1-in.petrinet"
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
                "models/performance/sp200000-pvg.petrinet"]
          :let [pn (load-model file)]]
    (System/gc)
    (println "==========================================================================")
    (println "Initializing Statechart for" file)
    (print "Transformation Time: ")
    (let [[sc place2or place2basic trans2hyperedge] (time (init-statechart pn))]
      (doseq [p (eallobjects pn 'Place)]
        (let [or (get place2or p)
              basic (get place2basic p)]
          (is or)
          (is basic)
          (is (= (eget p :name) (eget basic :name)))
          (is (= or (eget basic :rcontains)))))
      (doseq [t (eallobjects pn 'Transition)]
        (let [he (get trans2hyperedge t)]
          (is he)
          (is (= (eget t :name) (eget he :name)))
          ;; Connection constraints (without respecting the order)
          (is (= (set (eget he :rnext)) (set (map place2basic (eget t :prep)))))
          (is (= (set (eget he :next)) (set (map place2basic (eget t :postp))))))))))
