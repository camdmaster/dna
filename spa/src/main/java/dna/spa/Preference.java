package dna.spa;

public class Preference {

	public static int VERTEX_SIZE = 25;
	
	public static int CUTOFF_TERMINAL_VERTEX_SIZE = 20;
	public static int CUTOFF_SEQUENCE_SIZE = 60;
	public static int CUTOFF_BUBBLE_SIZE = 10;
	public static int CUTOFF_COVERAGE = 7;
	public static int READ_LENGTH = 50;
	
//	public static String BLAST_PLUS_PATH = "E:\\Program Files\\NCBI\\blast-2.6.0+\\bin\\";
//	public static String BLAST_PLUS_PATH = "/usr/local/ncbi/blast/bin/";
	public static String BLAST_PLUS_PATH = "/program/ncbi-blast-2.2.30+/bin/";
	
	public static String FRAGGENESCAN_PATH = "/program/FragGeneScan1.19/";
	
	public static String TEMP_PATH = "/data1/yjseo/temp/";
	
	public static String INPUT_DNA_READ_PATH;
	public static String INPUT_READ_PATH;
	public static String INPUT_BLASTDB_PATH;
	public static String OUTPUT_LOG_PATH;
	public static String OUTPUT_ASB_PATH;
	public static String OUTPUT_ASB_CLUSTER_PATH;
	public static String OUTPUT_BLAST_PATH;
	public static String OUTPUT_ANALYSIS_PATH;
	
	public static String LOG;
}
