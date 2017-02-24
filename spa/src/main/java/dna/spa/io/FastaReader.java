package dna.spa.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import dna.spa.Sequence;

public class FastaReader {

	String fileName;
	
	public FastaReader(String fileName){
		this.fileName = fileName;
	}
	
	public ArrayList<Sequence> read() throws IOException {
//		String fileName = "F:\\Dropbox\\DNA\\20160929_SPA\\data\\20161216_protein_3_read.faa";
    	File file = new File(fileName);
    	BufferedReader br = new BufferedReader(new FileReader(file));
    	ArrayList<Sequence> sequenceList = new ArrayList<Sequence>();
    	int count = 0;
    	// get read
    	String line = br.readLine();
    	while(line != null && count != 150000) {
    		String header = line.substring(1);
    		StringBuilder seq = new StringBuilder();
    		line = br.readLine();
    		while(line != null && !line.startsWith(">")) {
    			seq.append(line);
    			line = br.readLine();	
    		}
//    		System.out.println(header);
    		count++;
//    		System.out.println(count);
    		Sequence sequence = new Sequence(header, seq.toString());    		
    		sequenceList.add(sequence);
    	}
    	br.close();
    	
    	return sequenceList;
	}
}
