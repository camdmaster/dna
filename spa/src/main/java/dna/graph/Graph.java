package dna.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import dna.spa.Sequence;


public class Graph {
	
	HashMap<String, Vertex> vertexMap;
	List<Edge> edgeList;
//	HashMap<String, Edge> edgeMap;
//	List<Sequence> readList;
	
	public Graph() {
		vertexMap = new HashMap<String, Vertex>();
		edgeList = new ArrayList<Edge>();
//		edgeMap = new HashMap<String, Edge>();
//		this.readList = readList;
	}
	
	public void addVertex(String key, Vertex vertex) {
		vertexMap.put(key, vertex);
	}
	
	public void addEdge(Edge edge) {
		edgeList.add(edge);
//		edgeMap.put(key, edge);
	}
	
	public boolean existVertex(String string) {
		if(vertexMap.containsKey(string))
			return true;
		else
			return false;
	}
	
	public Vertex getVertex(String string) {
		if(vertexMap.containsKey(string))
			return vertexMap.get(string);
		else
			return null;
	}

	public HashMap<String, Vertex> getVertexMap() {
		return vertexMap;
	}

//	public boolean existEdge(String string) {
//		if(edgeMap.containsKey(string))
//			return true;
//		else
//			return false;
//	}
	
	public void addVertexCoverage(String string) {
		if(vertexMap.containsKey(string)) {
			Vertex v = vertexMap.get(string);
			v.addCoverage();
		}
	}
	
//	public void addEdgeCoverage(String string) {
//		if(edgeMap.containsKey(string)) {
//			Edge e = edgeMap.get(string);
//			e.addCoverage();
//		}
//	}
	
	public Edge getEdge(Vertex v1, Vertex v2) {
		for(Edge e1: v1.getEdgeList())
			for(Edge e2: v2.getEdgeList())
				if(e1.equals(e2))
					return e1;
		return null;
	}
	
	public void setFValue() {
		Iterator<Vertex> iter = vertexMap.values().iterator();
		while(iter.hasNext()) {
			Vertex v = iter.next();
			double f = (double)v.getCoverage() / Math.exp((double)v.getEdgeList().size());
			v.f_value = f;
		}
	}
	
	public void resetVisited() {
		Iterator<Vertex> iter = vertexMap.values().iterator();
		while(iter.hasNext()) {
			Vertex v = iter.next();
			v.visited = false;
		}
		for(Edge e: edgeList)
			e.visited = false;
//		Iterator<Edge> iter2 = edgeMap.values().iterator();
//		while(iter2.hasNext()) {
//			Edge e = iter2.next();
//			e.visited = false;
//		}
	}
	
	public List<Vertex> getSeedVertex() {
//		List<Vertex> list = new ArrayList<Vertex>(vertexMap.values());
//		Collections.sort(list);
//		Collections.reverse(list);
		
//		List<Vertex> candidates = list.subList(0, 500);
		List<Vertex> candidates = new ArrayList<Vertex>(vertexMap.values());
		Collections.sort(candidates, new FValueComparator());
		Collections.reverse(candidates);
		
		return candidates;
	}
	
	public List<Vertex> getVerticeOrderedByCoverage() {
		List<Vertex> list = new ArrayList<Vertex>(vertexMap.values());
		Collections.sort(list);
		Collections.reverse(list);
		
		return list;
	}
	
	public List<Edge> getEdgeList() {
//		List<Edge> list = new ArrayList<Edge>(edgeMap.values());
		return edgeList;
	}
	
	/**
	 * Depth first search
	 * direction: v1(left)
	 */
	@Deprecated
	public void traverseV1(Vertex vertex, StringBuilder string, Stack<Vertex> stack, ArrayList<String> seqList) {
//		ArrayList<String> seqList = new ArrayList<String>();
		ArrayList<Edge> edgeList = vertex.getEdgeList();
		Vertex tmpVertex = null;
//		Vertex extVertex = null;
//		int maxCoverage = 0;
//		double maxFValue = 0;
		for(Edge edge: edgeList) {
			if(edge.getV2().equals(vertex)) {
				tmpVertex = edge.getV1();
				if(stack.contains(tmpVertex))
					continue;
				edge.visited = true;
				tmpVertex.visited = true;
				string.append(tmpVertex.getString().substring(tmpVertex.getString().length()-1));
				stack.push(tmpVertex);
				traverseV1(tmpVertex, string, stack, seqList);
			}
		}
		
		if(tmpVertex == null) {
			StringBuilder sb = new StringBuilder(stack.get(0).getString());
			for(int i=1; i<stack.size(); i++) {
				Vertex v = stack.get(i);
				sb.insert(0, v.getString().charAt(0));
			}
//			System.out.println("\t" + sb.toString());
			seqList.add(sb.toString());
		}
		stack.pop();
//		return seqList;
	}
	
	/**
	 * Depth first search
	 * Direction: v2(right)
	 */
	@Deprecated
	public void traverseV2(Vertex vertex, StringBuilder string, Stack<Vertex> stack, ArrayList<String> seqList) {
//		ArrayList<String> seqList = new ArrayList<String>();
		ArrayList<Edge> edgeList = vertex.getEdgeList();
//		Vertex extVertex = null;
//		int maxCoverage = 0;
//		double maxFValue = 0;
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
//				if((tmpVertex.getCoverage() > maxCoverage) ||
//						((tmpVertex.getCoverage() == maxCoverage) && tmpVertex.f_value > maxFValue) ) {
//					maxCoverage = tmpVertex.getCoverage();
//					maxFValue = tmpVertex.f_value;
//					extVertex = tmpVertex;
//					
//					edge.visited = true;
//					extVertex.visited = true;
//					
//					traverseV2(extVertex, string);
//				} 
			}
		}
		
		if(tmpVertex == null) {
			StringBuilder sb = new StringBuilder(stack.get(0).getString());
			for(int i=1; i<stack.size(); i++) {
				Vertex v = stack.get(i);
				sb.append(v.getString().substring(v.getString().length()-1));
			}
//			System.out.println("\t" + sb.toString());
			seqList.add(sb.toString());
		}
		stack.pop();
//		return seqList;
	}
	
	public void removeVisitedGraph() {
//		List<Edge> elist = new ArrayList<Edge>(edgeMap.values());
		for(Edge e: edgeList) {
			if(e.visited)
				removeEdge(e);
//				edgeMap.remove(e.getString());
		}
		
		List<Vertex> vlist = new ArrayList<Vertex>(vertexMap.values());
		for(Vertex v: vlist) {
			if(v.getEdgeList().size() == 0)
				removeVertexAndConnectedEdges(v);
//			if(v.visited)
//				vertexMap.remove(v.getString());
		}
	}
	
	public void removeEdge(Edge edge) {
		edgeList.remove(edge.getString());
		edge.getV1().removeEdge(edge);
		edge.getV2().removeEdge(edge);
	}
	
	public void removeVertexAndConnectedEdges(Vertex vertex) {
		vertexMap.remove(vertex.getString());
		while(vertex.getEdgeList().size() != 0) {
			Edge e = vertex.getEdgeList().get(0);
			removeEdge(e);
		}
//		for(int i=vertex.getEdgeList().size()-1; i>=0; i--) {
//			Edge e = vertex.getEdgeList().get(i);
//			removeEdge(e);
//		}
	}
	
	public void removeVertexOnly(Vertex vertex) {
		vertexMap.remove(vertex.getString());
	}
}
