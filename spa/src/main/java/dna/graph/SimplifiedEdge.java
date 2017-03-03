package dna.graph;

import java.util.List;

public class SimplifiedEdge {

	List<Vertex> sequence;
	private SimplifiedVertex v1;
	private SimplifiedVertex v2;
	public boolean visited;
	
	public SimplifiedEdge(List<Vertex> sequence) {
		this.sequence = sequence;
	}

	public List<Vertex> getSequence() {
		return sequence;
	}

	public SimplifiedVertex getV1() {
		return v1;
	}

	public void setV1(SimplifiedVertex v1) {
		this.v1 = v1;
	}

	public SimplifiedVertex getV2() {
		return v2;
	}

	public void setV2(SimplifiedVertex v2) {
		this.v2 = v2;
	}

}
