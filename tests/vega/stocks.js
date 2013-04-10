{
  "width": 500,
  "height": 200,
  "padding": {"top": 10, "left": 40, "bottom": 20, "right": 40},
  "data": [
    {
      "name": "stocks",
      "url": "data/stocks.csv",
      "format": {"type": "csv", "parse": {"price": "number"}}
    }
  ],
  "scales": [
    {
      "name": "x",
      "type": "ordinal",
      "range": "width",
      "domain": {"data": "stocks", "field": "data.date"}
    },
    {
      "name": "y",
      "type": "linear",
      "range": "height",
      "nice": true,
      "domain": {"data": "stocks", "field": "data.price"}
    },
    {
      "name": "color", "type": "ordinal", "range": "category10"
    }
  ],
  "axes": [
    {"type": "x", "scale": "x", "tickSizeEnd": 0,
     "values": ["Jan 2000", "Jan 2002", "Jan 2004", "Jan 2006", "Jan 2008", "Jan 2010"]},
    {"type": "y", "scale": "y"}
  ],
  "marks": [
    {
      "type": "group",
      "from": {
        "data": "stocks",
        "transform": [{"type": "facet", "keys": ["data.symbol"]}]
      },
      "marks": [
        {
          "type": "line",
          "properties": {
            "enter": {
              "x": {"scale": "x", "field": "data.date"},
              "y": {"scale": "y", "field": "data.price"},
              "stroke": {"scale": "color", "field": "data.symbol"},
              "strokeWidth": {"value": 2}
            }
          }
        }
      ]
    },
    {
      "type": "text",
      "from": {
        "data": "stocks",
        "transform": [
          {"type": "filter", "test": "d.data.date == 'Mar 2010'"}
        ]
      },
      "properties": {
        "enter": {
          "x": {"scale": "x", "field": "data.date", "offset": 2},
          "y": {"scale": "y", "field": "data.price"},
          "fill": {"scale": "color", "field": "data.symbol"},
          "text": {"field": "data.symbol"},
          "baseline": {"value": "middle"}
        }
      }
    }
  ]
}
