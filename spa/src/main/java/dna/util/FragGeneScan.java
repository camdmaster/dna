package dna.util;

import java.io.File;

import dna.spa.Preference;

public class FragGeneScan {

	private String inputPath;
	private String outputPath;
	private String trainType;
	private int isWhole;
	private int threads;
	
	public FragGeneScan() {
		
	}
	
	public void runScan() {
		inputPath = Preference.INPUT_DNA_READ_PATH;
		File afile = new File(inputPath);
		String fileName = afile.getName() + ".fgs";
		outputPath = Preference.TEMP_PATH + fileName;
		isWhole = 0;
		threads = 4;
		trainType = "complete";
		
		runCommand();
	}
	
	private void runCommand() {
		String command = Preference.FRAGGENESCAN_PATH + "/FragGeneScan -s " + inputPath + " -o " + outputPath + 
							" -t " + trainType + " -w " + isWhole + " -p " + threads;
		String[] cmd = command.split(" ");
		try {
			Process p = new ProcessBuilder(cmd).start();
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
