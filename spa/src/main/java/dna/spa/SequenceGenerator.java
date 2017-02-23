package dna.spa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dna.spa.io.FastaWriter;

public class SequenceGenerator {

	private Graph graph;
	private List<Vertex> branchV1List;
	private List<Vertex> branchV2List;
	
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
    		System.out.print("seed " + count + ": " + seed.toString() + ", ");
    		ArrayList<Vertex> seqV = new ArrayList<Vertex>();
    		branchV1List = new ArrayList<Vertex>();
    		branchV2List = new ArrayList<Vertex>();
    		traverseV1(seed, seqV);
    		traverseV2(seed, seqV);
    		int branchCount = branchV1List.size() + branchV2List.size();
    		System.out.println("branch count: " + branchCount);
    		StringBuilder sb = new StringBuilder(seqV.get(0).getString());
    		String header = "seed=" + seed.getString() + ";branch=" + branchCount;
    		
    		if(seqV.size() + sb.length()-1 < 60) {
    			graph.removeVisitedGraph();
    			continue;
    		}
    		
    		for(int j=1; j<seqV.size(); j++) {
    			Vertex tmpV = seqV.get(j); 
    			sb.append(tmpV.getString().substring(tmpV.getString().length()-1));
    		}
    		Sequence seq = new Sequence(header, sb.toString());
    		bw.write(seq);
    		
    		// branch traverse
    		for(Vertex v: branchV1List) {
    			ArrayList<Vertex> branchV = new ArrayList<Vertex>();
    			traverseV1(v, branchV);
    			System.out.println("branch V1: " + getSequenceString(branchV));
    		}
//    		for(Vertex v: branchV2List) traverseV2(v, branchV);
    		
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
	
	private String getSequenceString(List<Vertex> vertexList) {
		StringBuilder sb = new StringBuilder(vertexList.get(0).getString());
		for(int i=1; i<vertexList.size(); i++) {
			Vertex tmpV = vertexList.get(i); 
			sb.append(tmpV.getString().substring(tmpV.getString().length()-1));
		}
		return sb.toString();
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
	
	private void traverseV1(Vertex vertex, ArrayList<Vertex> sequence) {
		ArrayList<Edge> edgeList = vertex.getEdgeList();
		Vertex extVertex = null;
		Edge extEdge = null;
		int branchCount = 0;
		int maxCoverage = 0;
		double maxFValue = 0;
		for(Edge edge: edgeList) {
			if(edge.getV2().equals(vertex) && !edge.visited) {
				Vertex tmpVertex = edge.getV1();
				branchCount++;
				if((tmpVertex.getCoverage() > maxCoverage) ||
						((tmpVertex.getCoverage() == maxCoverage) && tmpVertex.f_value > maxFValue) ) {
					maxCoverage = tmpVertex.getCoverage();
					maxFValue = tmpVertex.f_value;
					extVertex = tmpVertex;
					extEdge = edge;
				} 
			}
		}
		
		if(branchCount > 1)
			branchV1List.add(vertex);
		
		if(extVertex != null) {
			extEdge.visited = true;
			extVertex.visited = true;
			sequence.add(0, extVertex);
			traverseV1(extVertex, sequence);
		} 
		
	}
	
	private void traverseV2(Vertex vertex, ArrayList<Vertex> sequence) {
		ArrayList<Edge> edgeList = vertex.getEdgeList();
		Vertex extVertex = null;
		Edge extEdge = null;
		int branchCount = 0;
		int maxCoverage = 0;
		double maxFValue = 0;
		for(Edge edge: edgeList) {
			if(edge.getV1().equals(vertex) && !edge.visited) {
				Vertex tmpVertex = edge.getV2();
				branchCount++;
				if((tmpVertex.getCoverage() > maxCoverage) ||
						((tmpVertex.getCoverage() == maxCoverage) && tmpVertex.f_value > maxFValue) ) {
					maxCoverage = tmpVertex.getCoverage();
					maxFValue = tmpVertex.f_value;
					extVertex = tmpVertex;
					extEdge = edge;
				} 
			}
		}
		
		if(branchCount > 1)
			branchV2List.add(vertex);
		
		if(extVertex != null) {
			extEdge.visited = true;
			extVertex.visited = true;
			sequence.add(extVertex);
			traverseV2(extVertex, sequence);
		} 

	}
}
