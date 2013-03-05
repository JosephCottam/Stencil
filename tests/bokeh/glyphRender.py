#Stencil->Bokeh export for glyphRender
from bokeh import mpl 
from bokeh.bbmodel import ContinuumModel
p = mpl.PlotClient('defaultdoc', 'http://localhost:5006', 'nokey')


class source__:
  _fields = ['x', 'y', 'z', 'radius', 'color']
  x = []
  y = []
  z = []
  radius = []
  color = []

  def __init__(self):
     self.data([1, 2, 3, 4, 5], [5, 4, 3, 2, 1], [3, 3, 3, 3, 3], [10, 5, 5, 5, 5], ["blue", "blue", "red", "blue", "blue"])

  def data(self, x, y, z, radius, color):
    self.x.extend(x)
    self.y.extend(y)
    self.z.extend(z)
    self.radius.extend(radius)
    self.color.extend(color)

  def datum(self, x, y, z, radius, color):
    self.x.append(x)
    self.y.append(y)
    self.z.append(z)
    self.radius.append(radius)
    self.color.append(color)

  def clear(self):
    self.x = []
    self.y = []
    self.z = []
    self.radius = []
    self.color = []

  def size(self):
    return len(self.x)

  def dataSource(self):
    return p.make_source(idx=range(len(self.x)), x=self.x, y=self.y, z=self.z, radius=self.radius, color=self.color)


class glyphRender:
  source = None

  def __init__(self):
    #Create tables
    self.source = source__()
    #Share tables with each other

  def set_source_cols(self, x, y, z, radius, color):
    self.source.clear()
    self.source.data(x, y, z, radius, color)


  def render(self):
    source = self.source.dataSource()
    rend2233 = ContinuumModel('Plot')
    _x_dr_ = ContinuumModel(
           'DataRange1d', 
           sources=[{'ref' : source.ref(), 'columns' : ['x']}])
    _y_dr_ = ContinuumModel(
           'DataRange1d', 
           sources=[{'ref' : source.ref(), 'columns' : ['y']}])

    glyph_renderer = ContinuumModel(
       'GlyphRenderer',
       data_source = source.ref(),
       xdata_range = _x_dr_.ref(),
       ydata_range = _y_dr_.ref(),
       x="x",
       y="y",
       color="blue",
       glyphs = [{"type" : "circles"}]
       )

    xLinearAxis = ContinuumModel(
           'LinearAxis',
           parent=rend2233.ref(),
           data_range=_x_dr_.ref(),
           orientation="bottom")
    yLinearAxis = ContinuumModel(
           'LinearAxis',
           parent=rend2233.ref(),
           data_range=_y_dr_.ref(),
           orientation="left")

    rend2233.set('renderers', [glyph_renderer.ref()])
    rend2233.set('axes', [xLinearAxis.ref(), yLinearAxis.ref()])
    p.bbclient.upsert_all(
      [glyph_renderer,
       source,  rend2233,
       _x_dr_, _y_dr_,
       xLinearAxis, yLinearAxis])
    p.show(rend2233)

if __name__ == "__main__":
  plot = glyphRender()
  plot.render()
 