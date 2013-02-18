#Stencil->Bokeh export for multiplot
from bokeh import mpl 
p = mpl.PlotClient('defaultdoc', 'http://localhost:5006', 'nokey')
import numpy as np


class dataset__:
  _fields = ['a', 'b', 'c']
  a = []
  b = []
  c = []

  def __init__(self):
     (x) = np.arange(100) / 6.0
     (y) = np.sin(x)
     (z) = np.cos(x)
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


class multiplot:
  dataset = None

  def __init__(self):
    #Create tables
    self.dataset = dataset__()
    #Share tables with each other

  def set_dataset_cols(self, a, b, c):
    self.dataset.clear()
    self.dataset.data(a, b, c)


  def render(self):
    p.table(self.dataset.dataSource(), ['a', 'b', 'c'])
    p.scatter(data_source=self.dataset.dataSource(), x='a', y='b', color='orange')
    p.figure()
    p.scatter(data_source=self.dataset.dataSource(), x='a', y='c', color='red')
    p.figure()
    p.plot(data_source=self.dataset.dataSource(), x='a', y='b', color='yellow')
    p.figure()
    p.plot(data_source=self.dataset.dataSource(), x='a', y='c', color='black')
    p.figure()
    p.plot(data_source=self.dataset.dataSource(), x='a', y='b', color='blue')
    p.plot(data_source=self.dataset.dataSource(), x='a', y='c', color='green')
    p.figure()

if __name__ == "__main__":
  plot = multiplot()
  plot.render()
 
