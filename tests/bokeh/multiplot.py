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


class multiplot:
  dataset = None

  def __init__(self):
    #Create tables
    self.dataset = dataset__()
    #Share tables with each other

  def set_dataset_cols(self, a, b, c):
    self.dataset.setData(a=a, b=b, c=c)


  def render(self):
    p.table(self.dataset.data(), ['a', 'b', 'c'])
    p.scatter('a', 'b', color='orange', data_source=self.dataset.data()) 
    p.figure()
    p.scatter('a', 'c', color='red', data_source=self.dataset.data()) 
    p.figure()
    p.plot('a', 'b', color='yellow', data_source=self.dataset.data()) 
    p.figure()
    p.plot('a', 'c', color='black', data_source=self.dataset.data()) 
    p.figure()
    p.plot('a', 'b', color='blue', data_source=self.dataset.data()) 
    p.plot('a', 'c', color='green', data_source=self.dataset.data()) 
    p.figure()

x = np.arange(100) / 6.0 
y = np.sin(x) 
z = np.cos(x) 
plot = multiplot()
plot.set_dataset_cols(x,y,z)
plot.render()
 