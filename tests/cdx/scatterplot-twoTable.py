#Debug set to 'true'
from webplot import p 
import numpy as np 


class dataset:
  _fields = ['a', 'b', 'c']
  a = None
  b = None
  c = None

  def __init__(self, **kwargs):
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


class plot:
  _fields = ['x', 'y', 'color']
  _dataset = None
  x = None
  y = None
  color = None

  def __init__(self, dataset):
    self._dataset = dataset

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
      self.x.push(x)
      self.y.push(y)
      self.color.push(color)

  def size(self):
    return len(self.x)

  def data(self):
    return p.make_source(idx=range(len(self.x)), x=self.x, y=self.y, color=self.color)


class scatterplot_twoTable:
  dataset = None
  plot = None

  def set_dataset(self, a, b, c):
    self.dataset = dataset(a=a, b=b, c=c)


  def render(self):
    plot.update()
    p.plot('x', 'y', color='color', data_source=self.plot.data(), scatter=True)

x = np.arange(100) / 6.0 
y = np.sin(x) 
z = np.cos(x) 
plot = scatterplot_twoTable()
plot.set_dataset(x,y,z)
plot.render()
