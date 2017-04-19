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
import dna.spa.Preference;
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
    	
    	System.out.println("Number of Simplified Vertices: " + simplifiedGraph.getSVertexList().size());
	}
	
	/**
	 * Proc 2.
	 */
	private void findSimplifiedEdge() {
		
    	List<Vertex> seeds = seqGraph.getSeedVertex();
    	for(int i=0; i<seeds.size(); i++) {
    		Vertex seed = seeds.get(i);
    		if(seed.visited)
    			continue;
    		seed.visited = true;

    		ArrayList<Vertex> seqV = new ArrayList<Vertex>();
    		seqV.add(seed);
    		traverseSimpleEdgeV1(seed, seqV);
    		traverseSimpleEdgeV2(seed, seqV);
    		SimplifiedEdge sEdge = new SimplifiedEdge(seqV);
    		simplifiedGraph.addSEdge(sEdge);
    	}
    	
    	System.out.println("Number of Simplified Edges: " + simplifiedGraph.getSEdgeList().size());
    }
	
	/**
	 * Proc 2-1.
	 * @param vertex
	 * @param sequence
	 */
	private void traverseSimpleEdgeV1(Vertex vertex, ArrayList<Vertex> sequence) {
		while(vertex != null) {
			ArrayList<Edge> edgeList = vertex.getEdgeList();
			Vertex extVertex = null;
			for(Edge edge: edgeList) {
				if(edge.getV2().equals(vertex) && !edge.visited) {
					extVertex = edge.getV1();
					extVertex.visited = true;
					sequence.add(0, extVertex);
					break;
				}
			}
			vertex = extVertex;
		}
	}
	
	/**
	 * Proc 2-2.
	 * @param vertex
	 * @param sequence
	 */
	private void traverseSimpleEdgeV2(Vertex vertex, ArrayList<Vertex> sequence) {
		while(vertex != null) {
			ArrayList<Edge> edgeList = vertex.getEdgeList();
			Vertex extVertex = null;
			for(Edge edge: edgeList) {
				if(edge.getV1().equals(vertex) && !edge.visited) {
					extVertex = edge.getV2();
					extVertex.visited = true;
					sequence.add(extVertex);
					break;
				}
			}
			vertex = extVertex;
		}
	}
	
	/**
	 * Proc 3.
	 */
	private void organizeGraph() {
		List<SimplifiedEdge> tempSEdge = new ArrayList<SimplifiedEdge>();
		for(SimplifiedEdge sEdge: simplifiedGraph.getSEdgeList()) {
//			if(sEdge.getSequence().size() <= 5)
//				System.out.println("Vertex Size in Simplified Edge: " + sEdge.getSequence().size());
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

			if(firstKey == null && lastKey == null) {
				String seqString = getSequenceString(seq);
				if(seqString.length() >= Preference.CUTOFF_SEQUENCE_SIZE) {
					assembledNum++;
					String header = "sequence_nobranch_" + assembledNum;
					Sequence sequence = new Sequence(header, seqString);
					this.assembledSequences.add(sequence);	
				}
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
		List<SimplifiedEdge> seeds = simplifiedGraph.getSEdgeList();
		for(int i=0; i<seeds.size(); i++) {
			SimplifiedEdge seed = seeds.get(i);
			if(seed.visited_seed)
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
					String seqString = str1 + str2;

					if(seqString.length() >= Preference.CUTOFF_SEQUENCE_SIZE) {
						assembledNum++;
						String header = "sequence_branch_" + assembledNum;
						Sequence seq = new Sequence(header, seqString);
						this.assembledSequences.add(seq);	
					}
				}
			}
			
			simplifiedGraph.resetVisited_Traversal(false);
		}
	}
	
	private void traverseV1(SimplifiedEdge seedEdge, Stack<SimplifiedEdge> stack, List<String> seqStringList) {
		SimplifiedVertex vertex = seedEdge.getV1();
		seedEdge.visited_traversal = true;
		seedEdge.visited_seed = true;
		List<SimplifiedEdge> edgeList = new ArrayList<SimplifiedEdge>();
		if(vertex != null && !vertex.visited_traversal) {
			vertex.visited_traversal = true;
			vertex.visited_seed = true;
			edgeList = vertex.getEdgeList();
		}
		judgeBranch(edgeList);
		int traverseCount = 0;
		for(SimplifiedEdge edge: edgeList) {
			if(edge.visited_traversal)
				continue;
			if(!(edge.equals(seedEdge) || stack.contains(edge))) {
				traverseCount++;
				SimplifiedVertex edgeV2 = edge.getV2();
				SimplifiedVertex seedV1 = seedEdge.getV1();
				if(seedV1 != null && edgeV2 != null && edgeV2 == seedV1) {
					traverseCount++;
					
					// check coverage & read distribution
					List<Integer> readListSeed = seedEdge.getSequence().get(0).getReadIndexList();
					List<Integer> readListTarget = edge.getSequence().get(edge.getSequence().size()-1).getReadIndexList();
					int coverageGap = Math.abs(readListSeed.size() - readListTarget.size());
					int matchCount = 0;
					for(int read: readListSeed) {
						if(readListTarget.contains(read))
							matchCount++;
					}
					System.out.println("Coverage Gap: " + coverageGap + "\tMatching (Seed, Target): " + matchCount +
							"/" + readListSeed.size() + ", " + matchCount + "/" + readListTarget.size());
					
					stack.push(edge);
					traverseV1(edge, stack, seqStringList);
				}
			}
		}
		
		if(vertex == null || traverseCount == 0) {
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
			seqStringList.add(sb.toString());
		}
		
		stack.pop();
	}
	
	private void judgeBranch(List<SimplifiedEdge> edgeList) {
		List<SimplifiedEdge> tempList = new ArrayList<SimplifiedEdge>();
		for(SimplifiedEdge edge: edgeList) {
			if(!edge.visited_traversal && !(isShort(edge) && isTerminal(edge))) {
				if(!tempList.contains(edge))
					tempList.add(edge);
			} else
				edge.visited_seed = true;
		}
		
		int length = tempList.size();
		for(int i=0; i<length-1; i++) {
			SimplifiedEdge edge1 = edgeList.get(i);
			if(edge1.visited_traversal)
				continue;
			for(int j=i+1; j<length; j++) {
				SimplifiedEdge edge2 = edgeList.get(j);
				if(edge2.visited_traversal)
					continue;
				if(isBubble(edge1, edge2)) {
					int coverage1 = 0;
					int coverage2 = 0;
					for(Vertex v: edge1.getSequence())
						coverage1 += v.getCoverage();
					for(Vertex v: edge2.getSequence())
						coverage2 += v.getCoverage();
					if(coverage1 < coverage2)
						edge1.visited_traversal = true;
					else
						edge2.visited_traversal = true;
				}
			}
		}
	}
	
	private boolean isShort(SimplifiedEdge edge) {
		return edge.getSequence().size() <= Preference.CUTOFF_TERMINAL_VERTEX_SIZE;
	}
	
	private boolean isTerminal(SimplifiedEdge edge) {
		return edge.getV1() == null || edge.getV2() == null;
	}
	
	private boolean isBubble(SimplifiedEdge edge1, SimplifiedEdge edge2) {
		return edge1.getV1() == edge2.getV1() && edge1.getV2() == edge2.getV2();
	}
	
	private void traverseV2(SimplifiedEdge seedEdge, Stack<SimplifiedEdge> stack, List<String> seqStringList) {
		SimplifiedVertex vertex = seedEdge.getV2();
		seedEdge.visited_traversal = true;
		seedEdge.visited_seed = true;
		List<SimplifiedEdge> edgeList = new ArrayList<SimplifiedEdge>();
		if(vertex != null && !vertex.visited_traversal) {
			vertex.visited_traversal = true;
			vertex.visited_seed = true;
			edgeList = vertex.getEdgeList();
		}
		judgeBranch(edgeList);
		int traverseCount = 0;
		for(SimplifiedEdge edge: edgeList) {
			if(edge.visited_traversal)
				continue;
			if(!(edge.equals(seedEdge) || stack.contains(edge))) {
				traverseCount++;
				SimplifiedVertex edgeV1 = edge.getV1();
				SimplifiedVertex seedV2 = seedEdge.getV2();
				if(seedV2 != null && edgeV1 != null && edgeV1 == seedV2) {
					traverseCount++;
					
					// check coverage & read distribution
					List<Integer> readListSeed = seedEdge.getSequence().get(0).getReadIndexList();
					List<Integer> readListTarget = edge.getSequence().get(edge.getSequence().size()-1).getReadIndexList();
					int coverageGap = Math.abs(readListSeed.size() - readListTarget.size());
					int matchCount = 0;
					for(int read: readListSeed) {
						if(readListTarget.contains(read))
							matchCount++;
					}
					System.out.println("Coverage Gap: " + coverageGap + "\tMatching (Seed, Target): " + matchCount +
							"/" + readListSeed.size() + ", " + matchCount + "/" + readListTarget.size());
					
					stack.push(edge);
					traverseV2(edge, stack, seqStringList);
				}
			}
		}
		
		if(vertex == null || traverseCount == 0) {
			StringBuilder sb = new StringBuilder();
			SimplifiedVertex first = stack.get(0).getV2();
			if(first != null) {
				Vertex firstVx = stack.get(0).getV2().getVertex(); 
				sb.append(firstVx.getString().substring(firstVx.getString().length()-1));
				for(int i=1; i<stack.size(); i++) {
					SimplifiedEdge e = stack.get(i);
					for(Vertex v: e.getSequence())
						sb.append(v.getString().substring(v.getString().length()-1));
					if(i!=stack.size()-1) {
						Vertex svx = e.getV2().getVertex();
						sb.append(svx.getString().substring(svx.getString().length()-1));
					}
				}
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
	
	
	// TODO calculate similarity of Simplified Edges
	private double calculateSequenceSimilarity(SimplifiedEdge edge1, SimplifiedEdge edge2) {
		return 0.0;
	}
}
