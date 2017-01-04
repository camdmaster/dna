package dna.spa.redundancy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dna.spa.Sequence;

public class IterativeSequenceChecker {

	private ArrayList<Sequence> seqList;
	private int minSizeOfSubSequnece = 10;
	private int maxSizeOfSubSequnece = 20;
	private HashSet<String> checkedSubSequnece;
	private BufferedWriter bw;
//	private StringBuilder out;
	
	public IterativeSequenceChecker(ArrayList<Sequence> sequenceList) {
		seqList = sequenceList;
//		out = new StringBuilder();
	}
	
	public void findPattern() throws IOException {
//		System.out.println(seq.toString());
//		out.append(seq.toString() + "\r\n");
		bw = new BufferedWriter(new FileWriter("F:\\Dropbox\\DNA\\20160929_SPA\\data\\20170103_redundant.txt"));
		for(int i=minSizeOfSubSequnece; i<=maxSizeOfSubSequnece; i++) {
			if(!findPattern(i))
				break;
//			out.append("\r\n");
		}
		bw.close();
//		out.append("\r\n");
//		return out.toString();
	}
	
	private boolean findPattern(int sizeOfSubSequence) throws IOException {
		boolean find = false;
		checkedSubSequnece = new HashSet<String>();
		StringBuilder subStr = new StringBuilder();
		subStr.append("sub " + sizeOfSubSequence + ": ");
		for(int h=0; h<seqList.size(); h++) {
			Sequence seq = seqList.get(h);
			int length = seq.getString().length();

			for(int i=0; i<=length-sizeOfSubSequence; i++) {
				String subSequence = seq.getString().substring(i, i+sizeOfSubSequence);
				if(checkedSubSequnece.contains(subSequence))
					continue;
				else
					checkedSubSequnece.add(subSequence);
				
				int count = findPattern(subSequence, h);
				if(count >= 2) {
					find = true;
//					System.out.println(subSequence + "(" + count + ") ");
					bw.write(subSequence + "(" + count + ") \r\n");
					bw.flush();
//					subStr.append(subSequence + "(" + count + ") ");
				}
			}
		}
		
//		if(find)
//			System.out.println(subStr);
//		out.append(subStr);
		
		
		return find;
	}
	
	private int findPattern(String subSequence, int index) {
		int countPattern = 0;
		for(int i=index; i<seqList.size(); i++) {
			Sequence seq = seqList.get(i);
			countPattern += findPattern(subSequence, seq);
		}
		
		return countPattern;
	}
	
	private int findPattern(String subSequence, Sequence seq) {
//		subSequence = ".*" + subSequence + ".*";
		String wholeSeq = seq.getString();
//		return wholeSeq.matches(".*" + pattern + ".*");
		
		Pattern pattern = Pattern.compile(subSequence);
		Matcher match = pattern.matcher(wholeSeq);
		
		int matchCount = 0;
		while(match.find()) {
			matchCount++;
		}
		
		return matchCount;
	}
	
}
