package dna.assembly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import dna.alignment.NeedlemanWunsch;
import dna.analysis.BlastResult;
import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.SimplifiedEdge;
import dna.graph.SimplifiedGraph;
import dna.graph.SimplifiedVertex;
import dna.graph.Vertex;
import dna.spa.Preference;
import dna.spa.Sequence;
import dna.spa.io.BlastReader;
import dna.util.Blast;

public class Assembly_SimpleGraphMethod {
	private Graph seqGraph;
	private SimplifiedGraph simplifiedGraph;
	private List<Sequence> assembledSequences;
	private List<Sequence> clusteredSequences;
	private int assembledNum;
	
	private int countBubbleOver99;
	private int countBubble99to95;
	private int countBubbleUnder95;

	public Assembly_SimpleGraphMethod(Graph sequenceGraph) {
		this.seqGraph = sequenceGraph;
		simplifiedGraph = new SimplifiedGraph();
		assembledSequences = new ArrayList<Sequence>();
		clusteredSequences = new ArrayList<Sequence>();
		assembledNum = 0;
	}
	
	public List<Sequence> getAssembledSequences() {
		return assembledSequences;
//		return clusteredSequences;
	}

	public void makeGraph() {
		long startTime = System.nanoTime();
		System.out.println("<Simplified Graph Method>");
		Preference.LOG += "<Simplified Graph Method>\r\n";
		
		System.out.println("Proc 1: Find simplified vertice");
		Preference.LOG += "Proc 1: Find simplified vertice\r\n";
		findSimplifedVertex();
		long p1Time = System.nanoTime();
		System.out.println("P1 TIME : " + (p1Time - startTime) /1000000.0 + " (ms)");
		Preference.LOG += "P1 TIME : " + (p1Time - startTime) /1000000.0 + " (ms)\r\n";
		
		System.out.println("Proc 2: Find simplified edges");
		Preference.LOG += "Proc 2: Find simplified edges\r\n";
		findSimplifiedEdge();
		long p2Time = System.nanoTime();
		System.out.println("P2 TIME : " + (p2Time - p1Time) /1000000.0 + " (ms)");
		Preference.LOG += "P2 TIME : " + (p2Time - p1Time) /1000000.0 + " (ms)\r\n";
		
		System.out.println("Proc 3: Organize Simplified Graph");
		Preference.LOG += "Proc 3: Organize Simplified Graph\r\n";
		organizeGraph();
		long p3Time = System.nanoTime();
		System.out.println("P3 TIME : " + (p3Time - p2Time) /1000000.0 + " (ms)");
		Preference.LOG += "P3 TIME : " + (p3Time - p2Time) /1000000.0 + " (ms)\r\n";
		
		System.out.println("Proc 4: Assemble sequence by DFS");
		Preference.LOG += "Proc 4: Assemble sequence by DFS\r\n";
		dfs();
//		System.out.println("bubble >=99: " + countBubbleOver99 + " , 99~95: " + countBubble99to95 + " , <95: " + countBubbleUnder95 );
		long p4Time = System.nanoTime();
		System.out.println("P4 TIME : " + (p4Time - p3Time) /1000000.0 + " (ms)");
		Preference.LOG += "P4 TIME : " + (p4Time - p3Time) /1000000.0 + " (ms)\r\n";
		
//		System.out.println("Proc 5: Clustering & Merge");
//		Preference.LOG += "Proc 5: Clustering & Merge\r\n";
//		clusteringMerge();
//		long p5Time = System.nanoTime();
//		System.out.println("P5 TIME : " + (p5Time - p4Time) /1000000.0 + " (ms)");
//		Preference.LOG += "P5 TIME : " + (p5Time - p4Time) /1000000.0 + " (ms)\r\n";
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
    					if(e.getV1() == vx)
    						e.getV2().simplifiedVertexKey = vx;
    					if(e.getV2() == vx)
    						e.getV1().simplifiedVertexKey = vx;
    				}
    			}
    	}

