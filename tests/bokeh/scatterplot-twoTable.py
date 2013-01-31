#Debug set to 'true'
from bokeh import mpl 
p = mpl.PlotClient('defaultdoc', 'http://localhost:5006', 'nokey')
import numpy as np 
import datetime
import time


class dataset__:
  _fields = ['a', 'b', 'c']
  a = None
  b = None
  c = None

  def setData(self, **kwargs):
    if (len(kwargs) == len(self._fields)):
      self.a=kwargs['a']
      self.b=kwargs['b']
      self.c=kwargs['c']
    else:
      raise Exception("Data not properly supplied to table dataset")

  def size(self):
    return len(self.a)

  def data(self):
    return p.make_source(idx=range(len(self.a)), a=self.a, b=self.b, c=self.c)


class plot__:
  _fields = ['x', 'y', 'color']
  _dataset = None
  x = None
  y = None
  color = None

  def set_dataset(self, table):
     self._dataset=table

  def update(self):
    self.x = []
    self.y = []
    self.color = []
    for i in range(0, self._dataset.size()):
      a = self._dataset.a[i]
      b = self._dataset.b[i]
      c = self._dataset.c[i]
      (x) = a
      (y) = b
      (color) = "RED"
      self.x.append(x)
      self.y.append(y)
      self.color.append(color)

  def size(self):
    return len(self.x)

  def data(self):
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
    self.dataset.setData(a=a, b=b, c=c)


  def render(self):
    self.plot.update()
    p.scatter('x', 'y', color='color', data_source=self.plot.data()) 
    p.figure()

x = np.arange(100) / 6.0 
y = np.sin(x) 
z = np.cos(x) 
plot = scatterplot_twoTable()
plot.set_dataset_cols(x,y,z)
plot.render()
 