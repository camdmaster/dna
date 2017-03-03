package dna.graph;

import java.util.ArrayList;
import java.util.List;

public class SimplifiedVertex {

	private Vertex vertex;
	private List<SimplifiedEdge> edgeList;
	public boolean visited;
	
	public SimplifiedVertex(Vertex vertex) {
		this.vertex = vertex;
		edgeList = new ArrayList<SimplifiedEdge>();
	}
	
	public Vertex getVertex() {
		return vertex;
	}

	public List<SimplifiedEdge> getEdgeList() {
		return edgeList;
	}

	public void addSEdge(SimplifiedEdge edge) {
		edgeList.add(edge);
	}
}
