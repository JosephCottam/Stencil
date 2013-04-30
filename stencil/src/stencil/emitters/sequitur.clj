(ns stencil.emitters.sequitur
  "Infrastructure of building a compiler out of lists.
   The general assumption is that the first item in a list identifies its 'type'
   and is therefore suitable to distinguish most selection-based routines.
   By convention, items that end in a * apply to just a list, and ** apply to a tree.")

(defn any= [item coll]
  "item, coll->bool: Is the item in the collection?"
  (not (nil? (some (partial = item) coll))))

(defn- get-opt [opts tag default]
  "ls, a, fn -> fn
   Given a list of alternating tags and items (common for optional arguments)
   get the item following the passed tag.  Return the default if the tag is not found."
  (let [idx (if (empty? opts) -1 (.indexOf opts tag))]
   (if (> idx -1)
    (nth opts (+ idx 1))
    default)))

(defn- in-tagged-list [op test condition policies]
  "Operate on list of lists where the first item in each sub-list is
   assumed to be a tag." 
  (op #(and (seq? %) (test (first %) condition)) policies))


(defmulti remove* (fn [condition program] (seq? condition)))
(defmethod remove* false [tag program] (remove* (list tag) program))
(defmethod remove* true [tags program] (in-tagged-list remove any= tags program))

(defmulti find* (fn [condition program] (seq? condition)))
(defmethod find* false [tag program] (find* (list tag) program))
(defmethod find* true [tags program] (in-tagged-list filter any= tags program))

(defn find** [tag program] 
  "Find items from a tree based on item tags.
   Will return a list of the items.  
   Items that match are still searched for sub-items."
  (if (or (not (seq? program)) (empty? program)) 
    nil
    (concat (find* tag program) (mapcat (partial find** tag) program))))

(defn remove** [tag program] 
  "Remove items from a tree based on their tags.
   Returns the tree with items removed."
  (if (or (not (seq? program)) (empty? program)) 
    program 
    (map (partial remove** tag) (remove* tag program))))

(defn- just-one [search action & crit]
  (let [items (search)]
    (if (> (count items) 1)
      (throw (RuntimeException. (str "More than one item matches search criteria: " (first crit))))
      (action))))

(defn select* [tag program] (just-one #(find* tag program) #(first (find* tag program)) tag))
(defn select** [tag program] (just-one #(find** tag program) #(first (find** tag program)) tag))

(defn update* [tags program & opts]
  "Remove all old instances of an tagged items, and generate new ones from the old ones.
   Does not preserve relative order of items.
   Can specify two types of transformation that can be passed:
     :each -- Will be run on each instance that matches the tag(s)
     :all -- Will be run on the list of matching items (runs after each)"

  (let [all (get-opt opts :all identity)
        each (get-opt opts :each identity)]
   (concat (remove* tags program)
           (all (map each (find* tags program))))))

(defn update** [tags program each]
  "Replace old instances in a tree with new instances.
   Preserves placement."
  (let [tags (if (seq? tags) tags (list tags))]
    (cond
      (or (not (seq? program)) (empty? program)) program
      (any= (first program) tags) 
         (map #(update** tags % each) (each program))
      :else (map #(update** tags % each) program))))
    
