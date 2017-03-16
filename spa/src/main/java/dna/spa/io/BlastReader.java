package dna.spa.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dna.analysis.BlastResult;

public class BlastReader {

	String fileName;
	List<BlastResult> blastList;
	
	public BlastReader(String fileName) {
		this.fileName = fileName;
		this.blastList = new ArrayList<BlastResult>();
	}
	
	public List<BlastResult> readTable() throws IOException {
		File file = new File(fileName);
    	BufferedReader br = new BufferedReader(new FileReader(file));
    	String line = br.readLine();
    	while(line != null) {
    		String[] data = line.split("\t");
    		BlastResult bout = new BlastResult();
    		bout.setQueryID(data[0]);
    		bout.setReferenceID(data[1]);
    		bout.setIdentity(Double.parseDouble(data[2]));
    		bout.setLengthOfAlignment(Integer.parseInt(data[3]));
    		bout.setNumMismatch(Integer.parseInt(data[4]));
    		bout.setNumGapopen(Integer.parseInt(data[5]));
    		bout.setStartQuery(Integer.parseInt(data[6]));
    		bout.setEndQuery(Integer.parseInt(data[7]));
    		bout.setStartReference(Integer.parseInt(data[8]));
    		bout.setEndReference(Integer.parseInt(data[9]));
    		bout.setEvalue(Double.parseDouble(data[10]));
    		bout.setBitscore(Double.parseDouble(data[11]));
    		blastList.add(bout);
    		
    		line = br.readLine();
    	}
    	br.close();
    	
    	return blastList;
	}
	
}
