#Stencil->Bokeh export for scatterplot_twoTable
from bokeh import mpl 
from bokeh.bbmodel import ContinuumModel
p = mpl.PlotClient('defaultdoc', 'http://localhost:5006', 'nokey')
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


class plot__:
  _fields = ['x', 'y', 'color']
  x = []
  y = []
  color = []
  _dataset = None

  def set_dataset(self, table):
     self._dataset=table
  def update(self):
    self.clear()
    for i in range(0, self._dataset.size()):
      a = self._dataset.a[i]
      b = self._dataset.b[i]
      c = self._dataset.c[i]
      (x) = a
      (y) = b
      (color) = "RED"
      self.datum(x, y, color)
      self.x.append(x)
      self.y.append(y)
      self.color.append(color)

  def data(self, x, y, color):
    self.x.extend(x)
    self.y.extend(y)
    self.color.extend(color)

  def datum(self, x, y, color):
    self.x.append(x)
    self.y.append(y)
    self.color.append(color)

  def clear(self):
    self.x = []
    self.y = []
    self.color = []

  def size(self):
    return len(self.x)

  def dataSource(self):
    return p.make_source(idx=range(len(self.x)), x=self.x, y=self.y, color=self.color)


class scatterplot_twoTable:
  dataset = None
  plot = None

  def __init__(self):
    #Create tables
    self.dataset = dataset__()
    self.plot = plot__()
    #Share tables with each other
    self.plot.set_dataset(self.dataset)

  def set_dataset_cols(self, a, b, c):
    self.dataset.clear()
    self.dataset.data(a, b, c)


  def render(self):
    self.plot.update()
    p.scatter(data_source=self.plot.dataSource(), color="color", x="x", y="y")
    p.figure()

if __name__ == "__main__":
  plot = scatterplot_twoTable()
  plot.render()
 
