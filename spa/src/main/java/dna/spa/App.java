package dna.spa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import dna.analysis.Analysis;
import dna.analysis.BlastResult;
import dna.assembly.Assembly_SimpleGraphMethod;
import dna.graph.Graph;
import dna.graph.Vertex;
import dna.simulator.ReadGenerator;
import dna.spa.io.BlastReader;
import dna.spa.io.FastaReader;
import dna.spa.io.FastaWriter;
import dna.util.Blast;

//-XX:MaxMetaspaceSize=300000M
//-Xms200000M
//-Xmx200000M

/**
 * Start Point
 *
 */
public class App 
{
	
    public static void main( String[] args )
    {    	
    	readLinuxPreferences();
//    	readWindowsPreferences();
    	
    	// job start
    	long startTime = System.nanoTime();
    	job_main();
    	
//    	try {
////			checkCoverageReadRef();
//    		test();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
//    	int size = 100000000;
//    	HashMap<Integer, Integer> tmap = new HashMap<Integer, Integer>();
//    	ArrayList<Integer> tlist = new ArrayList<Integer>();
//    	long startTime1 = System.nanoTime();
//    	for(int j=0; j<size; j++) {
//    		tmap.put(j, j);
//    		tlist.add(j);
//    		if(j%1000000==0) {
//    			long endTime1 = System.nanoTime();
//    	    	long lTime = endTime1 - startTime1;
//    	    	System.out.println(j + " TIME : " + lTime/1000000.0 + " (ms)");
//    	    	startTime1 = endTime1;
//    		}
//    	}
    	
//    	Preference.INPUT_DNA_READ_PATH = "/data1/yjseo/data/read/NC_014034_d70_e0_single/NC_014034_l150_d70_e0_single.bwa.read1.fasta";
//    	Preference.INPUT_DNA_READ_PATH = "/data1/yjseo/20170419/NC_018936_l150_d50_e0_single.bwa.read1.fgs";
//    	FragGeneScan fgs = new FragGeneScan();
//    	fgs.runScan();

    	long endTime = System.nanoTime();
    	long lTime = endTime - startTime;
    	System.out.println("Overall TIME : " + lTime/1000000.0 + " (ms)");
    	System.out.println("Done.");
    	
    	
		// depth first search
//		Graph graph = makeGraph(readList);
//		traverseGraph(graph);

		// SPA based search
//		Graph graph = GraphGenerator.generate(readList);
//		SequenceGenerator sg = new SequenceGenerator(graph);
//		sg.traverseGraph();
    }
    
    private static void readLinuxPreferences() {
    	// set IO path
    	Preference.INPUT_READ_PATH = "/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_006814.read1.fgs.faa";
    	Preference.INPUT_BLASTDB_PATH = "/data1/yjseo/data_spa/ds3/faa_collection/NC_006814.cluster.faa";
    	Preference.OUTPUT_ASB_PATH = "/data1/yjseo/20180131/NC_006814.asb.faa";
    	Preference.OUTPUT_ASB_CLUSTER_PATH = "/data1/yjseo/20180131/NC_006814.asb.cluster.faa";
    	Preference.OUTPUT_BLAST_PATH = "/data1/yjseo/20180131/NC_006814.asb.cluster.blast.out";
    	Preference.OUTPUT_ANALYSIS_PATH = "/data1/yjseo/20180131/NC_006814.asb.cluster.analysis.out";
    	Preference.OUTPUT_LOG_PATH = "/data1/yjseo/20180131/NC_006814.log";
    	
//    	Preference.INPUT_READ_PATH = "/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/spa_wgsim_read1_ds3.fgs.faa";
//    	Preference.INPUT_BLASTDB_PATH = "/data1/yjseo/data_spa/ds3/faa_collection/spa_ds3.cluster.faa";
//    	Preference.OUTPUT_ASB_PATH = "/data1/yjseo/20180131/spa_ds3.asb.faa";
//    	Preference.OUTPUT_ASB_CLUSTER_PATH = "/data1/yjseo/20180131/spa_ds3.asb.cluster.faa";
//    	Preference.OUTPUT_BLAST_PATH = "/data1/yjseo/20180131/spa_ds3.asb.cluster.blast.out";
//    	Preference.OUTPUT_ANALYSIS_PATH = "/data1/yjseo/20180131/spa_ds3.asb.cluster.analysis.out";
//    	Preference.OUTPUT_LOG_PATH = "/data1/yjseo/20180131/spa_ds3.log";
    }
    
