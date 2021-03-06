{
  "name": "image",
  "width": 200,
  "height": 200,
  "padding": {"left":30, "top":10, "bottom":30, "right":10},
  "data": [
    {
      "name": "data",
      "values": [
        {"x":0.5, "y":0.5, "img":"data/ffox.png"},
        {"x":1.5, "y":1.5, "img":"data/gimp.png"},
        {"x":2.5, "y":2.5, "img":"data/7zip.png"}
      ]
    }
  ],
  "scales": [
    {"name": "x", "domain": [0, 3], "range": "width"},
    {"name": "y", "domain": [0, 3], "range": "height"}
  ],
  "axes": [
    {"type": "x", "scale": "x"},
    {"type": "y", "scale": "y"}
  ],
  "marks": [
    {
      "type": "image",
      "from": {"data": "data"},
      "properties": {
        "enter": {
          "url": {"field": "data.img"},
          "width": {"value": 50},
          "height": {"value": 50},
          "x": {"scale": "x", "field": "data.x"},
          "y": {"scale": "y", "field": "data.y"},
          "align": {"value": "center"},
          "baseline": {"value": "middle"}
        },
        "update": {
          "opacity": {"value": 1.0}
        },
        "hover": {
          "opacity": {"value": 0.5}
        }
      }
    }
  ]
}
