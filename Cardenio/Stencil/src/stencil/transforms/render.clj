(in-ns 'stencil.transform)

(defn clean-binds [program]
  (letfn [(maybe-clean [policy]
            (let [[tag meta & bindings] policy]
              (if (= tag 'bind)
                `(~tag ~meta ~@(map full-drop bindings))
                policy)))]
  (match [program]
    [a :guard atom?] a
    [(['render name (m :guard meta?) & policies] :seq)]
      `(~'render ~name ~m ~@(map maybe-clean policies))
    :else (map clean-binds program))))
       



