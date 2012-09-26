(in-ns 'stencil.transform)

(defn- isTop? [n] (some (partial = n) '(stencil stream view table op const)))

(defn- tagTops
  [program]
  (match [program]
    [([(top :guard isTop?) id & parts] :seq)] `(~top (~'$id ~id) ~@(map tagTops parts))
    :else (cons '$policy program)))

(defn- rTagger [renderer] (identity renderer))
(defn- dTagger [data] (identity data))
(defn- mTagger [metas] (identity metas))
(defn- fTagger [facet] (identity facet))

(defn- ptaggers [key] (or ({'renderer rTagger 'data dTagger 'facet fTagger 'meta mTagger} key) identity))
(defn- tagInPolicies [kind policy] ((ptaggers kind) policy))

(defn- tagPolicies 
  [program]
  (match [program]
    [(['$policy kind & policy] :seq)] `(~'$policy (~'id ~kind) ~@((ptaggers kind) policy))
    [([ a & x] :seq)] (map tagPolicies (cons a x))
    :else program))


(defn tagElements [program] (-> program tagTops tagPolicies))
