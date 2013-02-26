(ns ttc-2013-pn2sc.init
  (:use funnyqt.protocols funnyqt.emf funnyqt.declarative))

(deftransformation initialize-statechart [[pn :emf] [sc :emf]]
  (^:top place2basic-and-or [p]
         :from 'Place
         :to [o 'OR, b 'Basic]
         (eset! b :name (eget p :name))
         (eset! b :rcontains o)
         (eset! b :rnext (map transition2hyperedge (eget p :pret)))
         (eset! b :next  (map transition2hyperedge (eget p :postt))))
  (transition2hyperedge [t]
         :from 'Transition
         :to [he 'HyperEdge]
         (eset! he :name (eget t :name))))

(defn init-statechart [pn]
  (let [sc (new-model)
        trace (initialize-statechart pn sc)]
    [sc
     (apply hash-map (mapcat (fn [[p [o b]]] [p o])
                             (:place2basic-and-or trace)))
     (apply hash-map (mapcat (fn [[p [o b]]] [p b])
                             (:place2basic-and-or trace)))
     (:transition2hyperedge trace)]))

