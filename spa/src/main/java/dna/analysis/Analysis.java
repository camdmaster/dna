package dna.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import dna.spa.Sequence;

public class Analysis {
	
	private HashMap<String, Sequence> referenceList;
	private HashMap<String, Sequence> assembleList;
	private List<BlastResult> blastList;
	private double minCoverage = 0.9;
	private double minIdentity = 90.0;

	public Analysis() {
		
	}
	
	public Analysis(HashMap<String, Sequence> referenceMap, HashMap<String, Sequence> assembledMap, List<BlastResult> blastList) {
		this.referenceList = referenceMap;
		this.assembleList = assembledMap;
		this.blastList = blastList;
	}
	
	public void analyze() {
		double specificity = calculateSpecificity();
		double sensitivity = calculateSensitivity();
		System.out.println("Specificity = " + specificity);
		System.out.println("Sensitivity = " + sensitivity);
	}
	
	private double calculateSpecificity() {
		HashSet<String> matchedAssemble = new HashSet<String>();
		for(BlastResult br: blastList) {
			String id = br.getQueryID();
			double identity = br.getIdentity();
//			int matchedSize = br.getEndQuery() - br.getStartQuery() - 1;
			int matchedSize = br.getLengthOfAlignment();
			int assembleSeqSize = assembleList.get(id).getString().length();
			double coverage = (double)matchedSize / (double)assembleSeqSize;
			
			if(!matchedAssemble.contains(id) && coverage > minCoverage && identity > minIdentity)
				matchedAssemble.add(id);
		}
		
		System.out.println("Assembly - Matched Sequence: " + matchedAssemble.size() + " Whole Sequence: " + assembleList.size());
		return (double)matchedAssemble.size() / (double)assembleList.size();
	}
	
	private double calculateSensitivity() {
		HashSet<String> matchedReference = new HashSet<String>();
		for(BlastResult br: blastList) {
			String id = br.getReferenceID();
			double identity = br.getIdentity();
//			int matchedSize = br.getEndReference() - br.getStartReference() - 1;
			int matchedSize = br.getLengthOfAlignment();
			int refSeqSize = referenceList.get(id).getString().length();
			double coverage = (double)matchedSize / (double)refSeqSize;
			
			if(!matchedReference.contains(id) && coverage > minCoverage && identity > minIdentity)
				matchedReference.add(id);
		}
		
		System.out.println("Reference - Matched Sequence: " + matchedReference.size() + " Whole Sequence: " + referenceList.size());
		return (double)matchedReference.size() / (double)referenceList.size();
	}
}
