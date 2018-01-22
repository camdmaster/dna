package dna.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import dna.spa.Preference;
import dna.spa.Sequence;
import dna.spa.io.BlastReader;
import dna.util.Blast;

public class Analysis {
	
	private HashMap<String, Sequence> referenceList;
	private HashMap<String, Sequence> assembleList;
	private List<BlastResult> blastList;
//	private double minCoverage = 0.9;
//	private double minIdentity = 90.0;
	private double specificity;
	private double sensitivity;
	private List<BlastResult> falsePositive;
	private List<BlastResult> falseNegative;
	private List<BlastResult> truePositive;
	private List<BlastResult> trueNegative;

	public Analysis() {
		
	}
	
	public Analysis(HashMap<String, Sequence> referenceMap, HashMap<String, Sequence> assembledMap, List<BlastResult> blastList) {
		this.referenceList = referenceMap;
		this.assembleList = assembledMap;
		this.blastList = blastList;
		falsePositive = new ArrayList<BlastResult>();
		falseNegative = new ArrayList<BlastResult>();
		truePositive = new ArrayList<BlastResult>();
		trueNegative = new ArrayList<BlastResult>();
	}
	
//	public List<BlastResult> getFalseBlastList() {
//		return falseBlastList;
//	}

	public void analyze() {
		calculateSpecificityAndSensitivity(90.0, 0.9);
		System.out.println("Specificity = " + specificity);
		System.out.println("Sensitivity = " + sensitivity);
		Preference.LOG += "Specificity = " + specificity + "\r\n";
		Preference.LOG += "Sensitivity = " + sensitivity + "\r\n\r\n";
		System.out.println();
		
		calculateSpecificityAndSensitivity(90.0, 0.8);
		System.out.println("Specificity = " + specificity);
		System.out.println("Sensitivity = " + sensitivity);
		System.out.println();
		Preference.LOG += "Specificity = " + specificity + "\r\n";
		Preference.LOG += "Sensitivity = " + sensitivity + "\r\n\r\n";
		
		calculateSpecificityAndSensitivity(90.0, 0.7);
		System.out.println("Specificity = " + specificity);
		System.out.println("Sensitivity = " + sensitivity);
		System.out.println();
		Preference.LOG += "Specificity = " + specificity + "\r\n";
		Preference.LOG += "Sensitivity = " + sensitivity + "\r\n\r\n";
		
		calculateSpecificityAndSensitivity(90.0, 0.6);
		System.out.println("Specificity = " + specificity);
		System.out.println("Sensitivity = " + sensitivity);
		System.out.println();
		Preference.LOG += "Specificity = " + specificity + "\r\n";
		Preference.LOG += "Sensitivity = " + sensitivity + "\r\n\r\n";
		
		calculateSpecificityAndSensitivity(90.0, 0.5);
		System.out.println("Specificity = " + specificity);
		System.out.println("Sensitivity = " + sensitivity);
		System.out.println();
		Preference.LOG += "Specificity = " + specificity + "\r\n";
		Preference.LOG += "Sensitivity = " + sensitivity + "\r\n\r\n";
		
		try {
			writeBlastResult("True Positive", truePositive, false);
			writeBlastResult("False Positive", falsePositive, true);
			writeBlastResult("False Negative", falseNegative, true);
			writeBlastResult("True Negative", trueNegative, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void calculateSpecificityAndSensitivity(double minIdentity, double minCoverage) {
		HashSet<String> matchedAssemble = new HashSet<String>();
		HashSet<String> matchedReference = new HashSet<String>();
		
		for(BlastResult br: blastList) {
			String asbID = br.getQueryID();
			String refID = br.getReferenceID();
			double identity = br.getIdentity();
//			int matchedSize = br.getLengthOfAlignment();
			
			int asbMatchSize = br.getEndQuery() - br.getStartQuery() + 1;
			int refMatchSize = br.getEndReference() - br.getStartReference() + 1;
			int asbSeqSize = assembleList.get(asbID).getString().length();
			int refSeqSize = referenceList.get(refID).getString().length();
			double coverageAsb = (double)asbMatchSize / (double)asbSeqSize;
			double coverageRef = (double)refMatchSize / (double)refSeqSize;
			
			br.setLengthQuery(asbSeqSize);
			br.setLengthReference(refSeqSize);
			br.setCoverageQuery(coverageAsb);
			br.setCoverageReference(coverageRef);
			
			if(coverageRef >= minCoverage && identity >= minIdentity) {
				if(!matchedAssemble.contains(asbID))
					matchedAssemble.add(asbID);
				if(!matchedReference.contains(refID))
					matchedReference.add(refID);
			}
			
			if(minCoverage == 0.9) {
//				if(coverageRef >= minCoverage && coverageAsb >= minCoverage)
//					truePositive.add(br);
//				else if(coverageRef >= minCoverage)
//					falseNegative.add(br);
//				else if(coverageAsb >= minCoverage)
//					falsePositive.add(br);
//				else
//					trueNegative.add(br);
				
				if(coverageRef >= minCoverage  && identity >= minIdentity)
					truePositive.add(br);
				else if(coverageAsb >= minCoverage  && identity >= minIdentity)
					falsePositive.add(br);
				else
					trueNegative.add(br);
					
			}
			
		}
		
//		if(minCoverage == 0.9) {
//			
//			Preference.LOG += "false reference: +\r\n";
//			Iterator<BlastResult> iter = truePositive.iterator();
//			while(iter.hasNext()) {
//				String id = iter.next().getReferenceID();
//				for(int i=falsePositive.size()-1; i>=0; i--) {
//					BlastResult br = falsePositive.get(i);
//					String rID = br.getReferenceID();
//					if(rID.equals(id))
//						falsePositive.remove(br);
//				}
//				for(int i=falseNegative.size()-1; i>=0; i--) {
//					BlastResult br = falseNegative.get(i);
//					String rID = br.getReferenceID();
//					if(rID.equals(id))
//						falseNegative.remove(br);
//				}
//				for(int i=trueNegative.size()-1; i>=0; i--) {
//					BlastResult br = trueNegative.get(i);
//					String rID = br.getReferenceID();
//					if(rID.equals(id))
//						trueNegative.remove(br);
//				}
//			}
//		}
		
//		for(String ref: matchedReference)
//			System.out.println(ref);
		specificity = (double)matchedAssemble.size() / (double)assembleList.size();
		sensitivity = (double)matchedReference.size() / (double)referenceList.size();
		System.out.println(">>> min identity: " + minIdentity + " min coverage: " + minCoverage);
		System.out.println("Assembly - Matched Sequence: " + matchedAssemble.size() + " Whole Sequence: " + assembleList.size());
		System.out.println("Reference - Matched Sequence: " + matchedReference.size() + " Whole Sequence: " + referenceList.size());
		Preference.LOG += ">>> min identity: " + minIdentity + " min coverage: " + minCoverage + "\r\n";
		Preference.LOG += "Assembly - Matched Sequence: " + matchedAssemble.size() + " Whole Sequence: " + assembleList.size() + "\r\n";
		Preference.LOG += "Reference - Matched Sequence: " + matchedReference.size() + " Whole Sequence: " + referenceList.size() + "\r\n";
	}
	
	/**
	 * Pair-wise blast alignment
	 * @param asbID
	 * @param refID
	 * @param br
	 */
	private void alignWithBlast(String asbID, String refID, BlastResult br) {
		List<Sequence> querySeq = new ArrayList<Sequence>();
		querySeq.add(assembleList.get(asbID));
		List<Sequence> subjectSeq = new ArrayList<Sequence>();
		subjectSeq.add(referenceList.get(refID));
		
		Blast blast = new Blast(querySeq, subjectSeq);
		blast.runBlast();
		
		BlastReader blr = new BlastReader(Preference.OUTPUT_BLAST_PATH);
		try {
			blr.readXML(br);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(Preference.OUTPUT_ANALYSIS_PATH, true));
			bw.write("Coverage: " + br.getCoverageReference() + ", Identity: " + br.getIdentity());
			bw.newLine();
			bw.write("Asb: " + assembleList.get(asbID).getString());
			bw.newLine();
			bw.write("Ref: " + referenceList.get(refID).getString());
			bw.newLine();
			double asbPercent =  (double)br.getLengthOfAlignment()/(double)assembleList.get(asbID).getString().length() * 100;
			double refPercent =  (double)br.getLengthOfAlignment()/(double)referenceList.get(refID).getString().length() * 100;
			bw.write("Matched Asb: " + br.getStartQuery() + "~" + br.getEndQuery() + " in " + assembleList.get(asbID).getString().length() + " (" + asbPercent + "%)");
			bw.write(", Matched Ref: " + br.getStartReference() + "~" + br.getEndReference() + " in " + referenceList.get(refID).getString().length() + " (" + refPercent + "%)");
			bw.newLine();
			bw.write("Matched Sequence: " + br.getMatchedString());
			bw.newLine();
			bw.newLine();
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeBlastResult(String title, List<BlastResult> bList, boolean append) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(Preference.OUTPUT_ANALYSIS_PATH, append));
		bw.write(title);
		bw.newLine();
		for(BlastResult br: bList) {
			bw.write(br.getQueryID() + "\t" + br.getLengthQuery() + "\t" + br.getStartQuery() + "\t" + br.getEndQuery() + "\t" + br.getCoverageQuery() + "\t");
			bw.write(br.getReferenceID() + "\t" + br.getLengthReference() + "\t" + br.getStartReference() + "\t" + br.getEndReference() + "\t" + br.getCoverageReference() + "\t");
			bw.write(Double.toString(br.getIdentity()));
			bw.newLine();
		}
		bw.newLine();
		bw.close();
	}
	
}
