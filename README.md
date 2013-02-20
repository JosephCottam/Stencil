Stencil
=======

Stencil Visualization System


A description of the current state of the Stencil system.  
This document will be revised as reality aligns with plans (or vice-versa).

Sample Program
==============
(stencil example
   (table data
      (fields a b)
      (data (init (let (a : (list 1 2 3 4))
                       (b : (list 10 20 30 40)))))
      (render Scatterplot (bind x:a y:b))))

Grammar
=======

There are three main statement types in Stencil: Contexts, Policies and Expressions.
These statement types can be nested as follows:  
Contexts can contain contexts or policies.  
Policies can contain expressions.
Expressions can contain other expressions.

Contexts types are: stencil, import, table, render and view (we have plans for operator and stream contexts as well).
Though contexts can be nested, valid nestings depend on the context type.
Stencil contexts can contain any other context time (no nested stencils...yet).
Tables can contain renders.
Imports, Renders and Views can't contain other contexts.


Contexts provide an execution environment (a "context" if you will) for policies.
Policies describe attributes of the context.
Valid policy types depend on the context type.
Since the majority of the work is done by policies, 
the policies will be described in detail at this time.

Table contexts can contain *data* and *fields* policies.
The fields policy describes the fields of the table.  
Minimally, it is a list of names.
The data policy describes where/when to acquire data from and how to transform it into a row for the table
   (e.g., how to convert source data to the fields for the current table).
The data policy must start (e.g., have as its top element) some expresion that describes when and where to get data from.
The only valid expression right now is *(init ...)*, indicating that the actions should be done when the stencil program is initialized.
The data policy must result in one more tuples produced via *tuple*, *ptuple*, *tuples* or *ptuples*.
In the example above, the tuple-production is automatically generated from the fields of the let.
Explicit inclusion would look like *(ptuples (fields a b) a b)*.

The render declaration is a policy that expands to a corresponding context.
It indicates a rendering type (Scatterplot) and 
  a set of field bindings from the source table to the target rendering type.
Since it is in the context of a table, it implicitly targets the containing table.
As a context-declaration, 
  the renderer would need to be named and the the table would need to be indicated *(render myRenderer data Scatterplot ...)*.
This context form is valid inside of the table, but not suggested.

Imports recognize two policies: *as* and *only*.
The "as" policy provides a name proxy.  
The "only" policiy limits what is imported.
The two most important policies are "fields" and "data".
Fields policies describe the valid fields of a table.

View declarations compose multiple renderers together.  
If there is no view declaration, one composed of all defined renderers is made instead.

Expressions inhabit policies.  There are many expression types.
