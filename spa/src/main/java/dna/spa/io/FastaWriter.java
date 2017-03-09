package dna.spa.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import dna.spa.Sequence;

public class FastaWriter {

	BufferedWriter bw;
	
	public FastaWriter(String fileName) throws IOException {
		bw = new BufferedWriter(new FileWriter(fileName));
	}
	
	public void write(Sequence seq) throws IOException {
		int wrapSize = 70;
		bw.write(">" + seq.getHeader() + "\r\n");
		String seqString = seq.getString();
		int count = (int)Math.ceil((double)seqString.length()/(double)wrapSize);
		for(int i=0; i<count-1; i++) {
			bw.write(seqString.substring(i*wrapSize, (i+1)*wrapSize) + "\r\n");	
		}
		bw.write(seqString.substring((count-1)*wrapSize) + "\r\n");
		bw.flush();
	}
	
	public void close() throws IOException {
		bw.close();
	}
}
