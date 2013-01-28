;Count up. 
;   x -- start at zero, get to that value by inc
;   x trans step stop -- Start at x, increment using step, transform that result by trans
;      ends when applying stop returns false
(defn iota [x & rest]
  (if (== 0 (count rest))
      (iota 0 identity inc #(< % x))
      (let [[t nxt stop] rest]
       (take-while stop (iterate #(t (nxt %)) x)))))


(defn divide [size items] 
  (if (empty? items) 
      items
      (cons (take size items) (divide size (drop size items)))))
