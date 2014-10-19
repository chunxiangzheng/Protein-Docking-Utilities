/**
 * Calculate the distance between two sets of C alpha coordinates
 * @author Bruce Lab
 *
 */
import java.io.*;
import java.util.*;

public class CalculateInterface {
	public static void main(String[] args) throws IOException {
		//retDistances("1DC3.pdb", 'A', "1DC3.pdb", 'B', "test");
		//retDistances("3TGO.pdb", 'A', "3TGO.pdb", 'C', "bamC-D");
		//retDistances("Docking1A3WB-1BQ3D.pdb", 'A', "Docking1A3WB-1BQ3D.pdb", 'B', "1A3WB-1BQ3D");
		//retDistances("O13515A-P39954A.pdb", 'A', "O13515A-P39954A.pdb", 'B', "O13515A-P39954A");
		/*processXLinkDBTable("dockingmodeller.txt", "dockingmodeller.input");
		processXLinkDBTable("dockingpdbexisting.txt", "dockingpdbexisting.input");
		processXLinkDBTable("dockingphyre.txt", "dockingphyre.input");*/
		/*getInterface("dockingmodeller.input", "dockingmodeller.output", "F:/pdb");
		getInterface("dockingpdbexisting.input", "dockingpdbexisting.output", "F:/pdb");
		getInterface("dockingphyre.input", "dockingphyre.output", "F:/pdb");*/
		//analyzeProteinInterface(getProteinDB("dockingphyre.output"), "interfaceAnalysis.out");
		//analyzeProteinInterface("cocrystal.filtered", "interface.length"
				//, "/home/czheng/Script_Pool/benchmark_dataset/");
		
	}
	public static void analyzeProteinInterface(String in, String out, String dir) throws IOException {
		FileReader fr = new FileReader(dir + "/" + in);
		BufferedReader br = new BufferedReader(fr);
		FileOutputStream fout = new FileOutputStream(dir + "/" + out);
		PrintStream ps = new PrintStream(fout);
		String line = br.readLine();
		while (line != null) {
			String[] arr = line.split("\t");
			String[] interfaces = getInterfaceResidues(dir + "/pdb/" + arr[0] + ".pdb", arr[1].charAt(0)
					, dir + "/pdb/" + arr[0] + ".pdb", arr[2].charAt(0));
			ps.println(arr[0] + "\t" + arr[1] + "\t" + arr[2] + "\t" 
					+ interfaces[0].split(",").length + "\t" + interfaces[1].split(",").length);
			line = br.readLine();
		}
		ps.close();
		fout.close();
		br.close();
		fr.close();
	}
	public static void analyzeProteinInterface(Map<String, Protein> proMap, String out) {
		try {
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			for (String key : proMap.keySet()) {
				Protein p = proMap.get(key);
				ArrayList<String> interactors = p.interactors;
				for (int i = 0; i < interactors.size(); i++) {
					for (int j = i + 1; j < interactors.size(); j++) {
						if (interactors.get(i).equals(interactors.get(j))) continue;
						double percent = p.getOverlapPercent(interactors.get(i), interactors.get(j));
						double percent2 = p.getOverlapPercent(interactors.get(j), interactors.get(i));
						ps.println(key + "\t" + interactors.get(i) + "\t" + interactors.get(j) + "\t" + percent + "\t" + percent2);
					}
				}
			}
			ps.close();
			fout.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static Map<String, Protein> getProteinDB(String in) {
		Map<String, Protein> proMap = new HashMap<String, Protein>();
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				if (arr.length < 4) {
					line = br.readLine();
					continue;
				}
				String proA = arr[0];
				String proB = arr[1];
				String interfaceA = arr[2];
				String interfaceB = arr[3];
				String[] arrA = interfaceA.split(",");
				ArrayList<Integer> interfaceA_al = new ArrayList<Integer>();
				String[] arrB = interfaceB.split(",");
				ArrayList<Integer> interfaceB_al = new ArrayList<Integer>();
				for (int i = 0; i < arrA.length; i++) {
					if (!arrA.equals("")) interfaceA_al.add(Integer.valueOf(arrA[i]));
				}
				for (int i = 0; i < arrB.length; i++) {
					if (!arrB.equals("")) interfaceB_al.add(Integer.valueOf(arrB[i]));
				}
				if (proMap.containsKey(proA)) {
					Protein proteinA = proMap.get(proA);
					proteinA.interactors.add(proB);
					proteinA.interfaceRes.add(interfaceA_al);
				} else {
					Protein proteinA = new Protein();
					proteinA.name = proA;
					proteinA.interactors.add(proB);
					proteinA.interfaceRes.add(interfaceA_al);
					proMap.put(proA, proteinA);					
				}
				if (proMap.containsKey(proB)) {
					Protein proteinB = proMap.get(proB);
					proteinB.interactors.add(proA);
					proteinB.interfaceRes.add(interfaceB_al);
				} else {
					Protein proteinB = new Protein();
					proteinB.name = proB;
					proteinB.interactors.add(proA);
					proteinB.interfaceRes.add(interfaceB_al);
					proMap.put(proB, proteinB);					
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return proMap;
	}
	public static void processXLinkDBTable(String in, String out) {
		Set<String> pdbSet = new HashSet<String>();
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			String line = br.readLine();
			line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				if (!pdbSet.contains(arr[11])) {
					//System.out.println(in + "\t" + arr[12] + "\t" + arr[14]);
					pdbSet.add(arr[11]);
					String proA = arr[3];
					String proB = arr[15];
					String chainA = arr[12].split(":")[1].trim();
					String chainB = arr[24].split(":")[1].trim();
					if (!chainA.equals(chainB)) {
						ps.println(proA + "\t" + proB + "\t" + chainA + "\t" + chainB + "\t" + arr[11]);
					}
				}
				line = br.readLine();
			}
			ps.close();
			fout.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static void getInterface(String in, String out, String pdbDir) {
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				char chainA = arr[2].charAt(0);
				char chainB = arr[3].charAt(0);
				String[] interface_res = getInterfaceResidues(pdbDir + "/" + arr[4] + ".pdb", chainA, pdbDir + "/" + arr[4] + ".pdb", chainB);
				ps.println(arr[0] + "\t" + arr[1] + "\t" + interface_res[0] + "\t" + interface_res[1]);
				line = br.readLine();
			}
			ps.close();
			fout.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static String[] getInterfaceResidues(String inA, char chainA, String inB, char chainB) {
		String[] dockingInterface = new String[2];
		Set<Integer> atomSetA = new HashSet<Integer>();
		Set<Integer> atomSetB = new HashSet<Integer>();
		String interface_inA = "";
		String interface_inB = "";
		ArrayList<Atom> atomListA = getCoordinates(inA, chainA);
		ArrayList<Atom> atomListB = getCoordinates(inB, chainB);
		for (Atom atomA : atomListA) {
			for (Atom atomB : atomListB) {
				double distance = calculateDistance(atomA.x, atomA.y, atomA.z, atomB.x, atomB.y, atomB.z);
				if (distance < 10.0) {
					if (!atomSetA.contains(atomA.resNum)) {
						atomSetA.add(atomA.resNum);
						interface_inA += atomA.resNum + ",";
					}
					if (!atomSetB.contains(atomB.resNum)) {
						interface_inB += atomB.resNum + ",";
						atomSetB.add(atomB.resNum);
					}
				}
			}
		}
		dockingInterface[0] = interface_inA;
		dockingInterface[1] = interface_inB;
		return dockingInterface;
	}
	public static void retDistances(String inA, char chainA, String inB, char chainB, String out) {
		ArrayList<Atom> atomListA = getCoordinates(inA, chainA);
		ArrayList<Atom> atomListB = getCoordinates(inB, chainB);
		try {
			FileOutputStream fout = new FileOutputStream(out);
			PrintStream ps = new PrintStream(fout);
			for (Atom atomA : atomListA) {
				for (Atom atomB : atomListB) {
					double distance = calculateDistance(atomA.x, atomA.y, atomA.z, atomB.x, atomB.y, atomB.z);
					ps.println(atomA.residue + "\t" + atomA.resNum + "\t" + atomB.residue + "\t" + atomB.resNum + "\t" + distance);
				}
			}
			ps.close();
			fout.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static double calculateDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
		double distance = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2);
		return Math.sqrt(distance);
	}
	public static ArrayList<Atom> getCoordinates(String in, char chain) {
		ArrayList<Atom> atomList = new ArrayList<Atom>();
		try {
			FileReader fr = new FileReader(in);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while (line != null) {
				if (line.length() < 6) {
					line = br.readLine();
					continue;
				}
				if (!line.substring(0,  6).equals("ATOM  ")) {
					line = br.readLine();
					continue;
				}
				if (line.charAt(21) != chain) {
					line = br.readLine();
					continue;
				}
				if (line.substring(12,16).trim().equals("CA")) {
					Atom atom = new Atom();
					atom.residue = line.substring(17, 20).trim();
					atom.resNum = Integer.valueOf(line.substring(22, 26).trim());
					atom.x = Double.valueOf(line.substring(30, 38).trim());
					atom.y = Double.valueOf(line.substring(38, 46).trim());
					atom.z = Double.valueOf(line.substring(46, 54).trim());
					atomList.add(atom);
				}
				line = br.readLine();
			}
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return atomList;
	}
}
class Protein {
	String name;
	ArrayList<String> interactors;               //name of interacting proteins
	ArrayList<ArrayList<Integer>> interfaceRes;  //interface region of host protein when interacting with other proteins, the index is consistent with interactors list
	public Protein() {
		interactors = new ArrayList<String>();
		interfaceRes = new ArrayList<ArrayList<Integer>>();
	}
	public int getIndex(String s) {
		/**
		 * Get the index of the interacting protein
		 */
		for (int i = 0; i < interactors.size(); i++) {
			if (interactors.get(i).equals(s)) return i;
		}
		return -1;
	}
	public double getOverlapPercent(String a, String b) {
		/**
		 * get the percentage of interface overlap respective to the first protein
		 */
		double percentage = 0;
		int indexA = getIndex(a);
		int indexB = getIndex(b);
		ArrayList<Integer> interfaceA = interfaceRes.get(indexA);
		ArrayList<Integer> interfaceB = interfaceRes.get(indexB);
		int n = interfaceA.size();
		int count = 0;
		Set<Integer> resSet = new HashSet<Integer>();
		for (Integer i : interfaceB) resSet.add(i);
		for (Integer i : interfaceA) {
			if (resSet.contains(i)) count++;
		}
		percentage = (double) count / (double) n;
		return percentage;
	}
}
class Atom {
	int resNum;
	double x,y,z;
	String residue;
	public Atom() {
	}
}
