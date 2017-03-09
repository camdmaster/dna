package dna.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimplifiedGraph {

	HashMap<Vertex, SimplifiedVertex> vertexMap;
	List<SimplifiedEdge> edgeList;
	
	public SimplifiedGraph() {
		vertexMap = new HashMap<Vertex, SimplifiedVertex>();
		edgeList = new ArrayList<SimplifiedEdge>();
	}
	
	public void addSVertex(SimplifiedVertex sVertex) {
		vertexMap.put(sVertex.getVertex(), sVertex);
	}
	
	public SimplifiedVertex getSVertex(Vertex vertex) {
		if(vertexMap.containsKey(vertex))
			return vertexMap.get(vertex);
		else
			return null;
	}
	
	public List<SimplifiedVertex> getSVertexList() {
		return new ArrayList<SimplifiedVertex>(vertexMap.values());
	}
	
	public void addSEdge(SimplifiedEdge edge) {
		edgeList.add(edge);
	}

	public List<SimplifiedEdge> getEdgeList() {
		return edgeList;
	}
	
	public void removeVisited() {
		for(int i=edgeList.size()-1; i>=0; i--) {
			SimplifiedEdge edge = edgeList.get(i);
			if(edge.visited)
				removeEdge(edge);
		}
		
		List<SimplifiedVertex> vlist = new ArrayList<SimplifiedVertex>(vertexMap.values());
		for(SimplifiedVertex v: vlist) {
			if(v.getEdgeList().size() == 0)
				removeVertex(v);
		}
	}
	
	public void removeEdge(SimplifiedEdge edge) {
		edgeList.remove(edge);
		if(edge.getV1() != null)
			edge.getV1().removeEdge(edge);
		if(edge.getV2() != null)
			edge.getV2().removeEdge(edge);
	}
	
	public void removeVertex(SimplifiedVertex vertex) {
		vertexMap.remove(vertex.getVertex());
		for(int i=vertex.getEdgeList().size()-1; i>=0; i--) {
			SimplifiedEdge e = vertex.getEdgeList().get(i);
			removeEdge(e);
		}
	}
	
//	public void removeSEdge(SimplifiedEdge edge) {
//		edgeList.remove(edge);
//	}
	
}
