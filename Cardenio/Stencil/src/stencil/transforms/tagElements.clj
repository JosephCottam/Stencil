(in-ns 'stencil.transform)

(defn- isTop? [n] (some (partial = n) '(stencil stream view table op const)))

(defn- tagTops
  [program]
  (match [program]
    [([(top :guard isTop?) id & parts] :seq)] `(~top (~'$id ~id) ~@(map tagTops parts))
    :else (cons '$policy program)))

(declare tagExpr)

(defn tagLetLine [[op targets ex]] `(~op ~(map #(list '$id %) targets) ~(tagExpr ex)))

(defn tagExpr [ex]
  (match [ex]
    [(ex :guard symbol?)] (list '$sym ex)
    [(ex :guard value?)] (list '$value ex)
    [(['let & letLines] :seq)] (list 'let (map tagLetLine letLines))
    [([(op :guard symbol?) & args] :seq)] `((~'$op ~op) ~@(map tagExpr args))
    :else (map tagExpr ex)))

;;TODO: Make "kind" an ex as wel...
(defn- tagRender [kind & args] `((~'$id ~kind) ~@(map tagExpr args)))
(defn- tagFacet [facet] (identity facet))

(defn- ptaggers [key] (or ({'renderer tagRender 'facet tagFacet} key) tagExpr))
(defn- tagInPolicies [kind policy] ((ptaggers kind) policy))

(defn- tagPolicies 
  [program]
  (match [program]
    [(['$policy kind & policy] :seq)] `(~'$policy (~'$id ~kind) ~@((ptaggers kind) policy))
    [([ a & x] :seq)] (map tagPolicies (cons a x))
    :else program))


(defn tagElements [program] (-> program tagTops tagPolicies))
