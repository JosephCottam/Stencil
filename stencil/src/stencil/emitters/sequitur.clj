(ns stencil.emitters.sequitur)

(defn- in-tagged-list [op test condition policies]
  (op #(and (seq? %) (test (first %) condition)) policies))

(defn remove*
  ([condition policies] (in-tagged-list remove = condition policies))
  ([test condition policies] (in-tagged-list remove test condition policies)))

(defn find*
  ([condition policies] (in-tagged-list filter = condition policies))
  ([test condition policies] (in-tagged-list filter test condition policies)))


(defn find** [tag ls] 
  (if (or (not (seq? ls)) (empty? ls)) 
    nil
    (concat (find* tag ls) (mapcat (partial find* tag) ls))))

(defn remove** [tag ls] 
  (if (or (not (seq? ls)) (empty? ls)) 
    ls 
    (remove* tag 
              (map (partial remove** tag) ls))))

(defn- just-one [search action & crit]
  (let [items (search)]
    (if (> (count items) 1)
      (throw (RuntimeException. (str "more than one item matches search criteria: " (first crit))))
      (action))))

(defn select* [tag ls] (just-one #(find tag ls) #(first (find tag ls)) tag))
(defn select** [tag ls] (just-one #(find* tag ls) #(first find* tag ls) tag))