    private static void readWindowsPreferences() {
    	// set IO path
    	Preference.INPUT_READ_PATH = "F:\\Dropbox\\DNA\\20160929_SPA\\20180131\\NC_006814_test.read1.fgs.faa";
    	Preference.INPUT_BLASTDB_PATH = "/data1/yjseo/data_spa/ds3/faa_collection/NC_006814.cluster.faa";
    	Preference.OUTPUT_ASB_PATH = "F:\\Dropbox\\DNA\\20160929_SPA\\20180131\\out.asb.faa";
    	Preference.OUTPUT_ASB_CLUSTER_PATH = "/data1/yjseo/20180122/NC_006814.asb.cluster.faa";
    	Preference.OUTPUT_BLAST_PATH = "/data1/yjseo/20180122/NC_006814.asb.cluster.blast.out";
    	Preference.OUTPUT_ANALYSIS_PATH = "/data1/yjseo/20180122/NC_006814.asb.cluster.analysis.out";
    	Preference.OUTPUT_LOG_PATH = "F:\\Dropbox\\DNA\\20160929_SPA\\20180131\\NC_006814.log";
    }
    
    private static void checkCoverageReadRef() throws IOException {
    	System.out.println("<make reference sequence>");
    	BufferedWriter bw = new BufferedWriter(new FileWriter(Preference.OUTPUT_LOG_PATH));
    	FastaReader readerDB = new FastaReader("/data1/yjseo/data_spa/ds3/faa_collection/spa_ds3.cluster.faa");
//    	FastaReader readerDB = new FastaReader("/data1/yjseo/20171121/wgsim_0/spa_wgsim_read1_ds3.asb.cluster.branch.faa");
    	List<Sequence> seqList = readerDB.read();
    	System.out.println("reference gene count: " + seqList.size());
    	int shortSeq = 0;
    	for(int i=seqList.size()-1; i>=0; i--) {
    		Sequence s = seqList.get(i);
    		bw.write(s.getString().length() + "\r\n");
//    		if(s.getString().length() < 300) {
////    			System.out.println("short sequence id: " + s.getHeader());
//    			shortSeq++;
//    			seqList.remove(s);
//    		}
    	}
    	
    	System.out.println("short sequence count: " + shortSeq);
    	
    	HashMap<String, Vertex> vertexMap = GraphGenerator.generateOnlyVertices(seqList);
    	List<Vertex> vList = new ArrayList<Vertex>(vertexMap.values());
    	for(Vertex v: vList) {
    		v.setCoverage(1000);
    	}
    	System.out.println("Ref vertices size: " + vertexMap.size());
    	
    	System.out.println("<search uncovered region>");
//    	FastaReader readerQUE = new FastaReader("/data1/yjseo/20171121/sfaspa/single_0/post.fasta");
    	FastaReader readerQUE = new FastaReader("/data1/yjseo/20171121/wgsim_0/spa_wgsim_read1_ds3.asb.cluster.faa");
//    	FastaReader readerQUE = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/spa_wgsim_read1_ds3.fgs.faa");
    	List<Sequence> seqListQUE = readerQUE.read();
    	System.out.println("Read size: " + seqListQUE.size());
    	int i=0;
    	
    	HashSet<String> mutatedSequenceSet = new HashSet<String>();
    	for(Sequence seq: seqListQUE) {
//    		i++;
//    		if(i%10000 == 0)
//    			System.out.println("read " + i + " Mutation size: " + mutatedSequenceSet.size());
    			
    		List<Sequence> oneSeq = new ArrayList<Sequence>();
    		oneSeq.add(seq);
        	
        	HashMap<String, Vertex> vertexMapQUE = GraphGenerator.generateOnlyVertices(oneSeq);
        	List<Vertex> vListQUE = new ArrayList<Vertex>(vertexMapQUE.values());
        	for(Vertex v: vListQUE) {
        		String key = v.getString();
        		if(vertexMap.containsKey(key)) {
        			vertexMap.get(key).addCoverage();
        		} else {
        			vertexMap.put(key, v);
//        			bw.write("mutation: " + seq.getHeader() + " " + key + "\r\n");
//        			if(!mutatedSequenceSet.contains(key))
//        				mutatedSequenceSet.add(key);
        		}
        	}	
    	}
    	System.out.println("Total Vertice: " + vertexMap.size());
    	
    	System.out.println("<print uncovered region>");
    	StringBuilder sbC = new StringBuilder();
    	StringBuilder sbM = new StringBuilder();
    	int uncovered = 0;   	
    	int mutation = 0;
    	vList = new ArrayList<Vertex>(vertexMap.values());
    	for(Vertex v: vList) {
    		if(v.getCoverage() == 1000) {
    			// uncovered
//    			System.out.println("uncovered: " + v.getString());
    			sbC.append("uncovered: " + seqList.get(v.getReadIndexList().get(0)).getHeader() + " " + v.getString() + "\r\n");
//    			sb.append("uncovered: " + v.getString() + "\r\n");
    			uncovered++;
    		} 
    		else if(v.getCoverage() < 1000) {
    			// mutation?
//    			System.out.println("mutation: " + v.getString());
//    			sb.append("mutation: " + v.getString() + "\r\n");
    			sbM.append("mutation: " + seqList.get(v.getReadIndexList().get(0)).getHeader() + " " + v.getString()+ "\r\n");
    			mutation++;
    		}
    	}
    	Iterator<String> iter = mutatedSequenceSet.iterator();
    	while(iter.hasNext()) {
    		sbM.append(iter.next() + "\r\n");
    	}
    	
//    	Preference.LOG += sbC.toString();
//    	Preference.LOG += sbM.toString();
//    	BufferedWriter bw = new BufferedWriter(new FileWriter(Preference.OUTPUT_LOG_PATH));
    	bw.write(sbC.toString());
    	bw.write(sbM.toString());
    	bw.close();
    	System.out.println("uncovered count: " + uncovered);
    	System.out.println("mutation count: " + mutation);
    }
    
