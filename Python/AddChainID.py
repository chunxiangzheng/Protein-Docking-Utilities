import os
def addChain(f):
	fin = open(f, "r")
	fout = open(f + ".new" ,"w")
	isChainB = False
	for line in fin:
		if line.startswith("ATOM"):
			if isChainB: fout.write(line[0:21] + "B" + line[22:])
			else : fout.write(line[0:21] + "A" + line[22:])
		else: 
			fout.write(line)
			if line.startswith("TER") : isChainB = True	
	fout.close()
	fin.close()
def processFolder(fdir):	
	for f in os.listdir(fdir):
		if f.startswith("result"):
			addChain(fdir + "/" + f)

