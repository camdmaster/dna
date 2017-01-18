package dna.spa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
//    		FastaReader reader = new FastaReader("/data2/db/ncbi/bacteria_150414/all_faa/Streptococcus_pyogenes_A20_uid178106/NC_018936.faa");
//    		seqList = reader.read();
//    		reader = new FastaReader("/data2/db/ncbi/bacteria_150414/all_faa/Staphylococcus_aureus_04_02981_uid161969/NC_017340.faa");
//    		seqList.addAll(reader.read());
//    		reader = new FastaReader("/data2/db/ncbi/bacteria_150414/all_faa/Clostridium_difficile_630_uid57679/NC_009089.faa");
//    		seqList.addAll(reader.read());
    		
//			IterativeSequenceChecker ic = new IterativeSequenceChecker(seqList);
//			ic.findPattern();
			
    		Graph graph = makeGraph(seqList);
//    		traverseGraph(graph);
    		printVertexOrderedByCoverage(graph);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	System.out.println("Done.");
    }
    
    private static Graph makeGraph(ArrayList<Sequence> sequenceList) throws IOException {
    	// make graph
    	Graph graph = new Graph();
    	int vertexLength = 12;
    	int edgeLength = vertexLength + 1;
    	
    	for(Sequence seq: sequenceList) {
    		String string = seq.getString();
    		int sequenceLength = string.length();
    		
    		String v = string.substring(0, vertexLength);
    		Vertex vertex = null;
        	if(graph.existVertex(v)) {
    			vertex = graph.getVertex(v);
    			graph.addVertexCoverage(v);
    		} else {
    			vertex = new Vertex(v);
    			graph.addVertex(vertex);
    		}
    		for(int i=0; i<sequenceLength-vertexLength; i++) {
    			String v1 = string.substring(i, i+vertexLength);
    			String v2 = string.substring(i+1, i+vertexLength+1);
    			String ed = string.substring(i, i+edgeLength);
    			
    			Vertex vertex1 = null;
    			Vertex vertex2 = null;
    			
    			if(graph.existVertex(v2)) {
    				vertex2 = graph.getVertex(v2);
    				graph.addVertexCoverage(v2);
    			} else {
    				vertex2 = new Vertex(v2);
    				graph.addVertex(vertex2);
    			}
    			
    			vertex1 = graph.getVertex(v1);
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
    
    private static void printVertexOrderedByCoverage(Graph graph) throws IOException {
//    	BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yjseo/temp/Streptococcus_pyogenes_A20_uid178106_edge.out"));
//    	BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yjseo/temp/Staphylococcus_aureus_04_02981_uid161969_edge.out"));
//    	BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yjseo/temp/Staphylococcus_Streptococcus_Clostridium_edge.out"));
//    	BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yjseo/temp/Clostridium_difficile_630_uid57679_edge.out"));
    	BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yjseo/temp/genus_vertex.out"));
    	
    	List<Vertex> list = graph.getVerticeOrderedByCoverage();
    	for(Vertex vertex: list) {
    		bw.write(vertex.toString() + ", Edge=" + vertex.getEdgeList().size() + "\r\n");
//    		System.out.println(vertex.toString() + ", Edge=" + vertex.getEdgeList().size());
    	}
    	bw.close();
    	
//    	List<Edge> list = graph.getEdgeList();
//    	for(Edge edge: list) {
//    		bw.write(edge.getV1().getString() + "\t" + edge.getV2().getString() + "\t" + edge.getCoverage() + "\r\n");
////    		System.out.println(edge.getV1().getString() + "\t" + edge.getV2().getString() + "\t" + edge.getCoverage());
//    	}
    	bw.close();
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
    	File dir = new File("/data2/db/ncbi/bacteria_150414/all_faa");
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
