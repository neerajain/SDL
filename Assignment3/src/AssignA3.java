import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AssignA3 {
	
	public void generateMacroIC() throws Exception {
	
		BufferedReader br = new BufferedReader(new FileReader("input1.asm"));
		
		BufferedWriter MNTbw = new BufferedWriter(new FileWriter("MNT.txt"));
		BufferedWriter PNTABbw = new BufferedWriter(new FileWriter("PNTAB.txt"));
		BufferedWriter KPDTABbw = new BufferedWriter(new FileWriter("KPDTAB.txt"));
		BufferedWriter MDTbw = new BufferedWriter(new FileWriter("MDT.txt"));
		BufferedWriter ICbw = new BufferedWriter(new FileWriter("NoMacroCode.txt"));
		
		String line;
		
		LinkedHashMap<String, Integer> PNTAB = new LinkedHashMap<String, Integer>();
		ArrayList<String> allMacroNames = new ArrayList<String>();
		String macroName = null;
		int pp = 0, kp = 0;
		int ppIndex = 0, kpIndex = 100;
		int mdtp = 1, kpdtp = 100;
		int insideMacroFlag = 0;
		
		while((line = br.readLine()) != null) {
			String split[] = line.split("\\s+");
//			System.out.println(split.length);
			
			if(split[1].equalsIgnoreCase("MACRO")) {
				
				insideMacroFlag = 1;
				
				line = br.readLine();
				
				split = line.split("\\s+");
//				System.out.println(split[1]);
				
				pp = 0;
				kp = 0;
				ppIndex = 0;
				
				macroName = split[1];
				allMacroNames.add(split[1]);
				
				for(int i=2; i<split.length; i++) {
					
					split[i] = split[i].replaceAll("[&,]", "");
					
					if(split[i].contains("=")) {
						kp++;
						
						String keywordParam[] = split[i].split("=");
						
						PNTAB.put(keywordParam[0], ++ppIndex);
						
						if(keywordParam.length == 2) {
							KPDTABbw.write(++kpIndex + "\t" + keywordParam[0] + "\t" + keywordParam[1] + "\n");
						}
						else {
							KPDTABbw.write(++kpIndex + "\t" + keywordParam[0] + "\t-\n");
						}
						
					}
					else {
						pp++;
						PNTAB.put(split[i], ++ppIndex);
					}
				}
				
				// writing to PNTAB
				if(!PNTAB.isEmpty()) {
					PNTABbw.write(macroName + "\n");
					for(Map.Entry<String, Integer> lhm: PNTAB.entrySet()) {
						PNTABbw.write(lhm.getValue() + " " + lhm.getKey() + "\n");
					}
					PNTABbw.write("\n");
				}
				
				MNTbw.write(split[1] + "\t" + pp + "\t" + kp + "\t" + mdtp + "\t" + ((kp==0)?("-"):(kpdtp+1)) + "\n");
				kpdtp = kpIndex;
			}
			else if(split[1].equalsIgnoreCase("MEND")) {
				MDTbw.write(mdtp + "\t" + split[1]);
				MDTbw.write("\n");
				mdtp++;
				
				
				PNTAB.clear();
				
				insideMacroFlag = 0;
				
			}
			else if(insideMacroFlag == 1) {
				MDTbw.write(mdtp + "\t");
				
				for(int i=1; i<split.length; i++) {
					
					if(split[i].contains("&")) {
//						split[i] = split[i].replaceAll("[&,]", "");
//						MDTbw.write("(P, " + PNTAB.get(split[i]) + ")\t");
						
						String op = split[i].replaceAll("[&,]", "");
						
						if(PNTAB.containsKey(op)) {
							if(split[i].contains(",")) {
								MDTbw.write("(P, " + PNTAB.get(op) + "),\t");	
							}
							else {
								MDTbw.write("(P, " + PNTAB.get(op) + ")\t");
							}
							
//							System.out.println(op);
						}
						else {
							if((allMacroNames.contains(split[1])) && (split[i].contains("="))) {
								MDTbw.write(split[i] + "\t");
							}
							else {
								System.out.println("Invalid Parameter : " + op);
							}
						}
					}
					else {
						MDTbw.write(split[i] + "\t");
					}
				}

				MDTbw.write("\n");
				mdtp++;
			}
			else {
				ICbw.write(line + "\n");
			}			
		}
		
		MNTbw.close();
		PNTABbw.close();
		KPDTABbw.close();
		MDTbw.close();
		ICbw.close();
		br.close();
	}
	
	public static void main(String args[]) throws Exception {
		AssignA3 passOne = new AssignA3();
		passOne.generateMacroIC();
	}
}

// 31331 - Om Ingle