    private static void test() throws IOException {
    	BufferedWriter bw = new BufferedWriter(new FileWriter(Preference.OUTPUT_LOG_PATH));
//    	FastaReader reader = new FastaReader("/data1/yjseo/data_spa/ds3/faa_collection/spa_ds3.cluster.faa");
    	FastaReader reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_1/NC_006814.read1.fgs.faa");
    	List<Sequence> seqList = null;
    	seqList = reader.read();
    	
    	int repeatCount = 0;
    	
//    	for(Sequence seq: seqList) {
//    		List<Sequence> s = new ArrayList<Sequence>();
//    		s.add(seq);
    		Graph graph =  GraphGenerator.generate(seqList, Preference.VERTEX_SIZE);
    		Iterator<Vertex> iter = graph.getVertexMap().values().iterator();
    		while(iter.hasNext()) {
    			Vertex v = iter.next();
    			bw.write(v.getEdgeList().size() + "\r\n");
//    			if(iter.next().getCoverage() > 1) {
//    				repeatCount++;
//    				break;
//    			}
    		}
//    	}
    	bw.close();
    	System.out.println("repeat count = " + repeatCount);
    }
    
    private static void job_main() {
    	
    	try {
//    		System.out.println("<< Read Generation >>");
//    		Preference.LOG += "<< Read Generation >>\r\n";
//    		List<Sequence> readList = generateRead();
//    		System.out.println();
    		
//    		FastaReader reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/spa_wgsim_read1_ds3.fgs.faa");
    		FastaReader reader = new FastaReader(Preference.INPUT_READ_PATH);
    		List<Sequence> readList = reader.read();
    		
    		System.out.println("<< Assembly >>");
    		Preference.LOG += "<< Assembly >>\r\n";
        	assembleRead(readList);
        	System.out.println();
        	
        	runCDHIT();
        	
        	System.out.println("<< Blast >>");
        	Preference.LOG += "<< Blast >>\r\n";
        	searchBlastWithDB();
        	System.out.println();
        	
        	System.out.println("<< Analysis >>");
        	Preference.LOG += "<< Analysis >>\r\n";
        	analyze();
        	
        	BufferedWriter bw = new BufferedWriter(new FileWriter(Preference.OUTPUT_LOG_PATH));
        	bw.write(Preference.LOG);
        	bw.close();
        	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
    }
    
    private static List<Sequence> generateRead() throws IOException {
    	
    	List<Sequence> readList = new ArrayList<Sequence>();
    	FastaReader reader = null;
    	ReadGenerator readGen = null;
    	
    	// genus 7
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_006814.faa");
//    	readGen = new ReadGenerator(reader.read(), 50);
    	readList = reader.read();
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_014370.faa");
//    	readGen = new ReadGenerator(reader.read(), 60);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_006085.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_004116.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_002967.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_013520.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_003454.faa");
//    	readGen = new ReadGenerator(reader.read(), 90);
    	readList.addAll(reader.read());
    	
    	// lacto 10
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_006814.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 50);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_008497.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 60);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_008498.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_008499.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_008502.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_008526.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_010999.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 90);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_011352.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_014334.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_010610.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 90);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_008530.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_013198.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_013199.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_013200.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_006529.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 90);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_006530.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_007929.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(reader.read());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_007930.read1.fgs.faa");
//    	readGen = new ReadGenerator(reader.read(), 90);
    	readList.addAll(reader.read());
    	
    	// ds3
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_006814.faa");
//    	readGen = new ReadGenerator(reader.read(), 50);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_008497.faa");
//    	readGen = new ReadGenerator(reader.read(), 60);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_008498.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_008499.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_008502.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_008526.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_010999.faa");
//    	readGen = new ReadGenerator(reader.read(), 90);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_011352.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_014334.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_010610.faa");
//    	readGen = new ReadGenerator(reader.read(), 90);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_008530.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_013198.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_013199.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_013200.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_006529.faa");
//    	readGen = new ReadGenerator(reader.read(), 90);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_006530.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_007929.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_007930.faa");
//    	readGen = new ReadGenerator(reader.read(), 90);
    	readList.addAll(readGen.generateRead());
    	
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_002967.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_003454.faa");
//    	readGen = new ReadGenerator(reader.read(), 90);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_004116.faa");
//    	readGen = new ReadGenerator(reader.read(), 10);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_004350.faa");
//    	readGen = new ReadGenerator(reader.read(), 60);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_006085.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_008024.faa");
//    	readGen = new ReadGenerator(reader.read(), 20);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_009009.faa");
//    	readGen = new ReadGenerator(reader.read(), 20);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_009332.faa");
//    	readGen = new ReadGenerator(reader.read(), 20);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_009785.faa");
//    	readGen = new ReadGenerator(reader.read(), 30);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_013520.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_013853.faa");
//    	readGen = new ReadGenerator(reader.read(), 50);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_014039.faa");
//    	readGen = new ReadGenerator(reader.read(), 40);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_014370.faa");
//    	readGen = new ReadGenerator(reader.read(), 60);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_014371.faa");
//    	readGen = new ReadGenerator(reader.read(), 60);
    	readList.addAll(readGen.generateRead());
    	reader = new FastaReader("/data1/yjseo/data_spa/ds3/fnn_collection/wgsim_0/NC_014498.faa");
