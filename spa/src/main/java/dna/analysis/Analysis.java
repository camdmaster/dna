package dna.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
	private double minCoverage = 0.9;
	private double minIdentity = 90.0;
	private double specificity;
	private double sensitivity;
	private List<BlastResult> falseBlastList;

	public Analysis() {
		
	}
	
	public Analysis(HashMap<String, Sequence> referenceMap, HashMap<String, Sequence> assembledMap, List<BlastResult> blastList) {
		this.referenceList = referenceMap;
		this.assembleList = assembledMap;
		this.blastList = blastList;
		falseBlastList = new ArrayList<BlastResult>();
	}
	
	public List<BlastResult> getFalseBlastList() {
		return falseBlastList;
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
			
			int asbSeqSize = assembleList.get(asbID).getString().length();
			int refSeqSize = referenceList.get(refID).getString().length();
			double coverageAsb = (double)matchedSize / (double)asbSeqSize;
			double coverageRef = (double)matchedSize / (double)refSeqSize;
			
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
//			else if (assembleList.get(asbID).getString().length() > refSeqSize && identity >= minIdentity) {
//				System.out.println("Coverage: " + coverage + ", Identity: " + identity);
//				System.out.println("Asb: " + assembleList.get(asbID).getString());
//				System.out.println("Ref: " + referenceList.get(refID).getString());
//			}
			if(coverageRef < minCoverage && identity >= minIdentity) {
				falseBlastList.add(br);
//				System.out.println(br.getQueryID() + " "+ br.getReferenceID());
			}
		}
		
		specificity = (double)matchedAssemble.size() / (double)assembleList.size();
		sensitivity = (double)matchedReference.size() / (double)referenceList.size();
		System.out.println("Assembly - Matched Sequence: " + matchedAssemble.size() + " Whole Sequence: " + assembleList.size());
		System.out.println("Reference - Matched Sequence: " + matchedReference.size() + " Whole Sequence: " + referenceList.size());
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
	
	public void writeBlastResult() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(Preference.OUTPUT_ANALYSIS_PATH));
		for(BlastResult br: falseBlastList) {
			bw.write(br.getQueryID() + "\t" + br.getLengthQuery() + "\t" + br.getStartQuery() + "\t" + br.getEndQuery() + "\t" + br.getCoverageQuery() + "\t");
			bw.write(br.getReferenceID() + "\t" + br.getLengthReference() + "\t" + br.getStartReference() + "\t" + br.getEndReference() + "\t" + br.getCoverageReference());
			bw.newLine();
		}
		bw.close();
	}
	
}
