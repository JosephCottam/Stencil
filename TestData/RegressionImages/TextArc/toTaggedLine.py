import sys

input = open(sys.argv[1], "r")
output = open(sys.argv[2], "w")

output.write("line, word\n")

lineNum =-1
for line in input:
  lineNum = lineNum +1
  for word in line.strip().split(" "):
    output.write('{0!r}, {1}\n'.format(lineNum, word))

output.close()
input.close()
