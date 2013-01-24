#Debug set to 'true'
from webplot import p 
import numpy as np 


class dataset:
  _fields = ['a', 'b', 'c']
  a = None
  b = None
  c = None

  def __init__(self, *args, **kwargs):
    if (len(kwargs) == len(self._fields)):
      self.a=kwargs['a']
      self.b=kwargs['b']
      self.c=kwargs['c']
    else:
      raise Exception("Data not properly supplied to table dataset")

  def data(self):
    return  p.make_source(idx=range(len(self.a)), a=self.a, b=self.b, c=self.c)

class plot:
  _fields ['x','y']
  x = None
  y = None

  def __init__(self, dataset):
    self.dataset = dataset

  def update():
    self.x = []
    self.y = []
    for i in range(0, len(source.a)):
       a = source.a[i]
       b = source.b[i]
       x = a
       y = b
       self.x.push(x)
       self.y.push(y)

  def data(self):
    self.update()
    return  p.make_source(idx=range(len(self.x)), x=self.x, y=self.y)


class scatterplot_twoTable:
  _dataset = None
  _plot = None

  def set_dataset(self, a, b, c):
    self._dataset = dataset(a=a, b=b, c=c)

  def render(self):
    _plot.update()
    p.plot('x', 'y', color='RED', data_source=self._plot.data(), scatter=True)


x = np.arange(100) / 6.0 
y = np.sin(x) 
z = np.cos(x) 
plot = scatterplot_inline()
plot.set_dataset(x,y,z)
plot.render()

