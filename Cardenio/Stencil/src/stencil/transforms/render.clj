(in-ns 'stencil.transform)

(defn clean-binds [program]
  (letfn [(maybe-clean [policy]
            (let [[tag meta & bindings] policy]
              (if (= tag 'bind)
                `(~tag ~meta ~@(map full-drop bindings))
                policy)))]
  (match [program]
    [a :guard atom?] a
    [(['render (m1 :guard meta?) name (m2 :guard meta?) & policies] :seq)]
      `(~'render ~m1 ~name ~m2 ~@(map maybe-clean policies))
    :else (map clean-binds program))))
       