//    	for(SimplifiedVertex svx: simplifiedGraph.getSVertexList()) {
//    		if(svx.getVertex().simplifiedVertexKey != null)
//    			System.out.println("Continuous Branch: " + svx.getVertex().toString());
//    	}
    	
    	System.out.println("Number of Simplified Vertices: " + simplifiedGraph.getSVertexList().size());
    	Preference.LOG += "Number of Simplified Vertices: " + simplifiedGraph.getSVertexList().size() + "\r\n";
	}
	
	/**
	 * Proc 2.
	 */
	private void findSimplifiedEdge() {
		
		List<Edge> edges = seqGraph.getEdgeList();
		int size = edges.size();
		int count=0;
		for(Edge edge: edges) {
//			count++;
//			System.out.println("step: " + count + "/" + size);
			if(edge.visited)
				continue;
			edge.visited = true;
			
			List<Edge> condensedEdge = new ArrayList<Edge>();
			condensedEdge.add(edge);
			traverseCondensedEdge1(edge, condensedEdge);
			traverseCondensedEdge2(edge, condensedEdge);
			SimplifiedEdge se = new SimplifiedEdge(condensedEdge);
			simplifiedGraph.addSEdge(se);
		}
		
    	System.out.println("Number of Simplified Edges: " + simplifiedGraph.getSEdgeList().size());
    	Preference.LOG += "Number of Simplified Edges: " + simplifiedGraph.getSEdgeList().size() + "\r\n";
    }
	
	/**
	 * Proc 2-1.
	 * @param edge
	 * @param condensedEdge
	 */
	private void traverseCondensedEdge1(Edge edge, List<Edge> condensedEdge) {
		Vertex v1 = edge.getV1();
		if(simplifiedGraph.getSVertex(v1) != null)
			return;
		Edge extEdge = null;
		List<Edge> edgeList = v1.getEdgeList();
		for(Edge e: edgeList) {
			if(e.getV2().equals(v1)) {
				extEdge = e;
				if(extEdge.visited)
					return;
				extEdge.visited = true;
				condensedEdge.add(0, extEdge);
				traverseCondensedEdge1(extEdge, condensedEdge);
			}
		}
	}
	
	/**
	 * Proc 2-2.
	 * @param edge
	 * @param condensedEdge
	 */
	private void traverseCondensedEdge2(Edge edge, List<Edge> condensedEdge) {
		Vertex v2 = edge.getV2();
		if(simplifiedGraph.getSVertex(v2) != null)
			return;
		Edge extEdge = null;
		List<Edge> edgeList = v2.getEdgeList();
		for(Edge e: edgeList) {
			if(e.getV1().equals(v2)) {
				extEdge = e;
				if(extEdge.visited)
					return;
				extEdge.visited = true;
				condensedEdge.add(extEdge);
				traverseCondensedEdge2(extEdge, condensedEdge);
			}
		}
	}
	
	/**
	 * Proc 3.
	 */
	private void organizeGraph() {
//		List<SimplifiedEdge> tempSEdge = new ArrayList<SimplifiedEdge>();
		for(SimplifiedEdge sEdge: simplifiedGraph.getSEdgeList()) {
			List<Edge> edgeList = sEdge.getEdgeList();
			Vertex branchV1 = edgeList.get(0).getV1();
			Vertex branchV2 = edgeList.get(edgeList.size()-1).getV2();			
			SimplifiedVertex svx1 = simplifiedGraph.getSVertex(branchV1);
			SimplifiedVertex svx2 = simplifiedGraph.getSVertex(branchV2);
			
			if(svx1 == null && svx2 == null) {
//				String seqString = getSequenceString(edgeList);
//				if(seqString.length() >= Preference.CUTOFF_SEQUENCE_SIZE) {
//					assembledNum++;
//					String header = "nobranch_" + assembledNum;
//					Sequence sequence = new Sequence(header, seqString);
//					this.assembledSequences.add(sequence);	
//				}
//				tempSEdge.add(sEdge);
			} else {
				if(svx1 != null) {
					svx1.addSEdge(sEdge);
					sEdge.setV1(svx1);
				}
				if(svx2 != null) {
					svx2.addSEdge(sEdge);
					sEdge.setV2(svx2);
				}
			}
		}
		
//		for(SimplifiedEdge e: tempSEdge)
//			simplifiedGraph.removeEdge(e);
		
		for(SimplifiedVertex sv: simplifiedGraph.getSVertexList()) {
			if(sv.getEdgeList().size() == 0)
				System.out.println("false negative SV: " + sv.getVertex().getString());
		}
		
		divideAndReconnectTerminal();
		
		return;
	}
	
	private void findRepeatRegion() {
		int repeatCount = 0;
		List<SimplifiedEdge> sEdges = simplifiedGraph.getSEdgeList();
		for(int i=0; i<sEdges.size(); i++) {
			SimplifiedEdge sEdge = sEdges.get(i);
			if(sEdge.getV1() == sEdge.getV2())
				repeatCount++;
		}
		System.out.println("repeat region count: " + repeatCount);
	}
	
	/**
	 *  Proc 3-1. divide terminal simplified vertex to small k-mer
	 */
	private void divideAndReconnectTerminal() {
		List<SimplifiedEdge> sEdgeList = simplifiedGraph.getSEdgeList();
		List<SimplifiedEdge> leftTerminalList = new ArrayList<SimplifiedEdge>();
		List<SimplifiedEdge> rightTerminalList = new ArrayList<SimplifiedEdge>();
		List<List<Edge>> leftTerminalSmallEdgesList = new ArrayList<List<Edge>>();
		List<List<Edge>> rightTerminalSmallEdgesList = new ArrayList<List<Edge>>();
		HashMap<String, Integer> leftEdgeMap = new HashMap<String, Integer>();
		
		// divide terminal
		for(int i=0; i<sEdgeList.size(); i++) {
			SimplifiedEdge se = sEdgeList.get(i);
			if(se.getV1() == null) {
				List<Edge> edgeList = se.getEdgeList();
				List<Edge> smallEdgeList = divideToSmallEdges(edgeList.get(0), leftTerminalList.size(), leftEdgeMap);
				leftTerminalList.add(se);
				leftTerminalSmallEdgesList.add(smallEdgeList);
			}
			if(se.getV2() == null) {
				List<Edge> edgeList = se.getEdgeList();
				int lastIndex = edgeList.size() - 1;
				List<Edge> smallEdgeList = divideToSmallEdges(edgeList.get(lastIndex), i, null);
				rightTerminalList.add(se);
				rightTerminalSmallEdgesList.add(smallEdgeList);
			}
		}
		
		// reconnect
		HashSet<Integer> reconnectedIndex = new HashSet<Integer>();
		for(int i=0; i<rightTerminalList.size(); i++) {
			for(Edge e: rightTerminalSmallEdgesList.get(i)) {
				String key = e.getString();
				if(leftEdgeMap.containsKey(key)) {
					int num = leftEdgeMap.get(key);
					if(!leftTerminalList.get(num).equals(rightTerminalList.get(i)) && !reconnectedIndex.contains(num)) {
						if(reconnect(leftTerminalList.get(num), rightTerminalList.get(i),
								leftTerminalSmallEdgesList.get(num), rightTerminalSmallEdgesList.get(i))) {
							reconnectedIndex.add(num);
							break;
						}
					}
				}
			}
		}
//		for(int i=0; i<leftTerminalList.size(); i++) {
//			for(int j=0; j<rightTerminalList.size(); j++) {
//				if(leftTerminalList.get(i) != rightTerminalList.get(j)) {
//					reconnect(leftTerminalList.get(i), rightTerminalList.get(j),
//							leftTerminalSmallEdgesList.get(i), rightTerminalSmallEdgesList.get(j));
//				}
//			}
//		}
	}
	
	/**
	 * Proc 3-2. divide simplified vertex
	 * @param sVertex
	 */
	private List<Edge> divideToSmallEdges(Edge edge, int number, HashMap<String, Integer> edgeMap) {
		List<Edge> edgeList = new ArrayList<Edge>();
		int kmerLength = 12;
		int edgeLength = kmerLength + 1;
		
		String seq = edge.getString();
		int length = seq.length();
		for(int i=0; i<length-kmerLength; i++) {
			String edgeSeq = seq.substring(i, i+edgeLength);
			Edge tmpEdge = new Edge(null, null, edgeSeq);
			edgeList.add(tmpEdge);
			if(edgeMap != null)
				edgeMap.put(tmpEdge.getString(), number);
		}
		
		return edgeList;
		
//		Sequence seq = new Sequence(edge.getString());
//		List<Sequence> seqList = new ArrayList<Sequence>();
//		seqList.add(seq);
//		Graph graph = GraphGenerator.generate(seqList, kmerLength);
//		return graph.getEdgeList();
	}
	
	private boolean reconnect(SimplifiedEdge leftTerminal, SimplifiedEdge rightTerminal, List<Edge> leftSmallEdges, List<Edge> rightSmallEdges) {
		List<Edge> bridgeEdgeList = new ArrayList<Edge>();
		int kmerLength = 12;
		int edgeLength = kmerLength + 1;
		Edge rightSmallEnd = rightSmallEdges.get(rightSmallEdges.size()-1);
		for(int i=0; i<leftSmallEdges.size(); i++) {
			Edge leftSmall = leftSmallEdges.get(i);
			if(leftSmall.getString().equals(rightSmallEnd.getString())) {
				String leftTerminalString = leftTerminal.getEdgeList().get(0).getString();
				leftTerminalString = leftTerminalString.substring(i+edgeLength, leftTerminalString.length()-1);
				String rightTerminalString = rightTerminal.getEdgeList().get(rightTerminal.getEdgeList().size()-1).getString().substring(1);
				String bridgeString = rightTerminalString + leftTerminalString;
				for(int j=0; j<bridgeString.length()-Preference.VERTEX_SIZE; j++) {
					String newEdgeString = bridgeString.substring(j, j+Preference.VERTEX_SIZE+1);
					Edge tmpEdge = new Edge(null, null, newEdgeString);
					bridgeEdgeList.add(tmpEdge);
				}
				
				List<Edge> completeEdgeList = rightTerminal.getEdgeList();
				completeEdgeList.addAll(bridgeEdgeList);
				completeEdgeList.addAll(leftTerminal.getEdgeList());
				
				SimplifiedVertex svx2 = leftTerminal.getV2();
				if(svx2 != null) {
					rightTerminal.setV2(leftTerminal.getV2());
					svx2.addSEdge(rightTerminal);
				}
				this.simplifiedGraph.removeEdge(leftTerminal);
				return true;
			} 
		}
		
		return false;
		
//		int size = 14;
//		List<Edge> leftEdgeList = seLeft.getEdgeList();
//		List<Edge> rightEdgeList = seRight.getEdgeList();
//		int leftLastIndex = leftEdgeList.size() - 1;
//		Edge edgeR = leftSmallEdges.get(0);
//		List<Edge> tempEdgeList = new ArrayList<Edge>();
//		for(int j=leftLastIndex; j>leftLastIndex - size; j--) {
//			Edge edgeL = leftEdgeList.get(j);
//			tempEdgeList.add(edgeL);
//			if(edgeR.getString().equals(edgeL.getString())) {
//				leftEdgeList.removeAll(tempEdgeList);
//				leftEdgeList.addAll(rightEdgeList);
//				seLeft.setV2(seRight.getV2());
//				simplifiedGraph.removeEdge(seRight);
//				return;
//			}
//		}
	}
	
	/**
	 * Proc 4.
	 */
	private void dfs() {
		
		// print single string sequence
		List<SimplifiedEdge> tempSEdge = new ArrayList<SimplifiedEdge>();
		for(SimplifiedEdge sEdge: simplifiedGraph.getSEdgeList()) {
			List<Edge> edgeList = sEdge.getEdgeList();
			Vertex branchV1 = edgeList.get(0).getV1();
			Vertex branchV2 = edgeList.get(edgeList.size()-1).getV2();			
			SimplifiedVertex svx1 = simplifiedGraph.getSVertex(branchV1);
			SimplifiedVertex svx2 = simplifiedGraph.getSVertex(branchV2);
			if(svx1 == null && svx2 == null) {
				String seqString = getSequenceString(edgeList);
				if(seqString.length() >= Preference.CUTOFF_SEQUENCE_SIZE) {
					assembledNum++;
					String header = "nobranch_" + assembledNum;
					Sequence sequence = new Sequence(header, seqString);
					this.assembledSequences.add(sequence);	
				}
				tempSEdge.add(sEdge);
			}
		}
		for(SimplifiedEdge e: tempSEdge)
		simplifiedGraph.removeEdge(e);

		
		// print branch string sequence
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
			
//			if(i%10 == 0)
//				System.out.println("dfs number: " + i);
				
			if(seqStringListV1.size() == 0)
				seqStringListV1.add("");
			if(seqStringListV2.size() == 0)
				seqStringListV2.add("");
			
			for(String str1: seqStringListV1) {
				for(String str2: seqStringListV2) {
					String seqString = str1 + str2;

					if(seqString.length() >= Preference.CUTOFF_SEQUENCE_SIZE) {
						assembledNum++;
						String header = "branch_" + assembledNum;
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
		if(vertex != null) {
//			vertex.visited_traversal = true;
//			vertex.visited_seed = true;
			edgeList = vertex.getEdgeList();
		}
//		List<SimplifiedEdge> tempList = new ArrayList<SimplifiedEdge>();
//		for(SimplifiedEdge e: edgeList) {
//			if(!e.judged)
//				tempList.add(e);
//		}
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
					
					// check overlapped read coverage
					Vertex v1 = seedEdge.getEdgeList().get(0).getV2();
					Vertex v2 = edge.getEdgeList().get(edge.getEdgeList().size()-1).getV1();
					if(countOverlappedReads(v1, v2) <= Preference.CUTOFF_COVERAGE) {
						edge.visited_traversal = true;
						continue;
					}
					
					//
//					List<Integer> readListSeed = seedEdge.getEdgeList().get(0).getV2().getReadIndexList();
//					List<Integer> readListTarget = edge.getEdgeList().get(edge.getEdgeList().size()-1).getV1().getReadIndexList();
//					int coverageCount = 0;
//					for(int read: readListSeed) {
//						if(readListTarget.contains(read))
//							coverageCount++;
//					}
//					if(coverageCount <= Preference.CUTOFF_COVERAGE) {
//						edge.visited_traversal = true;
//						continue;
//					}
					
					stack.push(edge);
					traverseV1(edge, stack, seqStringList);
				}
			}
		}
		
		if(vertex == null || traverseCount == 0) {
//			String terminal = stopTraverse(stack.peek(), 1);
//			StringBuilder sb = new StringBuilder();
//			sb.append(terminal);
//			for(int i=stack.size()-2; i>0; i--) {
//				SimplifiedEdge se = stack.get(i);
//				for(Edge e: se.getEdgeList()) {
//					sb.append(e.getString().substring(e.getString().length()-1));
//				}
//			}
//			seqStringList.add(sb.toString());
			
			StringBuilder sb = new StringBuilder();
			Edge firstEg = stack.peek().getEdgeList().get(0);
			sb.append(firstEg.getString().substring(0, firstEg.getString().length()-1));
			for(int i=stack.size()-1; i>0; i--) {
				SimplifiedEdge se = stack.get(i);
				for(Edge e: se.getEdgeList()) {
					sb.append(e.getString().substring(e.getString().length()-1));
				}
			}
			seqStringList.add(sb.toString());
		}
		
//		stack.peek().visited_traversal = false;
		stack.pop();
	}
	
	/**
	 * 
	 * @param sedge
	 * @param direction 1=V1, 2=V2
	 */
	private String stopTraverse(SimplifiedEdge sedge, int direction) {
		StringBuilder sequence = new StringBuilder();
		if(direction == 1) {
			List<Edge> edgeList = sedge.getEdgeList();
			for(int i=edgeList.size()-1; i>=0; i--) {
				Edge e = edgeList.get(i);
				Vertex v1 = e.getV1();
				Vertex v2 = e.getV2();
				if(countOverlappedReads(v1, v2) > Preference.CUTOFF_COVERAGE) {
					sequence.insert(0, v2.getString().substring(Preference.VERTEX_SIZE-1));
				} else {
					sequence.insert(0, v2.getString());
//					System.out.println(sequence.toString());
					break;
				}
			}
		} else {
			for(Edge e: sedge.getEdgeList()) {
				Vertex v1 = e.getV1();
				Vertex v2 = e.getV2();
				if(countOverlappedReads(v1, v2) > Preference.CUTOFF_COVERAGE) {
					sequence.append(v2.getString().substring(Preference.VERTEX_SIZE-1));
				} else {
//					System.out.println(sequence.toString());
					break;
				}
			}	
		}
		return sequence.toString();
	}
	
	private int countOverlappedReads(Vertex v1, Vertex v2) {
		int countMappedRead = 0;
		List<Integer> readList1 = v1.getReadIndexList();
		List<Integer> readList2 = v2.getReadIndexList();
		for(int read: readList1) {
			if(readList2.contains(read))
				countMappedRead++;
		}
		return countMappedRead;
	}
	
	private void judgeBranch(List<SimplifiedEdge> edgeList) {
		List<SimplifiedEdge> tempList = new ArrayList<SimplifiedEdge>();
		for(SimplifiedEdge edge: edgeList) {
			edge.judged = true;
			if(!edge.visited_traversal && !(isShort(edge) && isTerminal(edge))){ //&& !isLowCoverage(edge)) {
				if(!tempList.contains(edge))
					tempList.add(edge);
			} else
				edge.visited_seed = true;
		}
		
		int length = tempList.size();
		for(int i=0; i<length-1; i++) {
			SimplifiedEdge edge1 = tempList.get(i);
			if(edge1.visited_traversal)
				continue;
			for(int j=i+1; j<length; j++) {
				SimplifiedEdge edge2 = tempList.get(j);
				if(edge2.visited_traversal)
					continue;
				
				if(isBubble_NoIdentity(edge1, edge2)) {
//						System.out.println("edge1 size: " + edge1.getEdgeList().size() + ", edge2 size: " + edge2.getEdgeList().size());
					int coverage1 = 0;
					int coverage2 = 0;
					for(Edge e: edge1.getEdgeList())
						coverage1 += e.getCoverage();
					for(Edge e: edge2.getEdgeList())
						coverage2 += e.getCoverage();
					if(coverage1 < coverage2)
						edge1.visited_traversal = true;
					else
						edge2.visited_traversal = true;
				}	
				
			}
		}
	}
	
	private boolean isShort(SimplifiedEdge edge) {
		return edge.getEdgeList().size() <= Preference.CUTOFF_TERMINAL_VERTEX_SIZE;
	}
	
	private boolean isTerminal(SimplifiedEdge edge) {
		return edge.getV1() == null || edge.getV2() == null;
	}
	
	private boolean isBubble(SimplifiedEdge edge1, SimplifiedEdge edge2) {
		if(edge1.getV1() == edge2.getV1() && edge1.getV2() == edge2.getV2())
			if(edge1.getV1() != null && edge1.getV2() != null) {
				double identity = NeedlemanWunsch.align(getSequenceString(edge1.getEdgeList()), getSequenceString(edge2.getEdgeList()));
//				System.out.println("identity = " + identity);
				if(identity >= 0.99)
					countBubbleOver99++;
				else if(identity < 0.95)
					countBubbleUnder95++;
//					NeedlemanWunsch.align(getSequenceString(edge1.getEdgeList()), getSequenceString(edge2.getEdgeList()));
				else
					countBubble99to95++;
				int sizeEdge1 = edge1.getEdgeList().size() - Preference.CUTOFF_BUBBLE_SIZE * 2;
				int sizeEdge2 = edge1.getEdgeList().size() - Preference.CUTOFF_BUBBLE_SIZE * 2;
				if(sizeEdge1 > Preference.CUTOFF_BUBBLE_SIZE || sizeEdge2 > Preference.CUTOFF_BUBBLE_SIZE)
					return false;
//				else
//					return true;
				return true;
			}
		
		return false;
	}
	int countTemp=0;
	private boolean isBubble_NoIdentity(SimplifiedEdge edge1, SimplifiedEdge edge2) {
		SimplifiedVertex s1v1 = edge1.getV1();
		SimplifiedVertex s1v2 = edge1.getV2();
		SimplifiedVertex s2v1 = edge2.getV1();
		SimplifiedVertex s2v2 = edge2.getV2();
		countTemp++;
		if(s1v1 != null && s1v2 != null && s2v1 != null && s2v2 != null) {
			int sizeEdge1 = edge1.getEdgeList().size() - Preference.VERTEX_SIZE - 1;
			int sizeEdge2 = edge2.getEdgeList().size() - Preference.VERTEX_SIZE - 1;
			if(sizeEdge1 <= Preference.CUTOFF_BUBBLE_SIZE && sizeEdge2 <= Preference.CUTOFF_BUBBLE_SIZE) {
				if(s1v1 == s2v1 && s1v2 == s2v2) {
//					System.out.println(countTemp + " " + s1v1.getVertex().getString() + " " + s1v2.getVertex().getString());
					double identity = NeedlemanWunsch.align(getSequenceString(edge1.getEdgeList()), getSequenceString(edge2.getEdgeList()));
					if(identity >= 0.99)
						countBubbleOver99++;
					else if(identity < 0.95)
						countBubbleUnder95++;
					else
						countBubble99to95++;
					
//					if(identity >= 0.9)
//						return true;
//					else
//						return false;
					return true;
				}	
			}
		}

		return false;
	}
	
	private boolean isLowCoverage(SimplifiedEdge edge) {
		int lowCoverage = 7;
//		boolean isLow = false;
		for(Edge e: edge.getEdgeList()) {
			if(e.getCoverage() <= lowCoverage)
				return true;
		}
//		if(edge.getEdgeList().get(0).getCoverage() <= lowCoverage)
//			return true;
//		else
			return false;
	}
		
	private void traverseV2(SimplifiedEdge seedEdge, Stack<SimplifiedEdge> stack, List<String> seqStringList) {
		SimplifiedVertex vertex = seedEdge.getV2();
		seedEdge.visited_traversal = true;
		seedEdge.visited_seed = true;
		List<SimplifiedEdge> edgeList = new ArrayList<SimplifiedEdge>();
		if(vertex != null) {
//			vertex.visited_traversal = true;
//			vertex.visited_seed = true;
			edgeList = vertex.getEdgeList();
		}
//		List<SimplifiedEdge> tempList = new ArrayList<SimplifiedEdge>();
//		for(SimplifiedEdge e: edgeList) {
//			if(!e.judged)
//				tempList.add(e);
//		}
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
					Vertex v1 = seedEdge.getEdgeList().get(seedEdge.getEdgeList().size()-1).getV1();
					Vertex v2 = edge.getEdgeList().get(0).getV2();
					if(countOverlappedReads(v1, v2) <= Preference.CUTOFF_COVERAGE) {
						edge.visited_traversal = true;
						continue;
					}
					
					//
//					List<Integer> readListSeed = seedEdge.getEdgeList().get(seedEdge.getEdgeList().size()-1).getV1().getReadIndexList();
//					List<Integer> readListTarget = edge.getEdgeList().get(0).getV2().getReadIndexList();
//					int coverageCount = 0;
//					for(int read: readListSeed) {
//						if(readListTarget.contains(read))
//							coverageCount++;
//					}
//					if(coverageCount <= Preference.CUTOFF_COVERAGE) {
//						edge.visited_traversal = true;
//						continue;
//					}
					
					stack.push(edge);
					traverseV2(edge, stack, seqStringList);
				}
			}
		}
		
		if(vertex == null || traverseCount == 0) {
//			String terminal = stopTraverse(seedEdge, 2);
//			StringBuilder sb = new StringBuilder();
//			for(int i=0; i<stack.size()-1; i++) {
//				SimplifiedEdge se = stack.get(i);
//				for(Edge e: se.getEdgeList())
//					sb.append(e.getString().substring(e.getString().length()-1));
//			}
//			sb.append(terminal);
//			seqStringList.add(sb.toString());
			
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<stack.size(); i++) {
				SimplifiedEdge se = stack.get(i);
				for(Edge e: se.getEdgeList())
					sb.append(e.getString().substring(e.getString().length()-1));
			}
			seqStringList.add(sb.toString());
		}
		
