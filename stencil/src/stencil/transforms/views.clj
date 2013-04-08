(ns stencil.transform)

(defn ensure-view [program]
  "If there is no view statment, provide one with all renderers included.
  View statement is only checked for in the top-level.
  Assumes all renders have been gathered to the top-level already."
  (let [views (filter-tagged 'view program)
        renders (filter-tagged 'render program)
        render-names (map name-of renders)
        [preamble body] (split-preamble program)]
    (if (or (not (= (first program) 'stencil)) (not (empty? views)))
      program
      `(~@preamble 
           (~'view (~'$meta (~'type ~'fn)) ~(gensym 'viewg_) (~'$meta (~'type ~'view))
               ~@(interleave render-names (repeat '($meta (type render)))))
           ~@body))))





