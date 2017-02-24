package dna.spa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Vertex implements Comparable<Vertex> {

	private ArrayList<Edge> edgeList;
	private List<String> readFragmentIDList;
	private String string;
	private int coverage;
	public double f_value;
	public boolean visited;
	
	public Vertex(String string) {
		this.string = string;
		edgeList = new ArrayList<Edge>();
		readFragmentIDList = new ArrayList<String>();
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

	public ArrayList<Edge> getEdgeList() {
		return edgeList;
	}

	public void addEdge(Edge edge) {
		edgeList.add(edge);
	}

	public List<String> getReadFragmentIDList() {
		return readFragmentIDList;
	}
	
	public void addReadFragmentIDList(String readFragID) {
		readFragmentIDList.add(readFragID);
	}

	@Override
	public String toString() {
		return "Vertex [string=" + string + ", coverage=" + coverage + "]";
	}

	public int compareTo(Vertex o) {
		return coverage > o.coverage ? 1 : (coverage == o.coverage ? 0 : -1);
	}
	
}

class FValueComparator implements Comparator<Vertex> {

	public int compare(Vertex o1, Vertex o2) {
		double f1 = o1.f_value;
		double f2 = o2.f_value;
		return f1 > f2 ? 1 : (f1 == f2 ? 0 : -1);
	}
	
}
