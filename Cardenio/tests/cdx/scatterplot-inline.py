from webplot import p 
import numpy as np 
import datetime 
import time

class scatterplot_inline:
  _dataset_fields = ['a', 'b', 'c']
  def __init__(self, *args, **kwargs):
    if (len(args) == len(fields)):
      a = args[0]
      b = args[1]
      c = args[2]
      self.dataset = p.make_source(idx=range(len(a)), a=a, b=b, c=c)
    else:
      self.dataset = **kwargs['dataset']
    p.plot ('a', 'b', color='RED', data_source=self.dataset, scatter=True)

x = np.arange(100) / 6.0 
y = np.sin(x) 
z = np.cos(x) 
plot = Scatterplot(x,y,z)
