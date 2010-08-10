# Generate files with sequential numbers in them
#   dataSets.py <start> <stop> <step>
# 
#
# Number of elements will go from start to the largest value that satisfies start+(n*step) < stop
# Files will be named count_<size>.txt


import sys

start = int(sys.argv[1])
stop = int(sys.argv[2])
step = int(sys.argv[3])

for base in range(start,stop,step):
   name = "count_" + str(base) + ".txt"
   file = open(name, "w")
   for i in range(0, base):
      file.write(str(i))
      file.write("\n")
   file.close
      