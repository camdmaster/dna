package dna.spa;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import dna.spa.io.FastaReader;



/**
 * Start Point
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	// target sequence
    	ArrayList<Sequence> seqList = null;
    	
    	try {
    		seqList = makeTargetSequence();
//    		FastaReader reader = new FastaReader("F:\\Dropbox\\DNA\\20160929_SPA\\data\\20161216_protein_3_read.faa");
//    		seqList = reader.read();
    		
//			IterativeSequenceChecker ic = new IterativeSequenceChecker(seqList);
//			ic.findPattern();
			
    		Graph graph = makeGraph(seqList);
    		printVertexOrderedByCoverage(graph);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    }
    
    private static Graph makeGraph(ArrayList<Sequence> sequenceList) throws IOException {
    	// make graph
    	Graph graph = new Graph();
    	int vertexLength = 12;
    	int edgeLength = vertexLength + 1;
    	for(Sequence seq: sequenceList) {
    		String string = seq.getString();
    		int sequenceLength = string.length();
    		for(int i=0; i<sequenceLength-vertexLength; i++) {
    			String v1 = string.substring(i, i+vertexLength);
    			String v2 = string.substring(i+1, i+vertexLength+1);
    			String ed = string.substring(i, i+edgeLength);
    			Vertex vertex1 = null;
    			Vertex vertex2 = null;
    			
    			if(graph.existVertex(v1)) {
    				vertex1 = graph.getVertex(v1);
    				graph.addVertexCoverage(v1);
    			} else {
    				vertex1 = new Vertex(v1);
    				graph.addVertex(vertex1);
    			}
    			
    			if(graph.existVertex(v2)) {
    				vertex2 = graph.getVertex(v2);
    				graph.addVertexCoverage(v2);
    			} else {
    				vertex2 = new Vertex(v2);
    				graph.addVertex(vertex2);
    			}
    			
    			if(graph.existEdge(ed)) {
    				graph.addEdgeCoverage(ed);
    			} else {
    				Edge edge = new Edge(vertex1, vertex2, ed);
    				vertex1.addEdge(edge);
    				vertex2.addEdge(edge);
    				graph.addEdge(edge);
    			}
    		}
    	}
    	graph.setFValue();
    	
    	return graph;
    }
    
    private static void printVertexOrderedByCoverage(Graph graph) {
    	List<Vertex> list = graph.getVerticeOrderedByCoverage();
    	for(Vertex vertex: list) {
    		System.out.println(vertex.toString());
    	}
    }
    
    private static void traverseGraph(Graph graph) {
    	// find seed
    	List<Vertex> seeds = graph.getSeedVertex();
    	
    	for(int i=0; i<seeds.size(); i++) {
        	// traverse right
    		Vertex seed = seeds.get(i);
        	String whole = graph.traverseV1(seed, new StringBuilder(seed.getString()));
        	whole = graph.traverseV2(seed, new StringBuilder(whole));
        	graph.resetVisited();
        	
        	System.out.println(i + "\t" + whole);
    	}
    }
    
    
    private static ArrayList<Sequence> makeTargetSequence() throws IOException {
    	File dir = new File("F:\\학교\\all_faa");
    	HashSet<String> speciesName = new HashSet<String>();
    	ArrayList<Sequence> targetSeq = new ArrayList<Sequence>();
    	int count = 0;
    	for(File d: dir.listFiles()) {
    		String name = d.getName();
    		int idx = name.indexOf("_");
//    		idx = name.indexOf("_", idx+1);
    		if(idx < 0)
    			continue;
    		
    		String subName = name.substring(0, idx);
    		if(speciesName.contains(subName))
    			continue;

    		System.out.print(name + "\t");
    		speciesName.add(subName);
    		for(File dd: d.listFiles()) {
    			FastaReader reader = new FastaReader(dd.getAbsolutePath());
    			ArrayList<Sequence> temp = reader.read();
    			System.out.print(temp.size() + "\t");
    			targetSeq.addAll(temp);
    		}
    		count++;
    		System.out.println(subName + " " + count);
    		
//    		if(count == 1)
//    			break;
    	}
    	
    	return targetSeq;
    }
    
}
