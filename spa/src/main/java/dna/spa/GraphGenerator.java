package dna.spa;

import java.util.ArrayList;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Vertex;

public class GraphGenerator {

	public GraphGenerator() {
		
	}
	
	public static Graph generate(ArrayList<Sequence> sequenceList) {
    	// make graph
    	Graph graph = new Graph();
    	int vertexLength = 12;
    	int edgeLength = vertexLength + 1;
    	
    	for(Sequence seq: sequenceList) {
    		String string = seq.getString();
    		int sequenceLength = string.length();
    		
    		String v = string.substring(0, vertexLength);
    		Vertex vertex = null;
    		String fragID = parseReadHeader(seq.getHeader());
        	if(graph.existVertex(v)) {
    			vertex = graph.getVertex(v);
    			vertex.addCoverage();
    			vertex.addReadFragmentIDList(fragID);
    		} else {
    			vertex = new Vertex(v);
    			vertex.addReadFragmentIDList(fragID);
    			graph.addVertex(vertex);
    		}
        	
    		for(int i=0; i<sequenceLength-vertexLength; i++) {
    			String v1 = string.substring(i, i+vertexLength);
    			String v2 = string.substring(i+1, i+vertexLength+1);
    			String ed = string.substring(i, i+edgeLength);
    			
    			Vertex vertex1 = null;
    			Vertex vertex2 = null;
    			
    			if(graph.existVertex(v2)) {
    				vertex2 = graph.getVertex(v2);
    				vertex2.addCoverage();
    				vertex2.addReadFragmentIDList(fragID);
    			} else {
    				vertex2 = new Vertex(v2);
    				vertex2.addReadFragmentIDList(fragID);
    				graph.addVertex(vertex2);
    			}
    			
    			vertex1 = graph.getVertex(v1);
    			if(graph.existEdge(ed)) {
    				graph.addEdgeCoverage(ed);
    			} else {
    				Edge edge = new Edge(vertex1, vertex2, ed);
    				vertex1.addEdge(edge);
    				vertex2.addEdge(edge);
    				graph.addEdge(edge);
    			}
    		}
    	}
    	graph.setFValue();
    	
    	return graph;
    }
	
	private static String parseReadHeader(String header) {
		// parse DWGSIM header
		return header.split(":")[1].split("_")[0];
	}
}
