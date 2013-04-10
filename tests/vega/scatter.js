{
  "width": 400,
  "height": 400,
  "padding": {"top":50, "left":50, "bottom":50, "right":50},
  "data": [
    {
      "name": "points",
      "url": "data/points.json"
    }
  ],
  "scales": [
    {
      "name": "x",
      "nice": true,
      "range": "width",
      "domain": {"data": "points", "field": "data.x"}
    },
    {
      "name": "y",
      "nice": true,
      "range": "height",
      "domain": {"data": "points", "field": "data.y"}
    }
  ],
  "axes": [
    {"type": "x", "scale": "x"},
    {"type": "y", "scale": "y"}
  ],
  "marks": [
    {
      "type": "symbol",
      "from": {"data": "points"},
      "properties": {
        "enter": {
          "x": {"scale": "x", "field": "data.x"},
          "y": {"scale": "y", "field": "data.y"},
          "stroke": {"value": "steelblue"},
          "fillOpacity": {"value": 0.5}
        },
        "update": {
          "fill": {"value": "transparent"},
          "size": {"value": 100}
        },
        "hover": {
          "fill": {"value": "pink"},
          "size": {"value": 300}
        }
      }
    }
  ]
}
