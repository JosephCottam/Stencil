import sys

def prefix(parts):
    print "// %s pallet of %s elements" % (parts[2].strip(), parts[1])
    print "public static final Color[] %s%s = new Color[]{" % (parts[0].upper(), parts[1])

def terminate():
     print "};\n\n"

def color(parts):
     r=parts[6].strip()
     g=parts[7].strip()
     b=parts[8].strip()
     print "\t new Color(%s, %s, %s)" % (r,g,b),



input = open(sys.argv[1],"r")
input.readLine() 		//skip header...

for line in input:
   parts = line.split("\t")
   
   if (parts[1] == ""):
      print ","
      color(parts)
   else:
      terminate()
      prefix(parts)
      color(parts)
      
terminate()