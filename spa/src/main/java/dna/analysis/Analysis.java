package dna.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import dna.spa.Sequence;

public class Analysis {
	
	private HashMap<String, Sequence> referenceList;
	private HashMap<String, Sequence> assembleList;
	private List<BlastResult> blastList;
	private double minCoverage = 0.9;
	private double minIdentity = 90.0;
	private double specificity;
	private double sensitivity;

	public Analysis() {
		
	}
	
	public Analysis(HashMap<String, Sequence> referenceMap, HashMap<String, Sequence> assembledMap, List<BlastResult> blastList) {
		this.referenceList = referenceMap;
		this.assembleList = assembledMap;
		this.blastList = blastList;
	}
	
	public void analyze() {
		calculateSpecificityAndSensitivity();
		System.out.println("Specificity = " + specificity);
		System.out.println("Sensitivity = " + sensitivity);
	}
	
	private void calculateSpecificityAndSensitivity() {
		HashSet<String> matchedAssemble = new HashSet<String>();
		HashSet<String> matchedReference = new HashSet<String>();
		
		for(BlastResult br: blastList) {
			String asbID = br.getQueryID();
			String refID = br.getReferenceID();
			double identity = br.getIdentity();
			int matchedSize = br.getLengthOfAlignment();
			int refSeqSize = referenceList.get(refID).getString().length();
			double coverage = (double)matchedSize / (double)refSeqSize;
			
			if(coverage >= minCoverage && identity >= minIdentity) {
				if(!matchedAssemble.contains(asbID))
					matchedAssemble.add(asbID);
				if(!matchedReference.contains(refID))
					matchedReference.add(refID);
			}
//			else if (assembleList.get(asbID).getString().length() > refSeqSize && identity >= minIdentity) {
//				System.out.println("Coverage: " + coverage + ", Identity: " + identity);
//				System.out.println("Asb: " + assembleList.get(asbID).getString());
//				System.out.println("Ref: " + referenceList.get(refID).getString());
//			}
//			if(coverage < minCoverage && identity >= minIdentity) {
//				System.out.println("Coverage: " + coverage + ", Identity: " + identity);
//				System.out.println("Asb: " + assembleList.get(asbID).getString());
//				System.out.println("Ref: " + referenceList.get(refID).getString());
//			}
		}
		
		int count = 0;
		Iterator<String> iter = referenceList.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			if(!matchedReference.contains(key)) {
				count++;
				System.out.println(count + " Ref: " + referenceList.get(key).getHeader());
			}
		}
		
		specificity = (double)matchedAssemble.size() / (double)assembleList.size();
		sensitivity = (double)matchedReference.size() / (double)referenceList.size();
		System.out.println("Assembly - Matched Sequence: " + matchedAssemble.size() + " Whole Sequence: " + assembleList.size());
		System.out.println("Reference - Matched Sequence: " + matchedReference.size() + " Whole Sequence: " + referenceList.size());
	}
	
}