//    	readGen = new ReadGenerator(reader.read(), 20);
    	readList.addAll(readGen.generateRead());
    	
    	return readList;
    }

    /**
     * Assemble by Simplified Graph Method
     */
    private static void assembleRead(List<Sequence> readList) {
//    	List<Sequence> readList = null;
//    	FastaReader reader = new FastaReader(Preference.INPUT_READ_PATH);
		try {
//			ReadGenerator readGen = new ReadGenerator(reader.read(), 30);
//			readList = readGen.generateRead();
			System.out.println("Read size:" + readList.size());
			Preference.LOG += "Read size:" + readList.size() + "\r\n";
			
			// simplified graph method
			Graph graph = GraphGenerator.generate(readList, Preference.VERTEX_SIZE);
			Assembly_SimpleGraphMethod assembly = new Assembly_SimpleGraphMethod(graph);
			assembly.makeGraph();
			List<Sequence> seqList = assembly.getAssembledSequences();
			System.out.println("Assembled sequence size:" + seqList.size());
			Preference.LOG += "Assembled sequence size:" + seqList.size() + "\r\n";
			FastaWriter bw = new FastaWriter(Preference.OUTPUT_ASB_PATH);
			for(Sequence seq: seqList) {
				bw.write(seq);
			}
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private static void runCDHIT() {
    	System.out.println("Proc 5 : Sequence Clustering");
    	long startTime = System.nanoTime();
    	String command = "/home/hjgwak/program/cdhit-master/cd-hit -i " + Preference.OUTPUT_ASB_PATH + " -o " + Preference.OUTPUT_ASB_CLUSTER_PATH + " -c " + 0.9 + " -M " + 5000;
    	String[] cmd = command.split(" ");
		try {
			Process p = new ProcessBuilder(cmd).start();
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.nanoTime();
    	long lTime = endTime - startTime;
    	System.out.println("P5 TIME : " + lTime/1000000.0 + " (ms)");
    }
   
    /**
     * Local Blast DB search
     */
    private static void searchBlastWithDB() {
		Blast blast = new Blast(Preference.OUTPUT_ASB_CLUSTER_PATH);
		blast.runBlast();
    }
  
    /**
     * Assembled sequence analysis 
     * @throws IOException 
     */
    private static void analyze() {
    	
		try {
			// assembled sequence
	    	FastaReader afr = new FastaReader(Preference.OUTPUT_ASB_CLUSTER_PATH);
	    	List<Sequence> assembleList;
			assembleList = afr.read();
			
			HashMap<String, Sequence> assembleMap = new HashMap<String, Sequence>();
	    	for(Sequence seq: assembleList)
	    		assembleMap.put(seq.getHeader(), seq);
	    	
	    	// reference sequence
	    	FastaReader rfr = new FastaReader(Preference.INPUT_BLASTDB_PATH);
	    	List<Sequence> referenceList = rfr.read();
	    	HashMap<String, Sequence> referenceMap = new HashMap<String, Sequence>();
	    	for(Sequence seq: referenceList)
	    		referenceMap.put(seq.getHeader(), seq);
	    	
	    	// blast result
	    	BlastReader br = new BlastReader(Preference.OUTPUT_BLAST_PATH);
	    	List<BlastResult> bList = br.readTable();
	    	
			// Analysis
	    	Analysis analysis = new Analysis(referenceMap, assembleMap, bList);
	    	analysis.analyze();
//	    	analysis.writeBlastResult();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    
//    @Deprecated
//    private static Graph makeGraph(List<Sequence> sequenceList) throws IOException {
//    	// make graph
//    	Graph graph = new Graph();
//    	int vertexLength = 12;
//    	int edgeLength = vertexLength + 1;
//    	
//    	for(Sequence seq: sequenceList) {
//    		String string = seq.getString();
//    		int sequenceLength = string.length();
//    		
//    		String v = string.substring(0, vertexLength);
//    		Vertex vertex = null;
//        	if(graph.existVertex(v)) {
//    			vertex = graph.getVertex(v);
//    			graph.addVertexCoverage(v);
//    		} else {
//    			vertex = new Vertex(v);
//    			graph.addVertex(v, vertex);
//    		}
//    		for(int i=0; i<sequenceLength-vertexLength; i++) {
//    			String v1 = string.substring(i, i+vertexLength);
//    			String v2 = string.substring(i+1, i+vertexLength+1);
//    			String ed = string.substring(i, i+edgeLength);
//    			
//    			Vertex vertex1 = null;
//    			Vertex vertex2 = null;
//    			
//    			if(graph.existVertex(v2)) {
//    				vertex2 = graph.getVertex(v2);
//    				graph.addVertexCoverage(v2);
//    			} else {
//    				vertex2 = new Vertex(v2);
//    				graph.addVertex(v2, vertex2);
//    			}
//    			
//    			vertex1 = graph.getVertex(v1);
//    			if(graph.existEdge(ed)) {
//    				graph.addEdgeCoverage(ed);
//    			} else {
//    				Edge edge = new Edge(vertex1, vertex2, ed);
//    				vertex1.addEdge(edge);
//    				vertex2.addEdge(edge);
//    				graph.addEdge(ed, edge);
//    			}
//    		}
//    	}
//    	graph.setFValue();
//    	
//    	return graph;
//    }
    
    @Deprecated
    private static void printVertexOrderedByCoverage(Graph graph) throws IOException {
//    	BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yjseo/temp/Streptococcus_pyogenes_A20_uid178106_edge.out"));
//    	BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yjseo/temp/Staphylococcus_aureus_04_02981_uid161969_edge.out"));
//    	BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yjseo/temp/Staphylococcus_Streptococcus_Clostridium_edge.out"));
//    	BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yjseo/temp/Clostridium_difficile_630_uid57679_edge.out"));
    	BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yjseo/temp/genus_vertex.out"));

    	List<Vertex> list = graph.getVerticeOrderedByCoverage();
    	for(Vertex vertex: list) {
    		bw.write(vertex.toString() + ", Edge=" + vertex.getEdgeList().size() + "\r\n");
//    		System.out.println(vertex.toString() + ", Edge=" + vertex.getEdgeList().size());
    	}
    	bw.close();
    	
//    	List<Edge> list = graph.getEdgeList();
//    	for(Edge edge: list) {
//    		bw.write(edge.getV1().getString() + "\t" + edge.getV2().getString() + "\t" + edge.getCoverage() + "\r\n");
////    		System.out.println(edge.getV1().getString() + "\t" + edge.getV2().getString() + "\t" + edge.getCoverage());
//    	}
    	bw.close();
    }
    
    @Deprecated
    private static void traverseGraph(Graph graph) throws IOException {
    	// find seed
//    	List<Vertex> seeds = graph.getSeedVertex();
//    	BufferedWriter bw = new BufferedWriter(new FileWriter("F:\\Dropbox\\DNA\\20160929_SPA\\data\\traverse_fgs_noindel.txt"));
//    	BufferedWriter bw = new BufferedWriter(new FileWriter("/data1/yjseo/dfs_Clostridium_difficile_630_uid57679.out"));
//    	FastaWriter bw = new FastaWriter("/home/yjseo/temp/NC_017340_ffn_single.bwa.read1.fasta.fgs.asb.faa");
    	FastaWriter bw = new FastaWriter("/home/yjseo/temp/NC_018936_ffn_single.bwa.read1.fasta.fgs.asb.faa");
    	
    	for(int i=0; i<10000; i++) {
        	// traverse right
    		Vertex seed = getSeed(graph);
//    		Vertex seed = seeds.get(i);
    		if(seed == null) break;
    		seed.visited = true;
    		if(graph.getVertex(seed.getString()) == null)
    			continue;
    		
    		Stack<Vertex> stack = new Stack<Vertex>();
    		stack.push(seed);
    		int count = i+1;
//    		bw.write("seed " + count + ": " + seed.toString() + "\r\n");
    		System.out.println("seed " + count + ": " + seed.toString());
    		Preference.LOG += "seed " + count + ": " + seed.toString() + "\r\n";
    		ArrayList<String> seqListPre = new ArrayList<String>();
    		graph.traverseV1(seed, new StringBuilder(seed.getString()), stack, seqListPre);
    		stack = new Stack<Vertex>();
    		stack.push(seed);
    		ArrayList<String> seqListPost = new ArrayList<String>();
    		graph.traverseV2(seed, new StringBuilder(seed.getString()), stack, seqListPost);
    		int countTree = 1;
    		for(String seq1: seqListPre) {
    			for(String seq2: seqListPost) {
    				String header = "seed:"+ seed.getString() + "_" + countTree;
    				String string = seq1 + seq2.substring(seed.getString().length());
    				Sequence seq = new Sequence(header, string);
    				countTree++;
    				bw.write(seq);
//    				bw.write(seq1 + seq2.substring(seed.getString().length()) + "\r\n");
//    				System.out.println(seq1 + seq2.substring(seed.getString().length()));
    			}
    		}
        	graph.removeVisitedGraph();
//        	System.out.println(i + "\t" + whole);
    	}
    	bw.close();
    }
    
    @Deprecated
    private static Vertex getSeed(Graph graph) {
    	Vertex seed = null;
    	Iterator<Vertex> iter = graph.getVertexMap().values().iterator();
    	while(seed == null && iter.hasNext()) {
//    	if(iter.hasNext()) {
    		try {
    			seed = iter.next();	
    		} catch(Exception e) {
    			System.out.println("null seed");
    		}	
    	}
    	return seed;
    }
    
    @Deprecated
    private static ArrayList<Sequence> makeTargetSequence() throws IOException {
    	File dir = new File("/data2/db/ncbi/bacteria_150414/all_faa");
    	HashSet<String> speciesName = new HashSet<String>();
    	ArrayList<Sequence> targetSeq = new ArrayList<Sequence>();
    	int count = 0;
    	for(File d: dir.listFiles()) {
    		String name = d.getName();
    		int idx = name.indexOf("_");
//    		idx = name.indexOf("_", idx+1);
    		if(idx < 0)
    			continue;
    		
    		String subName = name.substring(0, idx);
    		if(speciesName.contains(subName))
    			continue;

    		System.out.print(name + "\t");
    		speciesName.add(subName);
    		for(File dd: d.listFiles()) {
    			FastaReader reader = new FastaReader(dd.getAbsolutePath());
    			ArrayList<Sequence> temp = reader.read();
    			System.out.print(temp.size() + "\t");
    			targetSeq.addAll(temp);
    		}
    		count++;
    		System.out.println(subName + " " + count);
    		
//    		if(count == 1)
//    			break;
    	}
    	
    	return targetSeq;
    }
    
}
