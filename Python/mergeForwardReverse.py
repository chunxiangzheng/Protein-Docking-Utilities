import os
folderSet = set()
fdir = "subset"
for folder in os.listdir(fdir) :
	folder_pre = folder.split("-")[0]
	if folder_pre in folderSet: continue
	folderSet.add(folder_pre)	
	forwardFolder = fdir + "/" + folder_pre + "-forward/rankedRes"
	reverseFolder = fdir + "/" + folder_pre + "-reverse/rankedRes"
	os.mkdir(folder_pre)
	for f in os.listdir(forwardFolder) :
		newf = f.replace("results_cxms_soap.txt", "forward")
		os.system("mv " + forwardFolder + "/" + f + " " + folder_pre + "/" + newf)
	for f in os.listdir(reverseFolder) :
		newf = f.replace("results_cxms_soap.txt", "reverse")
		os.system("mv " + reverseFolder + "/" + f + " " + folder_pre + "/" + newf)
	
