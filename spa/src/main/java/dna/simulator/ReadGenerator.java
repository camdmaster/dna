package dna.simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dna.spa.Preference;
import dna.spa.Sequence;
import dna.spa.io.FastaReader;

public class ReadGenerator {
	
	private List<Sequence> sequenceList;
	private int coverage;
	private int readLength = Preference.READ_LENGTH;
	
	public static void main(String[] args) {
		FastaReader reader = new FastaReader("/data1/yjseo/data_spa/ds3/faa_collection/NC_006814.faa");
		try {
			List<Sequence> seqList = reader.read();
			ReadGenerator gen = new ReadGenerator(seqList, 30);
			gen.generateRead();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public ReadGenerator(List<Sequence> sequenceList, int coverage) {
		this.sequenceList = sequenceList;
		this.coverage = coverage;
	}
	
	public List<Sequence> generateRead() {
		List<Sequence> readList = new ArrayList<Sequence>();
		for(Sequence seq: sequenceList) {
			String string = seq.getString();
			if(string.length() <= readLength) {
				for(int i=0; i<coverage; i++) {
					int num = i+1;
					String header = "read_" + seq.getHeader() + "_" + num; 
	    			Sequence read = new Sequence(header, string);
	    			readList.add(read);	
				}
    			continue;
			}
			
			int genomeLength = string.length();
			int readSize = (int)Math.ceil(coverage * (double)genomeLength / (double)readLength);
			int iteration = coverage / 10;
			int readSizePerIter = readSize/iteration;
			
			double pushSeq = 3.4;
			int gap = genomeLength / readSizePerIter;
			int count = 0;
			for(int i=0; i<iteration; i++) {
				count++;
				readList.add(makeRead(seq, 0, count));
				for(int j=i*(int)Math.round(pushSeq); j<=string.length()-readLength; j=j+gap) {
					count++;
					readList.add(makeRead(seq, j, count));
				}
				count++;
				readList.add(makeRead(seq, seq.getString().length()-readLength, count));
			}
		}
		return readList;
	}
	
	private Sequence makeRead(Sequence gene, int location, int readNumber) {
		String header = "read_" + gene.getHeader() + "_" + readNumber; 
		String readString = gene.getString().substring(location, location+readLength);
		return new Sequence(header, readString);
	}
}
