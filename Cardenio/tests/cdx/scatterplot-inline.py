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

  def data(self):
    return p.make_source(idx=range(len(self.a)), a=self.a, b=self.b, c=self.c)


class scatterplot_inline:
  _dataset = None

  def set_dataset(self, a, b, c):
    self._dataset = dataset(a=a, b=b, c=c)

  def render(self):
      p.plot('a', 'b', color='RED', data_source=self._dataset.data(), scatter=True)


x = np.arange(100) / 6.0 
y = np.sin(x) 
z = np.cos(x) 
plot = scatterplot_inline()
plot.set_dataset(x,y,z)
plot.render()
