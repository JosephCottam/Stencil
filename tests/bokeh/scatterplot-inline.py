#Debug set to 'true'
from bokeh import mpl 
p = mpl.PlotClient('defaultdoc', 'http://localhost:5006', 'nokey')
import numpy as np 
import datetime
import time


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


class scatterplot_inline:
  dataset = None

  def set_dataset(self, a, b, c):
    self.dataset = dataset(a=a, b=b, c=c)


  def render(self):
    p.plot('a', 'b', color='RED', data_source=self.dataset.data(), scatter=True)

x = np.arange(100) / 6.0 
y = np.sin(x) 
z = np.cos(x) 
plot = scatterplot_inline()
plot.set_dataset(x,y,z)
plot.render()
 