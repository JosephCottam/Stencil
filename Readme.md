Getting Started
===============

To check out and build Stencil, do the following:

    git clone git@github.iu.edu:jcottam/Stencil.git
    cd Stencil
    ant -f build/build.xml -lib Library/ Release

This creates `Stencil_DDD.zip`, where `DDD` is the current
data. Create a new directory called `Stencil`, and unzip your zip file
into that directory. Now you can run Stencil as follows.

    java -jar Stencil.jar

Refer to Overview.pdf for language syntax and philosophy overview.
Look at the TestData/RegressionImages for many examples.
    
    
Testing
===============

In the Stencil directory you checked out from github, do the following:

ant -f build/build.xml -lib Library/ Test

This will generate a running textual status as well as
generate an html report in directory called testResults.

Note 1: Some test images involve non-deterministic operations.
Such tests attempt to account for this behavior, but will still fail occasionally.
If you re-run the tests and a test only fails *sometimes*, this is probably what you are seeing.
  
Note 2: Some tests fail because this is software under development.