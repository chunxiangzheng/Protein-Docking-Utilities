import os
import re

def fdrOnRMSD(fin, fout, numMax) :	
	
	fout = open(fout, "w")
	for i in range(1, numMax) : 
		f = open(fin, "r")		
		dict1 = dict()
		for line in f:
			arr = re.split("[\t/]", line)
			if (arr[0] in dict1) :
				if (float(arr[-1]) < i) :
					dict1[arr[0]] = True
			else :
				dict1[arr[0]] = False
				if (float(arr[-1]) < i) : 
					dict1[arr[0]] = True
		count = 0
		for v in dict1.values() :
			if (v) : 
				count = count + 1
		fout.write(str(i) + "\t" + str(count) + "\n")
		f.close()
	fout.close()

def fdrOnTopN(fin, fout, rmsd, n) :
	fout = open(fout, "w")
	for i in range(1, n) :
		f = open(fin, "r")		
		dict1 = dict()
		fname = ""
		index = 0
		for line in f:
			arr = re.split("[\t/]", line)
			#print(arr[0] + "\t" + arr[-2] + "\t" + arr[-1] + "\t" + str(i) + "\n")
			if arr[0] != fname :
				index = 0
				dict1[arr[0]] = False
				fname = arr[0]				
			index = index + 1
			if (arr[0] in dict1) :
				if (float(arr[-1]) < rmsd and index <= i) :
					dict1[arr[0]] = True
			
		count = 0
		for v in dict1.values() :
			if (v) : 
				count = count + 1
		fout.write(str(i) + "\t" + str(count) + "\n")
		f.close()
	fout.close()
