# Execute a given stencil on a large number of files with a btrace agent/program
# TraceAll <stencil-jar> <btrace file> <input-file-re>
#
# b-trace outputs will be moved to files indicating what was executed with which data set 



import sys
import dircache
import re
import os
from subprocess import call


stencilFile = "./StencilExplore.stencil"

stencilJar = sys.argv[1]
traceFile = sys.argv[2]
inputPattern = sys.argv[3]

inputPattern = re.compile(inputPattern).search

outputFile = traceFile + ".btrace"

allFiles = dircache.listdir(".")
files = filter(inputPattern, allFiles)

#get list of files
for file in files:
    args = ["java", "-javaagent:btrace-agent.jar=script=Tracer.class", "-jar", stencilJar, "-headless", "-open", stencilFile, "-source", "Entities", "./" + file]
    print args
    call(args)
    resultFile = stencilJar + "_" + file + ".btrace"
    os.rename(outputFile, resultFile)
   
