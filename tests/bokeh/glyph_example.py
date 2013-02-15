from bokeh import mpl
from bokeh.bbmodel import ContinuumModel
p = mpl.PlotClient('defaultdoc', 'http://localhost:5006', 'nokey')

source = ContinuumModel(
    'ObjectArrayDataSource',
    data = [
        {'x' : 1, 'y' : 5, 'z':3, 'radius':10},
        {'x' : 2, 'y' : 4, 'z':3},
        {'x' : 3, 'y' : 3, 'z':3, 'color':"red"},
        {'x' : 4, 'y' : 2, 'z':3},
        {'x' : 5, 'y' : 1, 'z':3},
        ]
    )
plot = ContinuumModel('Plot')   #Boilerplate: take the source-table name
glyph_renderer = ContinuumModel(
    'GlyphRenderer',                #Render type glyph
    data_source = source.ref(),     #Source table name
    xdata_range = xdr.ref(),        #if there is an x-guide
    ydata_range = ydr.ref(),        #if there is a y-guide
    x = 'x',                        #boiler-plate
    y = 'y',                        #boiler-plate
    color = 'blue',
    glyphs = [{'type' : 'circles'}]  #from bindings
    )         
xdr = ContinuumModel(           #When a glyph render-type is found, make data ranges for all guided fields....how to control the data-range type?
    'DataRange1d', 
    sources = [{'ref' : source.ref(), 'columns' : ['x']}]   #ref is the target table, column is the field
    )

ydr = ContinuumModel(
    'DataRange1d', 
    sources=[{'ref' : source.ref(), 'columns' : ['y']}],
    )

xaxis = ContinuumModel(    
    'LinearAxis',             #From the guide statement
    orientation='bottom',     #From guide-statement metadata
    parent=plot.ref(),        #From context
    data_range=xdr.ref()      #
    )

yaxis = ContinuumModel(
    'LinearAxis', 
    orientation='left',
    parent=plot.ref(),
    data_range=ydr.ref()
    )
plot.set('renderers', [glyph_renderer.ref()]) #Set renderer...
plot.set('axes', [xaxis.ref(), yaxis.ref()])  #Set axes...

p.bbclient.upsert_all([source, plot, xdr, ydr, glyph_renderer, xaxis, yaxis])
p.show(plot)



                   
                                            
