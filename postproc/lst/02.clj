(defn and-rule [pn sc prep-or-postp place2or]
  (loop [ts (eallobjects pn 'Transition), applied false]
    (if (seq ts)
      (let [t (first ts), preps-or-postps (prep-or-postp t)]
        (if (> (count preps-or-postps) 1)
          (let [p (first preps-or-postps), prets (pret p), postts (postt p)]
            (if (forall? #(and (= prets  (pret %))
                               (= postts (postt %)))
                         (rest preps-or-postps))
              (let [new-or  (ecreate! sc 'OR), new-and (ecreate! sc 'AND)]
                (eset! new-and :contains (mapv @place2or preps-or-postps))
                (eadd! new-or  :contains new-and)
                (swap! place2or assoc p new-or)
                (doseq [op (rest preps-or-postps)]
                  (edelete! op))
                (recur (rest ts) true))
              (recur (rest ts) applied)))
          (recur (rest ts) applied)))
      applied)))
