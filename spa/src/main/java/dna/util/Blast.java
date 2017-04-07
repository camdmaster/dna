package dna.util;

import java.io.IOException;
import java.util.List;

import dna.spa.Preference;
import dna.spa.Sequence;
import dna.spa.io.FastaWriter;

public class Blast {

	private String blastp = "blastp.exe";
	private String queryPath;
	private String databasePath;
	private String outPath;
	private double evalue;
	private int outfmt;
	private int threads;
	
	private List<Sequence> seqList;
	
	public Blast(List<Sequence> seqList) {
		evalue = 1;
		threads = 4;
		outfmt = 6;
		this.seqList = seqList;
	}
	
	public void runBlast() {
		try {
			prepareBlast();
			runCommand();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void prepareBlast() throws IOException {
		makeQueryFile(seqList);
		databasePath = "F:\\Dropbox\\DNA\\20160929_SPA\\database\\NC_014034.faa";
		outPath = Preference.TEMP_PATH + "blast.out";
	}
	
	private void makeQueryFile(List<Sequence> seqList) throws IOException {
		queryPath = Preference.TEMP_PATH + "query.faa";
		FastaWriter fw = new FastaWriter(queryPath);
		for(Sequence seq: seqList)
			fw.write(seq);
		fw.close();
	}
	
	private void runCommand() {
		String command = "\"" + Preference.BLAST_PLUS_PATH + blastp + "\" -query " + queryPath + " -db " + databasePath +
				" -out " + outPath + " -evalue " + evalue + " -outfmt " + outfmt + " -num_threads " + threads;
		try {
			Process p = new ProcessBuilder(command).start();
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
