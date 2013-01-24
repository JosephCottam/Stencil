#Debug set to 'true'
from webplot import p 
import numpy as np 


class dataset:
  _fields = ['a', 'b', 'c']
  _cols = None

  def __init__(self, *args, **kwargs):
    if (len(kwargs) == len(self._fields)):
      a=kwargs['a']
      b=kwargs['b']
      c=kwargs['c']
      _cols = ['a':a,'b':b,'c':c]
    else:
      raise Exception("Data not properly supplied to table dataset")

  def data(self):
    cols = self._cols
    return  p.make_source(idx=range(len(cols['a'])), a=cols['a'], b=cols['b'], c=cols['c'])

class plot:
  _fields ['x','y','color']
  _cols = None

  def __init__(self, dataset):
    self.dataset = dataset

  def update():
    source = self.dataset._cols
    _x = []
    _y = []
    for i in range(0, len(source['a'])):
       a = source['a'][i]
       b = source['b'][i]
       x = a
       y = b
       _x.push(x)
       _y.push(y)
    _cols = {"x":_x,"y":_y]

  def data(self):
    self.update()
    cols = self._cols
    return  p.make_source(idx=range(len(cols['x'])), x=cols['x'], y=cols['y'], c=cols['c'])


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

