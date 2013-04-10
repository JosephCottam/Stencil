{
  "width": 700,
  "height": 400,
  "padding": {"top": 0, "left": 0, "bottom": 20, "right": 0},
  "data": [
    {
      "name": "pop2000",
      "url": "data/population.json",
      "transform": [
        {"type": "filter", "test": "d.data.year == 2000"}
      ]
    }
  ],
  "scales": [
    {
      "name": "g",
      "domain": [0, 1],
      "range": [340, 10]
    },
    {
      "name": "y",
      "type": "ordinal",
      "range": "height",
      "reverse": true,
      "domain": {"data": "pop2000", "field": "data.age"}
    },
    {
      "name": "c",
      "type": "ordinal",
      "domain": [1, 2],
      "range": ["#1f77b4", "#e377c2"]
    }
  ],
  "marks": [
    {
      "type": "text",
      "interactive": false,
      "from": {
        "data": "pop2000",
        "transform": [{"type":"unique", "field":"data.age", "as":"age"}]
      },
      "properties": {
        "enter": {
          "x": {"value": 325},
          "y": {"scale": "y", "field": "age", "offset": 11},
          "text": {"field": "age"},
          "baseline": {"value": "middle"},
          "align": {"value": "center"},
          "fill": {"value": "#000"}
        }
      }
    },
    {
      "type": "group",
      "from": {
        "data": "pop2000",
        "transform": [
          {"type":"facet", "keys":["data.sex"]}
        ]
      },
      "properties": {
        "update": {
          "x": {"scale": "g", "field": "index"},
          "y": {"value": 0},
          "width": {"value": 300},
          "height": {"value": 400}
        }
      },
      "scales": [
        {
          "name": "x",
          "type": "linear",
          "range": "width",
          "reverse": {"field": "index"},
          "nice": true,
          "domain": {"data": "pop2000", "field": "data.people"}
        }
      ],
      "axes": [
        {"type": "x", "scale": "x", "format": "s"}
      ],
      "marks": [
        {
          "type": "rect",
          "properties": {
            "enter": {
              "x": {"scale": "x", "field": "data.people"},
              "x2": {"scale": "x", "value": 0},
              "y": {"scale": "y", "field": "data.age"},
              "height": {"scale": "y", "band": true, "offset": -1},
              "fillOpacity": {"value": 0.6},
              "fill": {"scale": "c", "field": "data.sex"}
            }
          }
        }
      ]
    }
  ]
}
