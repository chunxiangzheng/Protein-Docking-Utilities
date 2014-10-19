import os

dir = "IMP_files"
os.chdir(dir)
for root, dirs, files in os.walk(".") :
	for dir in dirs :
		os.chdir(dir)
		os.system("idock.py --cxms=distanceConstraint --patch_dock=/home/czheng/PatchDock --precision=1 a.pdb b.pdb")
		os.chdir("..")

