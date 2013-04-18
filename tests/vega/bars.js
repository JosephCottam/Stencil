{"axes":[{"scale":"xs", "type":"x"}, {"scale":"ys", "type":"y"}],
 "data":
 [{"name":"rawData",
   "values":
   [{"x":"A", "y":28}, {"x":"B", "y":55}, {"x":"C", "y":43},
    {"x":"D", "y":91}, {"x":"E", "y":81}, {"x":"F", "y":53},
    {"x":"G", "y":19}, {"x":"H", "y":79}, {"x":"I", "y":52}]}],
 "height":200,
 "marks":
 [{"from":{"data":"rawData"},
   "properties":
   {"enter":
    {"fill":{"value":"SteelBlue"},
     "width":{"band":true, "offset":-1, "scale":"xs"},
     "x":{"field":"data.x", "scale":"xs"},
     "y":{"field":"data.y", "scale":"ys"},
     "y2":{"scale":"ys", "value":0}},
    "hover":{"fill":{"value":"Red"}},
     "update":{"fill":{"value":"steelblue"}}},
   "type":"rect"}],
 "padding":{"bottom":20, "left":30, "right":10, "top":10},
 "scales":
 [{"domain":{"data":"rawData", "field":"data.x"},
   "name":"xs",
   "range":"width",
   "type":"ordinal"},
  {"domain":{"data":"rawData", "field":"data.y"},
   "name":"ys",
   "nice":true,
   "range":"height"}],
 "width":400}
