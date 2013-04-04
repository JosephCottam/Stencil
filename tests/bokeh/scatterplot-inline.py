#Stencil->Bokeh export for scatterplot_inline
from bokeh import mpl 
from bokeh.bbmodel import ContinuumModel
p = mpl.PlotClient('defaultuser', serverloc='http://localhost:5006', userapikey='nokey')
p.use_doc("scatterplot_inline")
from math import *


class dataset__:
  _fields = ['a', 'b', 'c']
  a = []
  b = []
  c = []

  def __init__(self):
     (x) = range(0, 100, 6)
     (y) = map(sin, x)
     (z) = map(cos, x)
     self.data(x, y, z)

  def data(self, a, b, c):
    self.a.extend(a)
    self.b.extend(b)
    self.c.extend(c)

  def datum(self, a, b, c):
    self.a.append(a)
    self.b.append(b)
    self.c.append(c)

  def clear(self):
    self.a = []
    self.b = []
    self.c = []

  def size(self):
    return len(self.a)

  def dataSource(self):
    return p.make_source(idx=range(len(self.a)), a=self.a, b=self.b, c=self.c)


class scatterplot_inline:
  dataset = None

  def __init__(self):
    #Create tables
    self.dataset = dataset__()
    #Share tables with each other

  def set_dataset_cols(self, a, b, c):
    self.dataset.clear()
    self.dataset.data(a, b, c)


  def render(self):
    p.scatter(data_source=self.dataset.dataSource(), x="a", y="b", color="RED")
    p.figure()

if __name__ == "__main__":
  plot = scatterplot_inline()
  plot.render()
 
