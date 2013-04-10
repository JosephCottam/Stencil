{
  "width": 960,
  "height": 500,
  "data": [
    {
      "name": "unemp",
      "url": "data/unemployment.json"
    },
    {
      "name": "counties",
      "url": "data/us-counties.json",
      "format": {"type": "json", "property": "features"},
      "transform": [
        {"type": "geopath", "value": "data", "projection": "albersUsa"},
        {
          "type": "zip",
          "key": "data.id",
          "with": "unemp",
          "withKey": "data.0",
          "as": "value"
        }
      ]
    },
    {
      "name": "states",
      "url": "data/us-states.json",
      "format": {"type": "json", "property": "features"},
      "transform": [
        {"type":"geopath", "value":"data", "projection":"albersUsa"}
      ]
    }
  ],
  "scales": [
    {
      "name": "color",
      "domain": {"data": "unemp", "field": "data.1"},
      "range": ["#f5f5f5","#00b"]
    }
  ],
  "marks": [
    {
      "type": "path",
      "from": {"data": "counties"},
      "properties": {
        "enter": {
          "stroke": {"value": "#fff"},
          "strokeWidth": {"value": 0.25},
          "path": {"field": "path"}
        },
        "update": {
          "fill": {"scale": "color", "field": "value.data.1"}
        },
        "hover": {
          "fill": {"value": "red"}
        }
      }
    },
    {
      "type": "path",
      "from": {"data": "states"},
      "properties": {
        "enter": {
          "stroke": {"value": "#fff"},
          "strokeWidth": {"value": 1.5},
          "path": {"field": "path"}
        }
      }
    }
  ]
}
