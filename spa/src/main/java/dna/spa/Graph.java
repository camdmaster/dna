package dna.spa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
		List<Vertex> list = new ArrayList<Vertex>(vertexMap.values());
		Collections.sort(list);
		Collections.reverse(list);
		
		List<Vertex> candidates = list.subList(0, 100);
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
	
	public String traverseV1(Vertex vertex, StringBuilder string) {
		ArrayList<Edge> edgeList = vertex.getEdgeList();
		Vertex extVertex = null;
		int maxCoverage = 0;
		double maxFValue = 0;
		for(Edge edge: edgeList) {
			if(edge.getV2().equals(vertex) && !edge.visited) {
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
			string.insert(0, extVertex.getString().substring(0,1));
//			System.out.println(string);
			traverseV1(extVertex, string);
		} 
//		else {
//			System.out.println(string);
//		}
		
		return string.toString();
	}
	
	public String traverseV2(Vertex vertex, StringBuilder string) {
		ArrayList<Edge> edgeList = vertex.getEdgeList();
		Vertex extVertex = null;
		int maxCoverage = 0;
		double maxFValue = 0;
		for(Edge edge: edgeList) {
			if(edge.getV1().equals(vertex) && !edge.visited) {
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
			string.append(extVertex.getString().substring(extVertex.getString().length()-1));
//			System.out.println(string);
			traverseV2(extVertex, string);
		} 
//		else {
//			System.out.println(string);
//		}
		
		return string.toString();
	}
}
