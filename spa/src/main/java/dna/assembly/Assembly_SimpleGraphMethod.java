package dna.assembly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.SimplifiedEdge;
import dna.graph.SimplifiedGraph;
import dna.graph.SimplifiedVertex;
import dna.graph.Vertex;

public class Assembly_SimpleGraphMethod {
	private Graph seqGraph;
	private SimplifiedGraph simplifiedGraph;

	public Assembly_SimpleGraphMethod(Graph sequenceGraph) {
		this.seqGraph = sequenceGraph;
		simplifiedGraph = new SimplifiedGraph();
	}
	
	public void makeGraph() {
		findSimplifedVertex();
		findSimplifiedEdge();
		organizeGraph();
		dfs();
	}
	
	/**
	 * Proc 1.
	 */
	private void findSimplifedVertex() {
		Iterator<Vertex> iter = seqGraph.getVertexMap().values().iterator();
    	while(iter.hasNext()) {
    			Vertex vx = iter.next();
    			int edgeSize = vx.getEdgeList().size();
    			if(edgeSize > 2) {
    				vx.visited = true;
    				SimplifiedVertex svx = new SimplifiedVertex(vx);
    				simplifiedGraph.addSVertex(svx);
    				
    				for(Edge e: vx.getEdgeList()) {
    					e.getV1().simplifiedVertexKey = vx;
    					e.getV2().simplifiedVertexKey = vx;
    				}
    			}
    	}
    	
    	for(SimplifiedVertex svx: simplifiedGraph.getSVertexList())
    		seqGraph.removeVertex(svx.getVertex());
	}
	
	/**
	 * Proc 2.
	 */
	private void findSimplifiedEdge() {
		
		SequenceGenerator sGenerator = new SequenceGenerator(seqGraph);
    	List<Vertex> seeds = seqGraph.getSeedVertex();
    	
    	for(int i=0; i<seeds.size(); i++) {
//    	for(int i=0; i<1000; i++) {
        	// traverse right
    		Vertex seed = seeds.get(i);
    		seed.visited = true;
    		if(seqGraph.getVertex(seed.getString()) == null)
    			continue;

    		ArrayList<Vertex> seqV = new ArrayList<Vertex>();
    		sGenerator.traverseV1(seed, seqV);
    		sGenerator.traverseV2(seed, seqV);
    		SimplifiedEdge sEdge = new SimplifiedEdge(seqV);
    		simplifiedGraph.addSEdge(sEdge);
    		
    		seqGraph.removeVisitedGraph();
    		
//    		System.out.println(getSequenceString(seqV));
    	}
    	return;
    }
	
	/**
	 * Proc 3.
	 */
	private void organizeGraph() {
		List<SimplifiedEdge> tempSEdge = new ArrayList<SimplifiedEdge>();
		for(SimplifiedEdge sEdge: simplifiedGraph.getEdgeList()) {
			List<Vertex> seq = sEdge.getSequence();
			Vertex first = seq.get(0);
			Vertex last = seq.get(seq.size()-1);
			Vertex firstKey = first.simplifiedVertexKey;
			Vertex lastKey = last.simplifiedVertexKey;
			if(firstKey != null) {
				simplifiedGraph.getSVertex(firstKey).addSEdge(sEdge);
				sEdge.setV1(simplifiedGraph.getSVertex(firstKey));
			}
			if(lastKey != null) {
				simplifiedGraph.getSVertex(lastKey).addSEdge(sEdge);
				sEdge.setV2(simplifiedGraph.getSVertex(lastKey));
			}
			if(firstKey == null && lastKey == null) {
				System.out.println(getSequenceString(seq));
//				simplifiedGraph.removeSEdge(sEdge);
				tempSEdge.add(sEdge);
			}
		}
		
		for(SimplifiedEdge e: tempSEdge)
			simplifiedGraph.removeSEdge(e);
		
		return;
	}
	
	/**
	 * Proc 4.
	 */
	private void dfs() {
		for(SimplifiedEdge seed: simplifiedGraph.getEdgeList()) {
			Stack<SimplifiedEdge> stack = new Stack<SimplifiedEdge>();
			stack.push(seed);
//			ArrayList<String> seqListPre = new ArrayList<String>();
			traverseV1(seed, stack);
			
		}
	}
	
	private void traverseV1(SimplifiedEdge seedEdge, Stack<SimplifiedEdge> stack) {
		SimplifiedVertex vertex = seedEdge.getV1();
		seedEdge.visited = true;
		List<SimplifiedEdge> edgeList = new ArrayList<SimplifiedEdge>();
		if(vertex != null) {
			vertex.visited = true;
			edgeList = vertex.getEdgeList();
		}
		for(SimplifiedEdge edge: edgeList) {
			if(!seedEdge.equals(edge)) {
				if(stack.contains(edge))
					continue;
				stack.push(edge);
				traverseV1(edge, stack);
			}
		}
		
		if(vertex == null) {
			StringBuilder sb = new StringBuilder();
			Vertex firstVx = stack.peek().getSequence().get(0); 
			sb.append(firstVx.getString().substring(0, firstVx.getString().length()-1));
			for(int i=stack.size()-1; i>=0; i--) {
				SimplifiedEdge e = stack.get(i);
				for(Vertex v: e.getSequence())
					sb.append(v.getString().substring(v.getString().length()-1));
				if(i!=0) {
					Vertex svx = e.getV2().getVertex();
					sb.append(svx.getString().substring(svx.getString().length()-1));
				}
			}
			System.out.println("V1: " + sb.toString());
		}
		
		stack.pop();
	}
	
	private void traverseV2(Vertex vertex, StringBuilder string, Stack<Vertex> stack, ArrayList<String> seqList) {
		ArrayList<Edge> edgeList = vertex.getEdgeList();
		Vertex tmpVertex = null;
		for(Edge edge: edgeList) {
			if(edge.getV1().equals(vertex)) {
				tmpVertex = edge.getV2();
				if(stack.contains(tmpVertex))
					continue;
				edge.visited = true;
				tmpVertex.visited = true;
				string.append(tmpVertex.getString().substring(tmpVertex.getString().length()-1));
				stack.push(tmpVertex);
				traverseV2(tmpVertex, string, stack, seqList);
			}
		}
		
		if(tmpVertex == null) {
			StringBuilder sb = new StringBuilder(stack.get(0).getString());
			for(int i=1; i<stack.size(); i++) {
				Vertex v = stack.get(i);
				sb.append(v.getString().substring(v.getString().length()-1));
			}
			seqList.add(sb.toString());
		}
		stack.pop();
	}
	
	private String getSequenceString(List<Vertex> vxList) {
		StringBuilder sb = new StringBuilder(vxList.get(0).getString());
		for(int j=1; j<vxList.size(); j++) {
			Vertex tmpV = vxList.get(j); 
			sb.append(tmpV.getString().substring(tmpV.getString().length()-1));
		}
		return sb.toString();
	}

}
