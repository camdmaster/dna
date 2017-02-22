package dna.spa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dna.spa.io.FastaWriter;

public class SequenceGenerator {

	private Graph graph;
	
	public SequenceGenerator(Graph graph) {
		this.graph = graph;
	}
	
	public void traverseGraph() throws IOException {
    	// find seed
    	List<Vertex> seeds = graph.getSeedVertex();
    	FastaWriter bw = new FastaWriter("F:\\Dropbox\\DNA\\20160929_SPA\\data\\test.faa");
    	
    	for(int i=0; i<seeds.size(); i++) {
        	// traverse right
//    		Vertex seed = getSeed();
    		Vertex seed = seeds.get(i);
//    		if(seed == null) break;
    		seed.visited = true;
    		if(graph.getVertex(seed.getString()) == null)
    			continue;
    		
//    		Stack<Vertex> stack = new Stack<Vertex>();
//    		stack.push(seed);
    		int count = i+1;
    		System.out.println("seed " + count + ": " + seed.toString());
    		ArrayList<Vertex> seqV = new ArrayList<Vertex>();
    		traverseV1(seed, seqV);
    		traverseV2(seed, seqV);

    		StringBuilder sb = new StringBuilder();
    		String header = "seed_" + seed.getString();
    		for(Vertex v: seqV) {
    			sb.append(v.getString());
    		}
    		Sequence seq = new Sequence(header, sb.toString());
    		bw.write(seq);
//    		int countTree = 1;
//    		for(String seq1: seqListPre) {
//    			for(String seq2: seqListPost) {
//    				String header = "seed " + count + ": " + seed.toString() + " " + countTree;
//    				String string = seq1 + seq2.substring(seed.getString().length());
//    				Sequence seq = new Sequence(header, string);
//    				countTree++;
//    				bw.write(seq);
//    			}
//    		}
    		
        	graph.removeVisitedGraph();
//        	System.out.println(i + "\t" + whole);
    	}
    	bw.close();
    }
	
//	private Vertex getSeed() {
//    	Vertex seed = null;
//    	Iterator<Vertex> iter = graph.vertexMap.values().iterator();
//    	while(seed == null && iter.hasNext()) {
////    	if(iter.hasNext()) {
//    		try {        		seed = iter.next();	
//    		} catch(Exception e) {
//    			System.out.println("null seed");
//    		}	
//    	}
//    	return seed;
//    }
	
	private void traverseV1(Vertex seed, ArrayList<Vertex> sequence) {
		ArrayList<Edge> edgeList = seed.getEdgeList();
		Vertex extVertex = null;
		int maxCoverage = 0;
		double maxFValue = 0;
		for(Edge edge: edgeList) {
			if(edge.getV2().equals(seed) && !edge.visited) {
				Vertex tmpVertex = edge.getV1();
				if((tmpVertex.getCoverage() > maxCoverage) ||
						((tmpVertex.getCoverage() == maxCoverage) && tmpVertex.f_value > maxFValue) ) {
					maxCoverage = tmpVertex.getCoverage();
					maxFValue = tmpVertex.f_value;
					extVertex = tmpVertex;
					
					edge.visited = true;
					extVertex.visited = true;
				} 
			}
		}
		
		if(extVertex != null) {
			sequence.add(0, extVertex);
			traverseV1(extVertex, sequence);
		} 
//		else {
//			System.out.println(string);
//		}
		
	}
	
	private void traverseV2(Vertex seed, ArrayList<Vertex> sequence) {
		ArrayList<Edge> edgeList = seed.getEdgeList();
		Vertex extVertex = null;
		int maxCoverage = 0;
		double maxFValue = 0;
		for(Edge edge: edgeList) {
			if(edge.getV1().equals(seed) && !edge.visited) {
				Vertex tmpVertex = edge.getV2();
				if((tmpVertex.getCoverage() > maxCoverage) ||
						((tmpVertex.getCoverage() == maxCoverage) && tmpVertex.f_value > maxFValue) ) {
					maxCoverage = tmpVertex.getCoverage();
					maxFValue = tmpVertex.f_value;
					extVertex = tmpVertex;
					
					edge.visited = true;
					extVertex.visited = true;
				} 
			}
		}
		
		if(extVertex != null) {
			sequence.add(extVertex);
//			string.append(extVertex.getString().substring(extVertex.getString().length()-1));
			traverseV2(extVertex, sequence);
		} 
//		else {
//			System.out.println(string);
//		}
	}
}
