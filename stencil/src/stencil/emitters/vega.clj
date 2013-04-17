(ns stencil.emitters.vega
  (:use [stencil.util])
  (:require [clojure.data.json :as json])
  (:require [stencil.transform :as t])
  (:require [stencil.pprint])
  (import (org.stringtemplate.v4 ST STGroup STGroupFile)))

(defn ptuples->lop [ptuple]
  (let [[tag fields & items] (remove-metas ptuple)
        fields (rest fields)
        items (map rest items)]
    (partition (count fields) (partition 2 (interleave (cycle fields) (apply interleave items))))))

(defn ptuple->lop [[tag fields & vals]] (map list (rest fields) vals))
(defn pair? [item] (and (seq? item) (== 2 (count item))))
(defn lop? [item] (and (seq? item) (every? pair? item)
                       (every? t/atom? (map first item))))


(defn find-descendant [tag data]
  (let [items (t/filter-tagged tag data)]
    (cond
      (not (seq? data)) nil 
      (empty? items) (mapcat (partial find-descendant tag) data)
      :else (first items))))

(defn distinguish-unrendered-tables [program]
  "Mark tables that will not be rendered as 'data-table'
  (simplifies later steps)."
  (letfn [(retag [table tag] (cons tag (rest table)))
          (maybe-retag [targets table]
            (if (any= (nth table 2) targets)
              (retag table 'render-table)
              (retag table 'data-table)))]
    (let [renders (t/filter-tagged 'render program)
          tables  (t/filter-tagged 'table program)
          targets (map #(nth % 4) renders)
          tables (map (partial maybe-retag targets) tables)]
      (concat (t/remove-tagged 'table program) tables))))


(defn transform-unrendered-tables [program]
  (letfn [
          (transform-data [table] 
            (let [data (t/filter-tagged 'data table)
                  using (find-descendant 'using data)
                  [tag _ fields _ load] using 
                  loader (first load)]
              (case loader
                'ptuples (ptuples->lop load) 
                :else (throw (RuntimeException. (str "Loader not know: " loader))))))
          (transform [table]
            (let [data (transform-data table)
                  [_ _ name & rest] table]
              (list (list 'name name) (list 'values data))))]
    (concat (t/remove-tagged 'data-table program)
      (list (list 'data (map transform (t/filter-tagged 'data-table program)))))))

(defn remove-unused-renders [program]
  (letfn [(render-filter [used] (fn [[tag _ name & rest]] (any= name used)))]
    (let [view (first (t/filter-tagged 'view program))
          renders (t/filter-tagged 'render program)
          used (drop 2 (remove-metas view))
          renders (filter (render-filter used) renders)]
      (concat (t/remove-tagged 'render program) renders))))
      

(defn simplify-renders [program]
  (letfn [(transform-actions [action]
            (letfn [(remove-do [action] (if (and (seq? action) (= 'do (first action))) (last action) action))
                    (field-or-val [item] 
                      (cond 
                        (symbol? item) (list 'field (symbol (str "data." item)))
                        (t/atom? item)   (list 'value item)
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
                      (concat (t/remove-tagged 'ptuple action)  
                              (apply concat (map ptuple->lop (t/filter-tagged 'ptuple action)))))]
              (-> action remove-do tag-scales tag-atoms ptuple->lop*)))
          (transform [render]
            (let [[tag m0 name m1 old-target _ type _ & policies] render
                  bindings (find-descendant 'bind policies)  ;;TODO Filter the data-bindings to just the items listed in the regular bind statement
                  data (find-descendant 'data policies)
                  [tag _ fields gen trans] (find-descendant 'using data)
                  [_ _ source _] gen
                  [_ _ data-binds _] trans 
                  data-actions (map transform-actions (remove-metas (map second data-binds)))
                  data-binds (remove-metas (map ffirst data-binds))
                  binds (map list data-binds data-actions)]
              (concat (t/remove-tagged 'data render)
                     (list (list 'type type) 
                           (list 'from (list 'data source)) 
                           (list 'properties (list 'enter binds))))))]
    (let [renders (t/filter-tagged 'render program)]
      (concat (t/remove-tagged 'render program) 
              (map transform renders)))))

(defn renders->marks [program]
  "Gathers up the renders, strips away the extras and puts it into a 'marks' list."
  (letfn [(clean [render]
          (let [[tag _ name _ target _ type _ & policies] render]
            (t/remove-tagged 'bind policies)))]
  (concat (t/remove-tagged 'render program)
          (list (list 'marks (map clean (t/filter-tagged 'render program)))))))

(defn fold-rendered-tables [program]
  "Merges the data statement of a table that is attached to a rendering
  into the render statement and deletes the table.
  Currently assumes that each table is only the target of ONE renderer."
  (letfn [(extender [pairs] 
            (fn [render]
              (let [[_ _ name _ target & policies] render
                    data (pairs target)]
                (concat render (list data)))))
          (table-pair [[_ _ name & policies]] (list name (first (t/filter-tagged 'data policies))))]
  (let [renders (t/filter-tagged 'render program)
        tables (t/filter-tagged 'render-table program)
        tm (t/lop->map (map table-pair tables))
        renders (map (extender tm) renders)]
    (concat (t/remove-tagged any= '(render render-table) program) renders))))


(defn scale-defs [program]
  "Transform operator defs into scale definitions; gather all into one place"
  (letfn [(gather [program] (t/filter-tagged 'operator program))
          (clean [[bind m0 key m1 & val]] (list* key m1 val))
          (reform-domain [scale]
            (let [[_ _ domain & rest] (first (t/filter-tagged 'domain scale))
                  [data field] (clojure.string/split (str domain) #"\.")]
              (concat (t/remove-tagged 'domain scale) 
                      `((~'domain ((~'data ~(symbol data)) (~'field ~(symbol (str "data." field)))))))))
          (reform [[_ _ name m1 & policies]] 
            (let [config (first (t/filter-tagged 'config policies))]
              (list* `(~'name ~name) (reform-domain (t/full-drop config)))))]
    (concat (t/remove-tagged 'operator program) 
      (list (list 'scales (map reform (gather program)))))))

(defn guides [program]
  "Lift guide declarations out of their render statements, bringing relevant context with them.
  Gather all into one place."
  (letfn [(delete [render] (t/remove-tagged 'guide render))
          (axis [bindings [_ _ type _ target _ & args]]
            (if (not (= type 'axis))
              (throw (RuntimeException. ("Guide type not recognized '" type "'")))
              (let [transform (bindings target)
                    ;_ (println "A:" target)
                    ;_ (println "T:" transform)
                    scale ((t/lop->map transform) 'scale)]
                (list* (list 'type target) (list 'scale scale) args))))
          (make-guides [render]
            (let [guides (t/filter-tagged 'guide render)
                  bindings (t/lop->map (second (clean-metas (find-descendant 'enter render))))]
              (map (partial axis bindings) guides)))]
    (concat 
      (t/remove-tagged 'render program)
      (map delete (t/filter-tagged 'render program))
      (list (list 'axes (apply concat (map make-guides (t/filter-tagged 'render program))))))))

(defn top-level-defs [program]
  (let [view (first (t/filter-tagged 'view program))
        canvas (remove meta? (first (t/filter-tagged 'canvas view)))
        width (list 'width (second canvas))
        height (list 'height (nth canvas 2))
        pad (list 'padding (ptuple->lop (remove-metas (nth (first (t/filter-tagged 'padding view)) 2))))
        view (t/remove-tagged any= '(canvas padding) view)]
    (concat (t/remove-tagged 'view program) (list width height pad view)))) 


(defn select [tag ls] 
  (let [items (t/filter-tagged tag ls)]
    (if (> (count items) 1)
      (throw (RuntimeException. (str "More than one '" tag "' items in program.")))
      (first items))))

(defn remove-imports [program] (remove #(and (seq? %) (= (first %) 'import)) program))


(defn pod2 [program]
  (cond
    (t/atom? program) program
    (and (pair? program)
         (t/atom? (first program)))
      (sorted-map (first program) (pod2 (second program)))
    (lop? program)
     (let [keys (map first program)
           vals (map pod2 (map second program))]
       (into (sorted-map) (zipmap keys vals)))
    :else (map pod2 program)))

(defn pod [program]
  ;;When everything works, this should be removed and pod2 should be called directly
  (let [axes (pod2 (select 'axes program))
        scales (pod2 (select 'scales program))
        width (pod2 (select 'width program))
        height (pod2 (select 'height program))
        padding (pod2 (select 'padding program))
        data-tables (pod2 (select 'data program))
        renders (pod2 (select 'marks program))]
    (reduce into (sorted-map) (list axes scales width height padding data-tables renders))))
          
(defn json [program] (with-out-str (json/pprint program)))
(defn medium [program]
  "Intermediate state to faciliate development."
  (-> program 
      top-level-defs
      remove-unused-renders
      scale-defs
      distinguish-unrendered-tables
      transform-unrendered-tables
      fold-rendered-tables
      simplify-renders
      guides         
      ;react
      renders->marks
      ))
   

(defn emit [program]
  (-> program
    medium
    remove-metas
    remove-imports
    pod 
    json))