//		stack.peek().visited_traversal = false;
		stack.pop();
	}
	
	private String getSequenceString(List<Edge> edgeList) {
		StringBuilder sb = new StringBuilder(edgeList.get(0).getString());
		for(int j=1; j<edgeList.size(); j++) {
			Edge tmpE = edgeList.get(j); 
			sb.append(tmpE.getString().substring(tmpE.getString().length()-1));
		}
		return sb.toString();
	}
	
	
	// TODO calculate similarity of Simplified Edges
	private double calculateSequenceSimilarity(SimplifiedEdge edge1, SimplifiedEdge edge2) {
		return 0.0;
	}
	
	private void clusteringMerge() {
		for(int i=0; i<assembledSequences.size()-1; i++) {
			Sequence seqA = assembledSequences.get(i);
//			boolean clustered = false;
			for(int j=i+1; j<assembledSequences.size(); j++ ) {
				Sequence seqB = assembledSequences.get(j);
				if(seqA != null && seqB != null) {
//					double identity = NeedlemanWunsch.align(seqA.getString(), seqB.getString());
					List<Sequence> q = new ArrayList<Sequence>();
					q.add(seqA);
					List<Sequence> t = new ArrayList<Sequence>();
					t.add(seqB);
					Blast blast = new Blast(q, t);
					blast.runBlast();
					BlastReader br = new BlastReader(Preference.OUTPUT_BLAST_PATH);
			    	List<BlastResult> bList = null;
					try {
						bList = br.readTable();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	double identity = bList.get(0).getIdentity();
					if(identity >= 70) {
//						clustered = true;
						assembledSequences.remove(j);
						System.out.println("identity = " + identity + "\t" + seqA.getString());
					}
				}
			}
			clusteredSequences.add(seqA);
		}
		return;
	}
}
