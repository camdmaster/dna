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
import dna.spa.Sequence;

public class Assembly_SimpleGraphMethod {
	private Graph seqGraph;
	private SimplifiedGraph simplifiedGraph;
	private List<Sequence> assembledSequences;
	private int assembledNum;

	public Assembly_SimpleGraphMethod(Graph sequenceGraph) {
		this.seqGraph = sequenceGraph;
		simplifiedGraph = new SimplifiedGraph();
		assembledSequences = new ArrayList<Sequence>();
		assembledNum = 0;
	}
	
	public List<Sequence> getAssembledSequences() {
		return assembledSequences;
	}

	public void makeGraph() {
		long startTime = System.nanoTime();
		System.out.println("<Simplified Graph Method>");
		
		System.out.println("Proc 1: Find simplified vertice");
		findSimplifedVertex();
		long p1Time = System.nanoTime();
		System.out.println("P1 TIME : " + (p1Time - startTime) /1000000.0 + " (ms)");
		
		System.out.println("Proc 2: Find simplified edges");
		findSimplifiedEdge();
		long p2Time = System.nanoTime();
		System.out.println("P2 TIME : " + (p2Time - p1Time) /1000000.0 + " (ms)");
		
		System.out.println("Proc 3: Organize Simplified Graph");
		organizeGraph();
		long p3Time = System.nanoTime();
		System.out.println("P3 TIME : " + (p3Time - p2Time) /1000000.0 + " (ms)");
		
		System.out.println("Proc 4: Assemble sequence by DFS");
		dfs();
		long p4Time = System.nanoTime();
		System.out.println("P4 TIME : " + (p4Time - p3Time) /1000000.0 + " (ms)");
	}
	
