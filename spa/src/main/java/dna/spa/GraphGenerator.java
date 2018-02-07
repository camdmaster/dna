package dna.spa;

import java.util.HashMap;
import java.util.List;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Vertex;

public class GraphGenerator {

	public GraphGenerator() {
		
	}
	
	public static HashMap<String, Vertex> generateOnlyVertices(List<Sequence> sequenceList) {
		long startTime = System.nanoTime();
//		System.out.println("<Vertex Generation>");
    	// make graph
		HashMap<String, Vertex> vertexMap = new HashMap<String, Vertex>();
    	int vertexLength = Preference.VERTEX_SIZE;
//    	int edgeLength = vertexLength + 1;
    	
    	int seqSize = sequenceList.size();
    	long startTime1 = System.nanoTime();
    	for(int j=0; j<seqSize; j++) {
    		if(j%1000000==0 && j != 0) {
    			long endTime1 = System.nanoTime();
    	    	long lTime = endTime1 - startTime1;
    	    	System.out.println(j + " TIME : " + lTime/1000000.0 + " (ms)" + ", vertex: " + vertexMap.size());
    	    	startTime1 = endTime1;
    		}
    		Sequence seq = sequenceList.get(j);
    		String string = seq.getString();
    		int sequenceLength = string.length();
    		
    		if(sequenceLength < vertexLength)
    			continue;
    		
    		for(int i=0;i<sequenceLength-vertexLength+1; i++) {
    			String v = string.substring(i, i+vertexLength);
        		
        		Vertex vertex = vertexMap.get(v);
            	if(vertex != null) {
        			vertex.addCoverage();
        			vertex.addReadIndex(j);
        		} else {
        			vertex = new Vertex(v);
        			vertex.addReadIndex(j);
        			vertexMap.put(v, vertex);
        		}
    		}
    		    		
    	}
    	
    	long endTime = System.nanoTime();
//    	System.out.println("Vertices size: " + vertexMap.size());
//    	System.out.println("Vertices TIME : " + (endTime - startTime) /1000000.0 + " (ms)");
    	
    	return vertexMap;
	}
	
	public static Graph generate(List<Sequence> sequenceList, int kmerSize) {
		long startTime = System.nanoTime();
//		System.out.println("<Graph Generation>");
    	// make graph
    	Graph graph = new Graph();
    	int vertexLength = kmerSize;
    	int edgeLength = vertexLength + 1;
    	
    	int seqSize = sequenceList.size();
//    	int seqSize = 1000002;
    	long startTime1 = System.nanoTime();
    	for(int j=0; j<seqSize; j++) {
    		if(j%1000000==0 && j != 0) {
    			long endTime1 = System.nanoTime();
    	    	long lTime = endTime1 - startTime1;
//    	    	System.out.println(j + " TIME : " + lTime/1000000.0 + " (ms)" + ", vertex: " + graph.getVertexMap().size());
    	    	startTime1 = endTime1;
    		}
//    	for(Sequence seq: sequenceList) {
    		Sequence seq = sequenceList.get(j);
    		String string = seq.getString();
    		int sequenceLength = string.length();
    		
    		if(sequenceLength < vertexLength)
    			continue;
    		String v = string.substring(0, vertexLength);
    		
    		Vertex vertex = graph.getVertex(v);
        	if(vertex != null) {
    			vertex.addCoverage();
    			vertex.addReadIndex(j);
    		} else {
    			vertex = new Vertex(v);
    			vertex.addReadIndex(j);
    			graph.addVertex(v, vertex);
    		}
        	
        	Vertex vertex1 = vertex;
        	Vertex vertex2 = null;
        	
    		for(int i=0; i<sequenceLength-vertexLength; i++) {
//    			String v1 = string.substring(i, i+vertexLength);
    			String v2 = string.substring(i+1, i+vertexLength+1);
    			String ed = string.substring(i, i+edgeLength);

    			vertex2 = graph.getVertex(v2);
    			if(vertex2 != null) {
    				vertex2.addCoverage();
    				vertex2.addReadIndex(j);
    			} else {
    				vertex2 = new Vertex(v2);
    				vertex2.addReadIndex(j);
    				graph.addVertex(v2, vertex2);
    			}
    			
    			Edge edge = graph.getEdge(vertex1, vertex2);
    			if(edge != null) {
    				edge.addCoverage();
    			} else {
    				edge = new Edge(vertex1, vertex2, ed);
    				vertex1.addEdge(edge);
    				vertex2.addEdge(edge);
    				graph.addEdge(edge);
    			}
    			
    			vertex1 = vertex2;
    		}
    	}
    	graph.setFValue();
    	
    	long endTime = System.nanoTime();
//    	System.out.println("Vertex size: " + graph.getVertexMap().size());
//    	System.out.println("Edge size: " + graph.getEdgeList().size());
//    	System.out.println("Graph TIME : " + (endTime - startTime) / 1000000.0 + " (ms)");
    	
    	return graph;
	}
	
	private static String parseReadHeader(String header) {
		// parse DWGSIM header
		return header.split(":")[1].split("_")[0];
	}
}