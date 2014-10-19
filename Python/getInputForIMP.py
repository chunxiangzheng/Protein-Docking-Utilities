import os
#create pdb set
pdbset = set()
f = open("benchmark.final", "r")
for line in f:
	arr = line.split("\t")
	if (arr[1] < arr[2]) :
		code = arr[0] + "_" + arr[1] + "_" + arr[2]
	else :
		code = arr[0] + "_" + arr[2] + "_" + arr[1]
	pdbset.add(code)
f.close()
#run through the dataset file and create data file folder
dir = "pdb"
createdDir = set()
f = open("cocrystal", "r")
def copyPDB(pdbin, chain, pdbout) :
	for line in pdbin:
		if (not line.startswith("ATOM")) : continue
		if (line[21 : 22] != chain) : continue
		pdbout.write(line)	
	pdbout.write("TER")

for line in f:
	arr = line.split("\t")
	pdb = arr[6]
	chainA = arr[7].split(":")[1]
	siteA = arr[7].split(":")[0]
	chainB = arr[16].split(":")[1]
	siteB = arr[16].split(":")[0]
	code = pdb
	if (chainA > chainB) :
		tmp = chainA
		chainA = chainB
		chainB = tmp
		tmp = siteA
		siteA = siteB
		siteB = tmp	
	code = code + "_" + chainA + "_" + chainB
	if (not code in pdbset) : continue
	datadir = "IMP_files/" + code
	#make dir, put pdb files in the folder
	if (not code in createdDir) :
		os.chdir("IMP_files")
		os.mkdir(code)
		os.chdir("..")
		fpdb = open(dir + "/" + pdb + ".pdb", "r")
		pdbA = open(datadir + "/a.pdb", "w")
		copyPDB(fpdb, chainA, pdbA)
		pdbA.close()
		fpdb.close()		
		fpdb = open(dir + "/" + pdb + ".pdb", "r")
		pdbB = open(datadir + "/b.pdb", "w")
		copyPDB(fpdb, chainB, pdbB)
		pdbB.close()
		fpdb.close()
		createdDir.add(code)
	#write distanceConstraint file
	distanceConstraint = open(datadir + "/distanceConstraint", "a")
	distanceConstraint.write(siteA + " " + chainA + " " + siteB + " " + chainB + " 7 35\n")
	distanceConstraint.close()

f.close()  	
