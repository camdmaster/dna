package dna.spa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class Graph {
	
	HashMap<String, Vertex> vertexMap;
	HashMap<String, Edge> edgeMap;
	
	public Graph() {
		vertexMap = new HashMap<String, Vertex>();
		edgeMap = new HashMap<String, Edge>();
	}
	
	public void addVertex(Vertex vertex) {
		vertexMap.put(vertex.getString(), vertex);
	}
	
	public void addEdge(Edge edge) {
		edgeMap.put(edge.getString(), edge);
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

	public boolean existEdge(String string) {
		if(edgeMap.containsKey(string))
			return true;
		else
			return false;
	}
	
	public void addVertexCoverage(String string) {
		if(vertexMap.containsKey(string)) {
			Vertex v = vertexMap.get(string);
			v.addCoverage();
		}
	}
	
	public void addEdgeCoverage(String string) {
		if(edgeMap.containsKey(string)) {
			Edge e = edgeMap.get(string);
			e.addCoverage();
		}
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
		Iterator<Edge> iter2 = edgeMap.values().iterator();
		while(iter2.hasNext()) {
			Edge e = iter2.next();
			e.visited = false;
		}
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
		List<Edge> list = new ArrayList<Edge>(edgeMap.values());
		return list;
	}
	
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
		List<Edge> elist = new ArrayList<Edge>(edgeMap.values());
		for(Edge e: elist) {
			if(e.visited)
				edgeMap.remove(e.getString());
		}
		
		List<Vertex> vlist = new ArrayList<Vertex>(vertexMap.values());
		for(Vertex v: vlist) {
			if(v.visited)
				vertexMap.remove(v.getString());
		}
	}
	
}
