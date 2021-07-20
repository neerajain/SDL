import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class MNTData {
	String name;
	int pp, kp, mdtp, kpdtp;
	
	public MNTData(String name, int pp, int kp, int mdtp, int kpdtp) {
		super();
		this.name = name;
		this.pp = pp;
		this.kp = kp;
		this.mdtp = mdtp;
		this.kpdtp = kpdtp;
	}
	
	public int getPp() {
		return pp;
	}
	public int getKp() {
		return kp;
	}
	public int getMdtp() {
		return mdtp;
	}
	public int getKpdtp() {
		return kpdtp;
	}
}

public class AssignA4 {

	// No Nested Macro and no Advanced Macro
	// Only Simple Macro and nested calls to a Macro
	
	HashMap<String, MNTData> MNT;
	ArrayList<String> MDT;
	ArrayList<String> KPDTAB;
	BufferedWriter bw, APTABbw;
	
	private void generateASM() throws Exception {
		
		BufferedReader MNTbr = new BufferedReader(new FileReader("MNT.txt"));
		BufferedReader KPDTABbr = new BufferedReader(new FileReader("KPDTAB.txt"));
		BufferedReader MDTbr = new BufferedReader(new FileReader("MDT.txt"));
		BufferedReader ICbr = new BufferedReader(new FileReader("NoMacroCode.txt"));
		
		bw = new BufferedWriter(new FileWriter("Ouput.txt"));
		APTABbw = new BufferedWriter(new FileWriter("APTAB.txt"));
		
		String line;
		
		MNT = new HashMap<String, MNTData>();
		
		MDT = new ArrayList<String>();
		KPDTAB = new ArrayList<String>();
		
		while((line = MNTbr.readLine()) != null) {
			String split[] = line.split("\\s+");
			if(split[4].equals("-")) {
				MNT.put(split[0], new MNTData(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), 0));
			}
			else {
				MNT.put(split[0], new MNTData(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4])));	
			}
		}
		
		while((line = KPDTABbr.readLine()) != null) {
			KPDTAB.add(line);
		}

		while((line = MDTbr.readLine()) != null) {
			MDT.add(line);
		}
		
		MNTbr.close();
		KPDTABbr.close();
		MDTbr.close();
		
		while((line = ICbr.readLine()) != null) {
			String split[] = line.split("\\s+");

			if(MNT.containsKey(split[1])) {
				expandMacro(split, line, null, null);
			}
			else {
				bw.write(line + "\n");
			}
		}
		
//		System.out.println(MNT + "\n" + MDT + "\n" + KPDTAB);
		
		ICbr.close();
		APTABbw.close();
		bw.close();		
	}
	
	public void expandMacro(String split[], String line, HashMap<Integer, String> parentAPTAB, HashMap<String, Integer> parentAPTABrev) throws Exception {
		HashMap<Integer, String> APTAB = new HashMap<Integer, String>();
		HashMap<String, Integer> APTABrev = new HashMap<String, Integer>();
		
		int pp = MNT.get(split[1]).getPp();
		int kp = MNT.get(split[1]).getKp();
		int mdtp = MNT.get(split[1]).getMdtp();
		int kpdtp = MNT.get(split[1]).getKpdtp();

		int pno = 1;
		int splitCount = 2;
		
//		System.out.println("Expand : " + line);
		
		int i;
		for(i=0; i<pp; i++) {
			
			if(split[splitCount].contains("(P,")) {
				int paraNo = Integer.parseInt(split[splitCount].replaceAll("\\D", ""));
				APTAB.put(pno, parentAPTAB.get(paraNo));
				APTABrev.put(parentAPTAB.get(paraNo), pno);
			}
			else {
				split[splitCount] = split[splitCount].replace(",", "");
				APTAB.put(pno, split[splitCount]);
				APTABrev.put(split[splitCount], pno);	
			}
			
			pno++;
			splitCount++;
		}
		
		int kpdIndex = kpdtp - 101;			// because KPDTAB index start at 101
		for(i=0; i<kp; i++) {
			String parts[] = KPDTAB.get(kpdIndex).split("\\s+");
			APTAB.put(pno, parts[2]);
			APTABrev.put(parts[1], pno);
			pno++;
			kpdIndex++;
		}
		
		for(i=pp+2; i<split.length; i++) {
			split[i] = split[i].replaceAll("[&,]", "");
			String parts[] = split[i].split("=");
			APTAB.put(APTABrev.get(parts[0]), parts[1]);
		}
	
		// Writing APTAB to APTAB.txt
		System.out.println("APTAB (" + split[1] +  ") : " + APTAB + "\n");
		
		APTABbw.write("APTAB (" + split[1] + ")\n");
		for(Map.Entry<Integer, String> lhm: APTAB.entrySet()) {
			APTABbw.write(lhm.getKey() + ". " + lhm.getValue() + "\n");
		}
		APTABbw.write("\n\n");
		// End of Writing APTAB to APTAB.txt
		
		i = mdtp - 1;
		
		while(!MDT.get(i).contains("MEND")) {
			String parts[] = MDT.get(i).split("\\t+");
			
			if(MNT.containsKey(parts[1])) {
				expandMacro(parts, MDT.get(i), APTAB, APTABrev);
			}
			else {
				bw.write("+\t");
				
				for(int j=1; j<parts.length; j++) {
					if(parts[j].contains("(P,")) {
						int paraNo = Integer.parseInt(parts[j].replaceAll("\\D", ""));
						String paraValue = APTAB.get(paraNo);
						if(parts[j].contains("),")) {
							bw.write(paraValue + ",\t");	
						}
						else {
							bw.write(paraValue + "\t");
						}
					}
					else {
						bw.write(parts[j] + "\t");
					}
				}
				bw.write("\n");
			}
			
			i++;
		}
		
		
		APTAB.clear();
		APTABrev.clear();
	}
	
	public static void main(String[] args) throws Exception {
		AssignA4 passTwo = new AssignA4();
		passTwo.generateASM();
	}
}

