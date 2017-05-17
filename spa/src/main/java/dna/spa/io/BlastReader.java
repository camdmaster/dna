package dna.spa.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
	
	public void readXML(BlastResult br) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		InputSource is = new InputSource(new FileReader(fileName));
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setFeature("http://xml.org/sax/features/namespaces", false);
		factory.setFeature("http://xml.org/sax/features/validation", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);


		Document document = factory.newDocumentBuilder().parse(is);
//		document.getDocumentElement().normalize();

		NodeList nodeList = document.getElementsByTagName("Hsp").item(0).getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node instanceof Element) {
//				if(node.getNodeName().equals("Hsp_qseq"))
//					System.out.println("que: " + node.getTextContent());
//				if(node.getNodeName().equals("Hsp_hseq"))
//					System.out.println("sub: " + node.getTextContent());
				if(node.getNodeName().equals("Hsp_midline"))
					br.setMatchedString(node.getTextContent());
//					System.out.println("mid: " + node.getTextContent());
//				System.out.println(node.getNodeName() + " " + node.getTextContent());
			}
				
		}


	}
	
}
