#Debug set to 'true'
from webplot import p 
import numpy as np 


class dataset:
  _fields = ['a', 'b', 'c']
  _data = None

  def __init__(self, *args, **kwargs):
    if (len(kwargs) == len(self._fields)):
      a=kwargs['a']
      b=kwargs['b']
      c=kwargs['c']
      self._data = p.make_source(idx=range(len(a)), a=a, b=b, c=c)
    else:
      raise Exception("Data not properly supplied to table dataset")

  def data(self):
    return self._data


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
