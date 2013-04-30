(ns stencil.emitters.sequitur
  "Infrastructure of building a compiler out of lists.
   The general assumption is that the first item in a list identifies its 'type'
   and is therefore suitable to distinguish most selection-based routines.
   By convention, items that end in a * apply to just a list, and ** apply to a tree.")

(defn- in-tagged-list [op test condition policies]
  (op #(and (seq? %) (test (first %) condition)) policies))

(defn remove*
  ([condition policies] (in-tagged-list remove = condition policies))
  ([test condition policies] (in-tagged-list remove test condition policies)))

(defn find*
  ([condition policies] (in-tagged-list filter = condition policies))
  ([test condition policies] (in-tagged-list filter test condition policies)))


(defn find** [tag program] 
  (if (or (not (seq? program)) (empty? program)) 
    nil
    (concat (find* tag program) (mapcat (partial find** tag) program))))

(defn remove** [tag program] 
  (if (or (not (seq? program)) (empty? program)) 
    program 
    (remove* tag 
              (map (partial remove** tag) program))))

(defn- just-one [search action & crit]
  (let [items (search)]
    (if (> (count items) 1)
      (throw (RuntimeException. (str "More than one item matches search criteria: " (first crit))))
      (action))))

(defn select* [tag program] (just-one #(find tag program) #(first (find tag program)) tag))
(defn select** [tag program] (just-one #(find** tag program) #(first (find** tag program)) tag))

(defn update* [tags updater program]
  "Remove all old instances of an tagged items, and generate new ones from the old ones.
   Does not preserve relative order of items."
  (concat (remove* tags program)
          (map updater (find* tags program))))
