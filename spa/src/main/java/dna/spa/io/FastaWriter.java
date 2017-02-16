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
		bw.write(">" + seq.getHeader() + "\r\n");
		bw.write(seq.getString() + "\r\n");
		bw.flush();
	}
	
	public void close() throws IOException {
		bw.close();
	}
}
