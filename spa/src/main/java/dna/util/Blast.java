package dna.util;

import java.io.IOException;
import java.util.List;

import dna.spa.Preference;
import dna.spa.Sequence;
import dna.spa.io.FastaWriter;

public class Blast {

//	private String blastp = "blastp.exe";
	private String blastp = "blastp";
	private String queryPath;
	private String subjectPath;
	private String outPath;
	private double evalue;
	private int outfmt;
	private int threads;
	private boolean searchDB;
	
	private List<Sequence> querySeqList;
	private List<Sequence> subjectSeqList;
	
	public Blast(List<Sequence> queryList) {
		evalue = 1;
		threads = 4;
		outfmt = 5;
		this.querySeqList = queryList;
		searchDB = true;
	}
	
	public Blast(List<Sequence> queryList, List<Sequence> targetList) {
		this(queryList);
		this.subjectSeqList = targetList;
		searchDB = false;
	}
	
	public Blast(String queryPath) {
		this.queryPath = queryPath;
		evalue = 1;
		threads = 64;
		outfmt = 6;
		searchDB = true;
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
		if(querySeqList != null)
			makeFastaFile(querySeqList, true);
		if(!searchDB) {
			makeFastaFile(subjectSeqList, false);
		}
		outPath = Preference.OUTPUT_BLAST_PATH;
	}
	
	private void makeFastaFile(List<Sequence> seqList, boolean isQuery) throws IOException {
		String filePath;
		if(isQuery) {
			queryPath = Preference.TEMP_PATH + "query.faa";
			filePath = queryPath;
		} else {
			subjectPath = Preference.TEMP_PATH + "subject.faa";
			filePath = subjectPath;
		}
			
		FastaWriter fw = new FastaWriter(filePath);
		for(Sequence seq: seqList)
			fw.write(seq);
		fw.close();
	}
	
	private void runCommand() {
		String command;
		if(searchDB) {
			command = Preference.BLAST_PLUS_PATH + blastp + " -query " + queryPath + " -db " + Preference.INPUT_BLASTDB_PATH +
			" -out " + outPath + " -evalue " + evalue + " -outfmt " + outfmt + " -max_target_seqs 1 -num_threads " + threads;			
		} else {
			command = Preference.BLAST_PLUS_PATH + blastp + " -query " + queryPath + " -subject " + subjectPath +
					" -out " + outPath + " -evalue " + evalue + " -outfmt " + outfmt + " -max_target_seqs 1 -num_threads " +threads;	
		}

		
		String[] cmd = command.split(" ");
		try {
			Process p = new ProcessBuilder(cmd).start();
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
