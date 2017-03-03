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
	
	public void removeSEdge(SimplifiedEdge edge) {
		edgeList.remove(edge);
	}
	
}
