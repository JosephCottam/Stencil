import sys

output = open(sys.argv[2], "w")
input = open(sys.argv[1], "r")

for line in input:
  if (len(line) <80): 
     output.write(line)
  else:
     length =0
     for word in line.split(" "):
        length = length + len(word)
        if (length > 80):
           output.write("\n")
           length = len(word)

        output.write(word)
        output.write(" ")
        length = length+1

input.close()
output.close()
