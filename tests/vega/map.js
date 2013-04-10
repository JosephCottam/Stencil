{
  "width": 1920,
  "height": 1000,
  "viewport": [960, 500],
  "data": [
    {
      "name": "world",
      "url": "data/world-countries.json",
      "format": {"type": "json", "property": "features"} 
    }
  ],
  "marks": [
    {
      "type": "path",
      "from": {
        "data": "world",
        "transform": [{
          "type": "geopath",
          "value": "data",
          "projection": "winkel3",
          "scale": 300,
          "translate": [960, 500]
        }]
      },
      "properties": {
        "enter": {
          "stroke": {"value": "#fff"},
          "path": {"field": "path"}
        },
        "update": {"fill": {"value": "#ccc"}},
        "hover": {"fill": {"value": "pink"}}
      }
    }
  ]
}
