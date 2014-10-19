import os
def getKey(item):
	return item[1]
fdir = "subset"
fdirOut = "subset_final"
if not fdirOut in os.listdir(".") : os.mkdir(fdirOut)
for subdir in os.listdir(fdir):
	if not os.path.isdir(fdir + "/" + subdir): continue
	if not subdir in os.listdir(fdirOut): os.mkdir(fdirOut + "/" + subdir)
	f = open(fdir + "/" + subdir + "/score")
	scores = []
	for line in f:
		arr = line.strip().split("\t")
		scores.append((arr[0], int(arr[1])))
	scores = sorted(scores, key = getKey, reverse = True)
	for i in range(0, 100) :
		if i >= len(scores) : break
		filename = scores[i][0]
		os.system("cp " + fdir + "/" + subdir + "/" + filename + " " + fdirOut + "/" + subdir + "/final." + str(i) + "." + filename)
	f.close()
