package dna.spa;

public class Edge {

	private Vertex v1;
	private Vertex v2;
	private String string;
	private int coverage;
	public boolean visited;
	
	public Edge(Vertex v1, Vertex v2, String string) {
		this.v1 = v1;
		this.v2 = v2;
		this.string = string;
		this.coverage = 1;
	}

	public String getString() {
		return string;
	}
	
	public void addCoverage() {
		coverage++;
	}

	public int getCoverage() {
		return coverage;
	}

	public Vertex getV1() {
		return v1;
	}

	public Vertex getV2() {
		return v2;
	}

	@Override
	public String toString() {
		return "Edge [string=" + string + ", coverage=" + coverage + "]";
	}
	
}
