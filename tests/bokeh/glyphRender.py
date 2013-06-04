#Stencil->Bokeh export for glyphRender
from bokeh.objects import(Plot, DataRange1d,LinearAxis, Rule, ColumnDataSource, GlyphRenderer, ObjectArrayDataSource, PanTool,ZoomTool)
import bokeh.glyphs
from bokeh import session


class source__:
  _fields = ['x', 'y', 'z']
  x = []
  y = []
  z = []

  def __init__(self):
     self.data([1, 2, 3, 4, 5], [5, 4, 3, 2, 1], [3, 3, 3, 3, 3])

  def data(self, x, y, z):
    self.x.extend(x)
    self.y.extend(y)
    self.z.extend(z)

  def datum(self, x, y, z):
    self.x.append(x)
    self.y.append(y)
    self.z.append(z)

  def clear(self):
    self.x = []
    self.y = []
    self.z = []

  def size(self):
    return len(self.x)

  def dataSource(self):
    return ColumnDataSource(data=dict(x=self.x,y=self.y,z=self.z))

class glyphRender:
  source = None

  def __init__(self):
    #Create tables
    self.source = source__()
    #Share tables with each other

  def set_source_cols(self, x, y, z):
    self.source.clear()
    self.source.data(x, y, z)


  def render(self):
    source = self.source.dataSource()
    _x_dr_ = DataRange1d(sources=[source.columns("x")])
    _y_dr_ = DataRange1d(sources=[source.columns("y")])

    Circle_glyph = bokeh.glyphs.Circle(
      x="x", y="y", fill="red", radius=5, line_color="black")

    glyph_renderer = GlyphRenderer(
      data_source = source,
      glyph = Circle_glyph,
      xdata_range = _x_dr_,
      ydata_range = _y_dr_)

    pantool = PanTool(dataranges = [_x_dr_, _y_dr_], dimensions=["width","height"])
    zoomtool = ZoomTool(dataranges=[_x_dr_, _y_dr_], dimensions=["width","height"])

    rend = Plot(x_range=_x_dr_, y_range=_y_dr_, data_sources=[source], border=80)
    rend.renderers.append(glyph_renderer)

    xLinearAxis = LinearAxis(plot=rend, dimension=self.source._fields.index("x"))
    yLinearAxis = LinearAxis(plot=rend, dimension=self.source._fields.index("y"))
    xRule = Rule(plot=rend, dimension=self.source._fields.index("x"))
    yRule = Rule(plot=rend, dimension=self.source._fields.index("y"))

    sess = session.PlotServerSession(username="defaultuser", serverloc="http://localhost:5006", userapikey="nokey")
    sess.add(glyph_renderer, rend, source,
             xLinearAxis, yLinearAxis, xRule, yRule,
             _x_dr_, _y_dr_, 
             pantool, zoomtool)
    sess.use_doc("glyphRender")
    sess.store_all();

if __name__ == "__main__":
  plot = glyphRender()
  plot.render()
 
