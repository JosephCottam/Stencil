Stencil
=======


Stencil is a language-based visualization system.  It pulls heavily from "coordination" style languages,
providing tools to build visualizations and workflows to supply data to those visualizations.  The visualization
facilities are similar to those found Bokeh, Protovis or D3.  

In the Stencil project, we are pursuing a means for building high-performance, flexible visualizations
that can run on a variety of platforms.  Stencil itself attempts to be runtime-agnostic, but provide
facilities to interact with the specific features of a particular runtime when desired.
To achieve this goal, Stencil follows the little-langauge philosophy and relies heavily on building
abstractions on top of a the small core language.



Technology
=========

Stencil is implemented as a compiler in Clojure. As a compiler, a dependency on Stencil is 
expressed in the build-chain.  Depending on the compilation target, there may or may not be a 
runtime dependency.

Stencil uses [Lieningen](https://github.com/technomancy/leiningen) for build management.
Therefore, the technical dependencies are handled through it, (and maven under the hood).
AST manipulation is done with [Clojure's match](https://github.com/clojure/core.match).
The Stencil compiler uses [StringTemplate](http://www.stringtemplate.org/) for code generation.

The current most-complete compilation is target is Bokeh (a Python visualization system).


Syntax
======

Stencil statements are build around s-expressions, extended for infix operators.
A top-level "stencil" statement is optional, and will be supplied if omitted.

```
(stencil scatterplot
  (table data
    (fields a b c)
    (data (file "./dataset.csv"))
    (render scatter (bind (x:a) (y:b) (color:c)))))
```    

Tables define data storage locations.  They are logically collections of tuples.
The **table** statement names a table.  
The **fields** statement defines the fields of the tuple (there can only be one fields statement per table).
Since visualizations are based on data collections, complete stencil programs include at least one table.
A **data** statement indicates where to acquire data from and how to process it into the table.
Data statements have one expression in them, a "generator." 
The generator in the above program indicates that data should be loaded out of a file.
In the above program, it will be loaded from a csv file. 
Generator statements may also include data transformation rules.
The **render** statement relates a table to a renderer.
It includes a renderer type ('scatter') and a relationship between the render type and the source data in a **bind** statement.
The bind statement includes name pairs, the name on the left is a target in the renderer, the name on the right is a source in the table. 


```
(stencil scatterplot
  (render _ project scatter (bind (x:a) (y:b) (color:c)))

  (table raw (fields a b c))
  (table project
    (fields a b c)
    (data 
      (pull raw
        (let (a : (scale 0 100 (a+10)) )
             (b : (scale 0 100 b))
             (c : (happyColors c)))))))
```

The above program shows some additional features of Stecnil.
The render statement can be lifted out of a table.  
If it is, the resulting renderer must be named (underscore in this case, is the universal throw-away name).
The target of the rendering must also be indicated ('project' in this case).
The 'raw' table is stripped down.
Without a data statement, code to support loading data from a host program is generated instead.
The 'project' table has a **pull** statement for a generator.
This indicates that this table depends on some other table (in this case 'raw').
The pull statement is similar to a for-each, but does not guarnatee iteration order
  (and will eventually support incremental updates).
_Only two generators currently work: external loading and a 'pull' generator targeted at another table._
Pull statements have two clauses: a source table and a transformation.
The transofrmation here is a 'let' statement.  Let statements composed of name/expresion pairs and a body.
(If no body is supplied, as seen here, a tuple is generated with the fields and values of the let.)
Each tuple produced by the let statement is stored in the project table.

As seen in the projection of 'a', stencil supports infix operators. 
Any operator that is all-symbols can be used infix (+,-, etc.)
The presented line is indentical to (scale 0 100 (+ a 10))

Plans and Dreams
================

Stencil is an evolving system.  We have plans for 
delcarative interaction specifciation (including CMV),
visual integration (reactive to the actual device being plotted on),
higher-level grammars (such as Grammar of Graphics)
and an expressive guide system.

From a technilogical standpoint, we are looking at
an optional inference-drive static type system,
streaming data support and
an in-situ HPC runtime (among other things).

