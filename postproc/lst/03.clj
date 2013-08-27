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
