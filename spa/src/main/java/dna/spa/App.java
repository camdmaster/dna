package dna.spa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import dna.analysis.Analysis;
import dna.analysis.BlastResult;
import dna.assembly.Assembly_SimpleGraphMethod;
import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Vertex;
import dna.spa.io.BlastReader;
import dna.spa.io.FastaReader;
import dna.spa.io.FastaWriter;



/**
 * Start Point
 *
 */
public class App 
{
    public static void main( String[] args )
    {
		// depth first search
//		Graph graph = makeGraph(readList);
//		traverseGraph(graph);

		// SPA based search
//		Graph graph = GraphGenerator.generate(readList);
//		SequenceGenerator sg = new SequenceGenerator(graph);
//		sg.traverseGraph();
    	
    	// job start
    	long startTime = System.nanoTime();
    	
//    	assembleRead();
    	
    	try {
			Analyze();
		} catch (IOException e) {
			e.printStackTrace();
		}

    	long endTime = System.nanoTime();
    	long lTime = endTime - startTime;
    	System.out.println("Overall TIME : " + lTime/1000000.0 + " (ms)");
    	System.out.println("Done.");
    }

    /**
     * Assemble by Simplified Graph Method
     */
    private static void assembleRead() {
    	ArrayList<Sequence> readList = null;
    	FastaReader reader = new FastaReader("F:\\Dropbox\\DNA\\20160929_SPA\\20170308\\repeat.faa");
		try {
			readList = reader.read();
			
			// simplified graph method
			Graph graph = GraphGenerator.generate(readList);
			Assembly_SimpleGraphMethod assembly = new Assembly_SimpleGraphMethod(graph);
			assembly.makeGraph();
			List<Sequence> seqList = assembly.getAssembledSequences();
			FastaWriter bw = new FastaWriter("F:\\Dropbox\\DNA\\20160929_SPA\\data\\simplegraph_test.faa");
			for(Sequence seq: seqList) {
				bw.write(seq);
			}
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
  
    /**
     * Assembled sequence analysis 
     * @throws IOException 
     */
    private static void Analyze() throws IOException {
    	// assembled sequence
    	String afileName = "F:\\Dropbox\\DNA\\20160929_SPA\\20170316\\NC_018140_ffn_single.bwa.read1.fgs.asb.faa";
    	FastaReader afr = new FastaReader(afileName);
    	List<Sequence> assembleList = afr.read();
    	HashMap<String, Sequence> assembleMap = new HashMap<String, Sequence>();
    	for(Sequence seq: assembleList)
    		assembleMap.put(seq.getHeader(), seq);
    	
    	// reference sequence
    	String rfileName = "F:\\Dropbox\\DNA\\20160929_SPA\\20170316\\NC_018140.faa";
    	FastaReader rfr = new FastaReader(rfileName);
    	List<Sequence> referenceList = rfr.read();
    	HashMap<String, Sequence> referenceMap = new HashMap<String, Sequence>();
    	for(Sequence seq: referenceList)
    		referenceMap.put(seq.getHeader(), seq);
    	
    	// blast result
    	String fileName = "F:\\Dropbox\\DNA\\20160929_SPA\\20170316\\NC_018140_blastp.out";
    	BlastReader br = new BlastReader(fileName);
    	List<BlastResult> bList = br.readTable();
    	
		// Analysis
    	Analysis analysis = new Analysis(referenceMap, assembleMap, bList);
    	analysis.analyze();
    }
    
    
    @Deprecated
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
    	BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/camdmaster/Dropbox/DNA/20160929_SPA/data/redundant.txt"));
    	List<Vertex> list = graph.getVerticeOrderedByCoverage();
    	for(Vertex vertex: list) {
    		bw.write(vertex.toString() + ", Edge=" + vertex.getEdgeList().size() + "\r\n");
    		System.out.println(vertex.toString() + ", Edge=" + vertex.getEdgeList().size());
    	}
    	bw.close();
    }
    
    private static void traverseGraph(Graph graph) throws IOException {
    	// find seed
//    	List<Vertex> seeds = graph.getSeedVertex();
    	FastaWriter bw = new FastaWriter("F:\\Dropbox\\DNA\\20160929_SPA\\data\\traverse_fgs_noindel.fasta");
    	
    	for(int i=0; i<500; i++) {
        	// traverse right
    		Vertex seed = getSeed(graph);
//    		Vertex seed = seeds.get(i);
    		if(seed == null) break;
    		seed.visited = true;
    		if(graph.getVertex(seed.getString()) == null)
    			continue;
    		
    		Stack<Vertex> stack = new Stack<Vertex>();
    		stack.push(seed);
    		int count = i+1;
//    		bw.write("seed " + count + ": " + seed.toString() + "\r\n");
    		System.out.println("seed " + count + ": " + seed.toString());
    		ArrayList<String> seqListPre = new ArrayList<String>();
    		graph.traverseV1(seed, new StringBuilder(seed.getString()), stack, seqListPre);
    		stack = new Stack<Vertex>();
    		stack.push(seed);
    		ArrayList<String> seqListPost = new ArrayList<String>();
    		graph.traverseV2(seed, new StringBuilder(seed.getString()), stack, seqListPost);
    		int countTree = 1;
    		for(String seq1: seqListPre) {
    			for(String seq2: seqListPost) {
    				String header = "seed " + count + ": " + seed.toString() + " " + countTree;
    				String string = seq1 + seq2.substring(seed.getString().length());
    				Sequence seq = new Sequence(header, string);
    				countTree++;
    				bw.write(seq);
//    				bw.write(seq1 + seq2.substring(seed.getString().length()) + "\r\n");
//    				System.out.println(seq1 + seq2.substring(seed.getString().length()));
    			}
    		}
        	graph.removeVisitedGraph();
//        	System.out.println(i + "\t" + whole);
    	}
    	bw.close();
    }
    
    private static Vertex getSeed(Graph graph) {
    	Vertex seed = null;
    	Iterator<Vertex> iter = graph.getVertexMap().values().iterator();
    	while(seed == null && iter.hasNext()) {
//    	if(iter.hasNext()) {
    		try {
    			seed = iter.next();	
    		} catch(Exception e) {
    			System.out.println("null seed");
    		}	
    	}
    	return seed;
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
