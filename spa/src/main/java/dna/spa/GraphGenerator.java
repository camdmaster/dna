package dna.spa;

import java.util.ArrayList;
import java.util.List;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Vertex;

public class GraphGenerator {

	public GraphGenerator() {
		
	}
	
	public static Graph generate(List<Sequence> sequenceList) {
		long startTime = System.nanoTime();
		System.out.println("<Graph Generation>");
    	// make graph
    	Graph graph = new Graph(sequenceList);
    	int vertexLength = 12;
    	int edgeLength = vertexLength + 1;
    	
    	for(int j=0; j<sequenceList.size(); j++) {
//    	for(Sequence seq: sequenceList) {
    		Sequence seq = sequenceList.get(j);
    		String string = seq.getString();
    		int sequenceLength = string.length();
    		
    		String v = string.substring(0, vertexLength);
    		Vertex vertex = null;
        	if(graph.existVertex(v)) {
    			vertex = graph.getVertex(v);
    			vertex.addCoverage();
    			vertex.addReadIndex(j);
    		} else {
    			vertex = new Vertex(v);
    			vertex.addReadIndex(j);
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
    				vertex2.addReadIndex(j);
    			} else {
    				vertex2 = new Vertex(v2);
    				vertex2.addReadIndex(j);
//    				vertex2.addReadFragmentIDList(fragID);
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
    	
    	long endTime = System.nanoTime();
    	System.out.println("Graph TIME : " + (endTime - startTime) /1000000.0 + " (ms)");
    	
    	return graph;
    }
	
	private static String parseReadHeader(String header) {
		// parse DWGSIM header
		return header.split(":")[1].split("_")[0];
	}
}
