/**
 * 
 * @author czheng
 *
 */
import java.io.*;
import java.util.HashMap;
import java.util.Map;
public class RankPDBInterface {
	public static void main(String[] args) {
		generateInterfaceFile("/home/czheng/Script_Pool/benchmark_dataset/subset");
		batchFreqFiles("/home/czheng/Script_Pool/benchmark_dataset/subset");
		batchScoreFiles("/home/czheng/Script_Pool/benchmark_dataset/subset");
	}
	public static void batchScoreFiles(String dir) {
		File fdir = new File(dir);
		File[] subdirs = fdir.listFiles();
		for (File subdir : subdirs) {
			generateScoreFile(subdir.getPath());
		}
	}
	public static void generateScoreFile(String dir) {
		try {
			FileReader fr = new FileReader(dir + "/frequency");
			BufferedReader br = new BufferedReader(fr);
			Map<String, Integer> scoreMapA = new HashMap<String, Integer>();
			Map<String, Integer> scoreMapB = new HashMap<String, Integer>();
			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				if (arr[0].equals("A")) scoreMapA.put(arr[1], Integer.valueOf(arr[2]));
				else scoreMapB.put(arr[1], Integer.valueOf(arr[2]));
				line = br.readLine();
			}
			br.close();
			fr.close();
			fr = new FileReader(dir + "/interface");
			br = new BufferedReader(fr);
			FileOutputStream fout = new FileOutputStream(dir + "/score");
			PrintStream ps = new PrintStream(fout);
			line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				if (arr.length < 3) {
					line = br.readLine();
					continue;
				}
				String[] interfaceA = arr[1].split(",");
				String[] interfaceB = arr[2].split(",");
				int score = 0;
				for (String s : interfaceA) score += scoreMapA.get(s);
				for (String s : interfaceB) score += scoreMapB.get(s);
				ps.println(arr[0] + "\t" + score);
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
	public static void batchFreqFiles(String dir) {
		File fdir = new File(dir);
		File[] subdirs = fdir.listFiles();
		for (File subdir : subdirs) {
			generateFrequencyFile(subdir + "/interface", subdir.getPath());
		}
	}
	public static void generateFrequencyFile(String interfaces, String dir) {
		try {
			FileReader fr = new FileReader(interfaces);
			BufferedReader br = new BufferedReader(fr);
			FileOutputStream fout = new FileOutputStream(dir + "/frequency");
			PrintStream ps = new PrintStream(fout);
			String line = br.readLine();
			Map<Integer, Integer> chainAMap = new HashMap<Integer, Integer>();
			Map<Integer, Integer> chainBMap = new HashMap<Integer, Integer>();
			while (line != null) {
				String[] arr = line.split("\t");
				if (arr.length < 3) {
					line = br.readLine();
					continue;
				}
				String[] chainA = arr[1].split(",");
				String[] chainB = arr[2].split(",");
				for (String s : chainA) {
					int i = Integer.valueOf(s.trim());
					if (chainAMap.containsKey(i)) chainAMap.put(i, chainAMap.get(i) + 1);
					else chainAMap.put(i, 1);
				}
				for (String s : chainB) {
					int i = Integer.valueOf(s.trim());
					if (chainBMap.containsKey(i)) chainBMap.put(i, chainBMap.get(i) + 1);
					else chainBMap.put(i, 1);
				}
				line = br.readLine();
			}

			for (int s : chainAMap.keySet()) {
				ps.println("A\t" + s + "\t" + chainAMap.get(s));
			}
			for (int s : chainBMap.keySet()) {
				ps.println("B\t" + s + "\t" + chainBMap.get(s));
			}
			ps.close();
			fout.close();
			br.close();
			fr.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	public static void generateInterfaceFile(String folder) {
		try {
			File fdir = new File(folder);
			File[] flist = fdir.listFiles();
			for (File subfolder : flist) {//folder list
				FileOutputStream fout1 = new FileOutputStream(subfolder + "/interface");
				PrintStream ps1 = new PrintStream(fout1);
				for (File f : subfolder.listFiles()) {
					String fname = f.getName();
					String[] arr = subfolder.getName().split("_");
					String chainA = arr[1];
					String chainB = arr[2];
					if (arr[0].equals("reverse")) {
						chainA = arr[2];
						chainB = arr[1];
					}
					String[] interfaces = CalculateInterface.getInterfaceResidues(f.getPath(), chainA.charAt(0), f.getPath(), chainB.charAt(0));
					ps1.println(fname + "\t" + interfaces[0] + "\t" + interfaces[1]);
				}
				ps1.close();
				fout1.close();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
	}
}
