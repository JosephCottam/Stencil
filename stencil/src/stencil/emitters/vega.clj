(ns stencil.emitters.vega
  (:use [stencil.util :exclude [any=]])
  (:use [stencil.emitters.sequitur])
  (:use [stencil.transform :only [atom? full-drop lop->map]])
  (:require [clojure.data.json :as json])
  (:require [stencil.pprint])) 

(defn ptuples->lop [ptuple]
  (let [[tag fields & items] (remove-metas ptuple)
        fields (rest fields)
        items (map rest items)]
    (partition (count fields) (partition 2 (interleave (cycle fields) (apply interleave items))))))

(defn ptuple->lop [[tag fields & vals]] (map list (rest fields) vals))
(defn pair? [item] (and (seq? item) (== 2 (count item))))
(defn lop? [item] (and (seq? item) (every? pair? item)
                       (every? atom? (map first item))))


(defn distinguish-unrendered-tables [program]
  "Mark tables that will not be rendered as 'data-table'
  (simplifies later steps)."
  (letfn [(change-tag [table tag] (cons tag (rest table)))
          (retag [targets table]
            (if (any= (nth table 2) targets)
              (change-tag table 'render-table)
              (change-tag table 'data-table)))]
    (let [renders (find* 'render program)
          targets (map #(nth % 4) renders)]
      (update* 'table program :each (partial retag targets)))))


(defn transform-unrendered-tables [program]
  (letfn [(transform-data [table] 
            (let [data (find* 'data table)
                  using (select** 'using data)
                  [tag _ fields _ load] using 
                  loader (first load)]
              (case loader
                'ptuples (ptuples->lop load) 
                :else (throw (RuntimeException. (str "Loader not know: " loader))))))
          (transform [table]
            (let [data (transform-data table)
                  [_ _ name & rest] table]
              (list (list 'name name) (list 'values data))))]
    (update* 'data-table program :each transform :all #(list (list 'data %)))))

(defn remove-unused-renders [program]
  (letfn [(render-filter [used] (fn [[tag _ name & rest]] (any= name used)))]
    (let [view (select* 'view program)
          renders (find* 'render program)
          used (drop 2 (remove-metas view))]
      (update* 'render program :all (partial filter (render-filter used))))))	
      
(defn transform-action [action]
  (letfn [(remove-do [action] (if (and (seq? action) (= 'do (first action))) (last action) action))
          (field-or-val [item] 
            (cond 
              (symbol? item) (list 'field (symbol (str "data." item)))
              (atom? item)   (list 'value item)
              :else item))
          (scale-or-not [item]
            (if (symbol? item) 
              (list 'scale item)
              item))
          (tag-atoms [action] 
            (if (seq? action)
              (map field-or-val action)
              (field-or-val action)))
          (tag-scales [action] 
            (if (seq? action)
              (list* (scale-or-not (first action)) (rest action))
              action))
          (ptuple->lop* [action] 
            (update* 'ptuple action :each ptuple->lop :all #(apply concat %)))]
    (-> action remove-do tag-scales tag-atoms ptuple->lop*)))

(defn simplify-renders [program]
  (letfn [(transform [render]
            (let [[tag m0 name m1 old-target _ type _ & policies] render
                  bindings (select** 'bind policies)  ;;TODO Filter the data-bindings to just the items listed in the regular bind statement
                  data (select** 'data policies)
                  [tag _ fields gen trans] (select** 'using data)
                  [_ _ source _] gen
                  [_ _ data-binds _] trans 
                  data-actions (map transform-action (remove-metas (map second data-binds)))
                  data-binds (remove-metas (map ffirst data-binds))
                  binds (map list data-binds data-actions)]
              (concat (remove* 'data render)
                     (list (list 'type type) 
                           (list 'from (list 'data source)) 
                           (list 'properties (list 'enter binds))))))]
    (update* 'render program :each transform)))

(defn renders->marks [program]
  "Gathers up the renders, strips away the extras and puts it into a 'marks' list."
  (letfn [(clean [render]
          (let [[tag _ name _ target _ type _ & policies] render]
            (remove* 'bind policies)))]  ;;TODO: filter the update/hover/enter transforms to only use items specified in the bind 
   (update* 'render program :each clean :all #(list (list 'marks %)))))

(defn fold-rendered-tables [program]
  "Merges the data statement of a table that is attached to a rendering
  into the render statement and deletes the table.
  Currently assumes that each table is only the target of ONE renderer.
  TODO: Is this pass needed?  Maybe just do chained data items in vega...."
  (letfn [(extender [pairs] 
            (fn [render]
              (let [[_ _ name _ target & policies] render
                    data (pairs target)]
                (concat render (list data)))))
          (table-pair [[_ _ name & policies]] (list name (select* 'data policies)))]
  (let [tables (find* 'render-table program)
        tm (lop->map (map table-pair tables))]
    (update* '(render render-table) program :each (extender tm)))))


(defn scale-defs [program]
  "Transform operator defs into scale definitions; gather all into one place"
  (letfn [(reform-domain [scale]
            (let [[_ _ domain & rest] (select* 'domain scale)
                  [data field] (clojure.string/split (str domain) #"\.")]
              (concat (remove* 'domain scale) 
                      `((~'domain ((~'data ~(symbol data)) (~'field ~(symbol (str "data." field)))))))))
          (reform [[_ _ name m1 & policies]] 
            (let [config (select* 'config policies)]
              (list* `(~'name ~name) (reform-domain (full-drop config)))))]
    (update* 'operator program :each reform :all #(list (list 'scales %)))))

(defn guides [program]
  "Lift guide declarations out of their render statements, bringing relevant context with them.
  Gather all into one place."
  (letfn [(delete [render] (remove* 'guide render))
          (axis [bindings [_ _ type _ target _ & args]]
            (if (not (= type 'axis))
              (throw (RuntimeException. ("Guide type not recognized '" type "'")))
              (let [transform (bindings target)
                    scale ((lop->map transform) 'scale)]
                (list* (list 'type target) (list 'scale scale) args))))
          (make-guides [render]
            (let [guides (find* 'guide render)
                  bindings (lop->map (second (clean-metas (select** 'enter render))))]
              (map (partial axis bindings) guides)))]
    (concat 
      (remove* 'render program)
      (map delete (find* 'render program))
      (list (list 'axes (apply concat (map make-guides (find* 'render program))))))))

(defn top-level-defs [program]
  (let [view (select* 'view program)
        [_ width height]  (remove meta? (select* 'canvas view))
        width (list 'width width)
        height (list 'height height)
        pad (list 'padding (ptuple->lop (remove-metas (nth (select* 'padding view) 2))))
        view (remove* '(canvas padding) view)]
    (concat (remove* 'view program) (list width height pad view)))) 


(defn remove-imports [program] (remove #(and (seq? %) (= (first %) 'import)) program))


(defn pod2 [program]
  (cond
    (atom? program) program
    (and (pair? program)
         (atom? (first program)))
      (sorted-map (first program) (pod2 (second program)))
    (lop? program)
     (let [keys (map first program)
           vals (map pod2 (map second program))]
       (into (sorted-map) (zipmap keys vals)))
    :else (map pod2 program)))

(defn pod [program]
  ;;When everything works, this should be removed and pod2 should be called directly
  (let [axes (pod2 (select* 'axes program))
        scales (pod2 (select* 'scales program))
        width (pod2 (select* 'width program))
        height (pod2 (select* 'height program))
        padding (pod2 (select* 'padding program))
        data-tables (pod2 (select* 'data program))
        renders (pod2 (select* 'marks program))]
    (reduce into (sorted-map) (list axes scales width height padding data-tables renders))))

(defn json [program] (with-out-str (json/pprint program)))

(defn weave-reacts [program]
  (letfn [(undo-mapping [render action]
            (let [ render-actions (lop->map (second (select* 'enter (select* 'properties (remove-metas render)))))
                  changed-fields (map first action)
                  old-bindings (map #(render-actions %) changed-fields)
                  bindings (map list changed-fields old-bindings)]
              (list 'update bindings)))
          (update-render [event render action]
            (let [[type _ modifier _ target _ mods] action   ;;TODO: target is estabilished after render is selected...fix that...and figure out how to make the event-on-a-different-data-group-than-change plubming work right. 
                  [tag & properties] (select* 'properties render)
                  [_ _ data-binds _] mods 
                  data-actions (map transform-action (remove-metas (map second data-binds)))
                  data-binds (remove-metas (map ffirst data-binds))
                  binds (map list data-binds data-actions)
                  action (list* event binds)
                  co-action (if (= 'transient modifier) (list (undo-mapping render binds)) nil)]
              (concat (remove* 'properties render)
                      (list (list 'properties (concat (list* action properties) co-action))))))
          (update [renders react]
            (let [trigger (select* 'on react)
                  [_ _ source _ event _] trigger
                  actions (find* 'update react)
                  render (first (filter #(= source (nth % 2)) renders))]
              (reduce (partial update-render event) render actions)))
          (update-all [renders reacts] (reduce update renders reacts))]
    (concat 
      (remove* '(react render) program)
      (list (update-all (find* 'render program) 
                        (find* 'react program))))))

(defn emit [program]
  (-> program
    top-level-defs
    remove-unused-renders
    scale-defs
    distinguish-unrendered-tables
    transform-unrendered-tables
    fold-rendered-tables
    simplify-renders
    guides         
    weave-reacts
    renders->marks
    remove-metas
    remove-imports
    pod 
    json
    ))
