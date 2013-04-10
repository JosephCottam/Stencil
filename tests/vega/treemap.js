{
  "name": "treemap",
  "width": 900,
  "height": 500,
  "padding": {"top":0, "bottom":0, "left":0, "right":0},
  "data": [
    {
      "name": "table",
      "values": [12, 23, 47, 6, 52, 19],
      "transform": [
        {"type": "facet"},
        {"type": "treemap"},
        {"type": "filter", "test": "!d.children"}
      ]
    }
  ],
  "marks": [
    {
      "type": "rect",
      "from": {"data": "table"},
      "properties": {
        "enter": {
          "x": {"field": "x"},
          "y": {"field": "y"},
          "width": {"field": "width"},
          "height": {"field": "height"},
          "stroke": {"value": "#fff"}
        },
        "update": {
          "fill": {"value": "#ccc"}
        },
        "hover": {
          "fill": {"value": "pink"}
        }
      }
    },
    {
      "type": "text",
      "from": {"data": "table"},
      "interactive": false,
      "properties": {
        "enter": {
          "x": {"field": "x"},
          "y": {"field": "y"},
          "dx": {"value": 4},
          "dy": {"value": 4},
          "font": {"value": "Helvetica Neue"},
          "fontSize": {"value": 14},
          "align": {"value": "left"},
          "baseline": {"value": "top"},
          "fill": {"value": "#000"},
          "text": {"field": "data"}
        }
      }
    }
  ]
}
