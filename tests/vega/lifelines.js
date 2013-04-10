{
  "name": "lifelines",
  "width": 400,
  "height": 100,
  "padding": {"top": 60, "left": 5, "bottom": 30, "right": 30},
  "data": [
    {
      "name": "people",
      "values": [
        {"label":"Washington", "born":-7506057600000, "died":-5365324800000, 
         "enter":-5701424400000, "leave":-5453884800000},
        {"label":"Adams",      "born":-7389766800000, "died":-4528285200000, 
         "enter":-5453884800000, "leave":-5327740800000},
        {"label":"Jefferson",  "born":-7154586000000, "died":-4528285200000, 
         "enter":-5327740800000, "leave":-5075280000000},
        {"label":"Madison",    "born":-6904544400000, "died":-4213184400000, 
         "enter":-5075280000000, "leave":-4822819200000},
        {"label":"Monroe",     "born":-6679904400000, "died":-4370518800000, 
         "enter":-4822819200000, "leave":-4570358400000}
      ]
    },
    {
      "name": "events",
      "format": {"type":"json", "parse":{"when":"date"}},
      "values": [
        {"name":"Decl. of Independence", "when":"July 4, 1776"},
        {"name":"U.S. Constitution",     "when":"3/4/1789"},
        {"name":"Louisiana Purchase",    "when":"April 30, 1803"},
        {"name":"Monroe Doctrine",       "when":"Dec 2, 1823"}
      ]
    }

  ],
  "scales": [
    {
      "name": "y",
      "type": "ordinal",
      "range": "height",
      "domain": {"data": "people", "field": "data.label"}
    },
    {
      "name": "x",
      "type": "time",
      "range": [0, 400],
      "round": true,
      "nice": "year",
      "domain": {"data": "people", "field": ["data.born", "data.died"]}
    }
  ],
  "axes": [
    {"type": "x", "scale": "x"}
  ],
  "marks": [
    {
      "type": "text",
      "from": {"data": "events"},
      "properties": {
        "enter": {
          "x": {"scale": "x", "field": "data.when"},
          "y": {"value": -10},
          "angle": {"value": -25},
          "fill": {"value": "#000"},
          "text": {"field": "data.name"},
          "font": {"value": "Helvetica Neue"},
          "fontSize": {"value": 10}
        }
      }
    },
    {
      "type": "rect",
      "from": {"data": "events"},
      "properties": {
        "enter": {
          "x": {"scale": "x", "field": "data.when"},
          "y": {"value": -8},
          "width": {"value": 1},
          "height": {"value": 108},
          "fill": {"value": "#888"}
        }
      }
    },
    {
      "type": "text",
      "from": {"data": "people"},
      "properties": {
        "enter": {
          "x": {"scale": "x", "field": "data.born"},
          "y": {"scale": "y", "field": "data.label", "offset": -3},
          "fill": {"value": "#000"},
          "text": {"field": "data.label"},
          "font": {"value": "Helvetica Neue"},
          "fontSize": {"value": 10}
        }
      }
    },
    {
      "type": "rect",
      "from": {"data": "people"},
      "properties": {
        "enter": {
          "x": {"scale": "x", "field": "data.born"},
          "x2": {"scale": "x", "field": "data.died"},
          "y": {"scale": "y", "field": "data.label"},
          "height": {"value": 2},
          "fill": {"value": "#557"}
        }
      }
    },
    {
      "type": "rect",
      "from": {"data": "people"},
      "properties": {
        "enter": {
          "x": {"scale": "x", "field": "data.enter"},
          "x2": {"scale": "x", "field": "data.leave"},
          "y": {"scale": "y", "field": "data.label", "offset":-1},
          "height": {"value": 4},
          "fill": {"value": "#e44"}
        }
      }
    }
  ]
}