	/**
	 * Proc 1.
	 */
	private void findSimplifedVertex() {
		Iterator<Vertex> iter = seqGraph.getVertexMap().values().iterator();
    	while(iter.hasNext()) {
    			Vertex vx = iter.next();
    			int inEdgeSize = 0;
    			int outEdgeSize = 0;
    			for(Edge edge: vx.getEdgeList()) {
    				if(edge.getV2().equals(vx))
    					inEdgeSize++;
    				else
    					outEdgeSize++;
    			}
    			if(inEdgeSize > 1 || outEdgeSize > 1) {
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
//    	int count=0;
    	for(int i=0; i<seeds.size(); i++) {
//    		long st = System.nanoTime();
        	// traverse right
    		Vertex seed = seeds.get(i);
    		
    		if(seed.visited)
    			continue;
//    		if(seqGraph.getVertex(seed.getString()) == null)
//    			continue;
    		
    		seed.visited = true;

    		ArrayList<Vertex> seqV = new ArrayList<Vertex>();
    		seqV.add(seed);
    		sGenerator.traverseV1(seed, seqV);
    		sGenerator.traverseV2(seed, seqV);
    		SimplifiedEdge sEdge = new SimplifiedEdge(seqV);
    		simplifiedGraph.addSEdge(sEdge);
    		
//    		seqGraph.removeVisitedGraph();
//    		long et = System.nanoTime();
//    		count++;
//    		System.out.println(count + " TIME : " + (et - st) /1000000.0 + " (ms)");
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
			// make sequence if size > 60
			if(firstKey == null && lastKey == null && seq.size() >= 60) {
				assembledNum++;
				String header = "sequence_nobrach_" + assembledNum;
				String seqString = getSequenceString(seq);
				Sequence sequence = new Sequence(header, seqString);
				this.assembledSequences.add(sequence);
				tempSEdge.add(sEdge);
			}
		}
		
		for(SimplifiedEdge e: tempSEdge)
			simplifiedGraph.removeEdge(e);
		
		return;
	}
	
	/**
	 * Proc 4.
	 */
	private void dfs() {
		List<SimplifiedEdge> seeds = simplifiedGraph.getEdgeList();
		for(int i=0; i<seeds.size(); i++) {
			SimplifiedEdge seed = seeds.get(i);
			if(seed == null)
				continue;
			
			Stack<SimplifiedEdge> stack = new Stack<SimplifiedEdge>();
			stack.push(seed);
			List<String> seqStringListV1 = new ArrayList<String>();
			traverseV1(seed, stack, seqStringListV1);
			stack = new Stack<SimplifiedEdge>();
			stack.push(seed);
			List<String> seqStringListV2 = new ArrayList<String>();
			traverseV2(seed, stack, seqStringListV2);
			
			for(String str1: seqStringListV1) {
				for(String str2: seqStringListV2) {
					assembledNum++;
					String header = "sequence_branch_" + assembledNum;
					String seqString = str1 + str2;
					Sequence seq = new Sequence(header, seqString);
					this.assembledSequences.add(seq);
				}
			}
			
			simplifiedGraph.removeVisited();
		}
	}
	
	private void traverseV1(SimplifiedEdge seedEdge, Stack<SimplifiedEdge> stack, List<String> seqStringList) {
		SimplifiedVertex vertex = seedEdge.getV1();
		seedEdge.visited = true;
		List<SimplifiedEdge> edgeList = new ArrayList<SimplifiedEdge>();
		if(vertex != null) {
			vertex.visited = true;
			edgeList = vertex.getEdgeList();
		}
		for(SimplifiedEdge edge: edgeList) {
			if(!(edge.equals(seedEdge) || stack.contains(edge))) {
				SimplifiedVertex edgeV2 = edge.getV2();
				SimplifiedVertex seedV1 = seedEdge.getV1();
				if(seedV1 != null && edgeV2 != null && edgeV2 == seedV1) {
					stack.push(edge);
					traverseV1(edge, stack, seqStringList);
				}
			}
		}
		
		if(vertex == null) {
			StringBuilder sb = new StringBuilder();
			Vertex firstVx = stack.peek().getSequence().get(0); 
			sb.append(firstVx.getString().substring(0, firstVx.getString().length()-1));
			String test = "";
			for(int i=stack.size()-1; i>=0; i--) {
				SimplifiedEdge e = stack.get(i);
				test += e.toString() + " + ";
				for(Vertex v: e.getSequence())
					sb.append(v.getString().substring(v.getString().length()-1));
				if(i!=0) {
					Vertex svx = e.getV2().getVertex();
					sb.append(svx.getString().substring(svx.getString().length()-1));
				}
			}
//			if(stack.size() > 1)
//				System.out.println("V1: " + sb.toString());
			seqStringList.add(sb.toString());
		}
		
		stack.pop();
	}
	
	private void traverseV2(SimplifiedEdge seedEdge, Stack<SimplifiedEdge> stack, List<String> seqStringList) {
		SimplifiedVertex vertex = seedEdge.getV2();
		seedEdge.visited = true;
		List<SimplifiedEdge> edgeList = new ArrayList<SimplifiedEdge>();
		if(vertex != null) {
			vertex.visited = true;
			edgeList = vertex.getEdgeList();
		}
		for(SimplifiedEdge edge: edgeList) {
			if(!(edge.equals(seedEdge) || stack.contains(edge))) {
				SimplifiedVertex edgeV1 = edge.getV1();
				SimplifiedVertex seedV2 = seedEdge.getV2();
				if(seedV2 != null && edgeV1 != null && edgeV1 == seedV2) {
					stack.push(edge);
					traverseV2(edge, stack, seqStringList);
				}
			}
		}
		
		if(vertex == null) {
			StringBuilder sb = new StringBuilder();
			SimplifiedVertex first = stack.get(0).getV2();
			if(first != null) {
				Vertex firstVx = stack.get(0).getV2().getVertex(); 
				sb.append(firstVx.getString().substring(firstVx.getString().length()-1));
				String test = stack.get(0).toString() + " + ";
				for(int i=1; i<stack.size(); i++) {
					SimplifiedEdge e = stack.get(i);
					test += e.toString() + " + ";
					for(Vertex v: e.getSequence())
						sb.append(v.getString().substring(v.getString().length()-1));
					if(i!=stack.size()-1) {
						Vertex svx = e.getV2().getVertex();
						sb.append(svx.getString().substring(svx.getString().length()-1));
					}
				}
//				if(stack.size() > 1)
//					System.out.println("V2: " + sb.toString());
				seqStringList.add(sb.toString());
			}
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
