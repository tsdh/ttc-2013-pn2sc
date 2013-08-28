(ns ttc-2013-pn2sc.core
  (:use funnyqt.protocols funnyqt.emf funnyqt.query funnyqt.query.emf
        [funnyqt.in-place :only [apply-repeatedly]])
  (:require [ttc-2013-pn2sc.init :as init]))

(defn refs-as-set [ref elem]
  (set (eget-raw elem ref)))

(def postt (partial refs-as-set :postt))
(def pret  (partial refs-as-set :pret))
(def postp (partial refs-as-set :postp))
(def prep  (partial refs-as-set :prep))

(defn and-rule [pn sc prep-or-postp place2or]
  (loop [ts (eallobjects pn 'Transition), applied false]
    (if (seq ts)
      (let [t (first ts), preps-or-postps (prep-or-postp t)]
        (if (> (count preps-or-postps) 1)
          (let [p (first preps-or-postps), prets (pret p), postts (postt p)]
            (if (forall? #(and (= prets  (pret %))
                               (= postts (postt %)))
                         (rest preps-or-postps))
              (let [new-and (ecreate! nil 'AND
                                      :contains (mapv @place2or preps-or-postps))
                    new-or  (ecreate! sc 'OR
                                      :contains [new-and])]
                (swap! place2or assoc p new-or)
                (doseq [op (rest preps-or-postps)]
                  (edelete! op))
                (recur (rest ts) true))
              (recur (rest ts) applied)))
          (recur (rest ts) applied)))
      applied)))

(defn or-rule [pn sc place2or]
  (loop [ts (vec (eallobjects pn 'Transition)), applied false]
    (if (seq ts)
      (let [t (first ts), preps (prep t), postps (postp t)]
        (if (= 1 (count preps) (count postps))
          (let [q (first preps), r (first postps)]
            (if (or (identical? q r)
                    (and (not (member? r (adjs q :pret :postp)))
                         (not (member? r (adjs q :postt :prep)))))
              (let [merger (@place2or q), mergee (@place2or r)]
                (when-not (identical? q r)
                  (eaddall! q :pret  (eget-raw r :pret))
                  (eaddall! q :postt (eget-raw r :postt))
                  (edelete! r)
                  (eaddall! merger :contains (eget-raw mergee :contains))
                  (edelete! mergee))
                (edelete! t)
                (recur (rest ts) true))
              (recur (rest ts) applied)))
          (recur (rest ts) applied)))
      applied)))

(defn assign-hyperedges [sc]
  (doseq [e (eallobjects sc 'HyperEdge)]
    (eset! e :rcontains
           (first (apply clojure.set/intersection
                         (map #(reachables % [p-+ --<>])
                              (concat (eget e :next) (eget e :rnext))))))))

(defn create-top [sc]
  (let [top-ors (filter #(not (eget % :rcontains)) (eallobjects sc 'OR))]
    (when (= 1 (count top-ors))
      (let [top (ecreate! nil 'AND :contains top-ors)
            statechart (ecreate! sc 'Statechart :topState top)]
        (eadd! sc statechart)))))

(defn create-statechart [pn]
  (let [[sc place2or _ _] (init/init-statechart pn)
        place2or (atom place2or)]
    (apply-repeatedly (fn []
                        (let [r     (and-rule pn sc prep  place2or)
                              r (or (and-rule pn sc postp place2or) r)
                              r (or (or-rule  pn sc place2or)       r)]
                          r)))
    (create-top sc)
    (assign-hyperedges sc)
    sc))

