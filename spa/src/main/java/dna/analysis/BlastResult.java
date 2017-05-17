package dna.analysis;

public class BlastResult {

	private String queryID;
	private String ReferenceID;
	private double identity;
	private int lengthOfAlignment;
	private int numMismatch;
	private int numGapopen;
	private int startQuery;
	private int endQuery;
	private int startReference;
	private int endReference;
	private double evalue;
	private double bitscore;
	private double lengthQuery;
	private double lengthReference;
	private double coverageQuery;
	private double coverageReference;
	private String matchedString;
	
	public BlastResult() {
		
	}

	public String getQueryID() {
		return queryID;
	}

	public void setQueryID(String queryID) {
		this.queryID = queryID;
	}

	public String getReferenceID() {
		return ReferenceID;
	}

	public void setReferenceID(String referenceID) {
		ReferenceID = referenceID;
	}

	public double getIdentity() {
		return identity;
	}

	public void setIdentity(double identity) {
		this.identity = identity;
	}

	public int getLengthOfAlignment() {
		return lengthOfAlignment;
	}

	public void setLengthOfAlignment(int lengthOfAlignment) {
		this.lengthOfAlignment = lengthOfAlignment;
	}

	public int getNumMismatch() {
		return numMismatch;
	}

	public void setNumMismatch(int numMismatch) {
		this.numMismatch = numMismatch;
	}

	public int getNumGapopen() {
		return numGapopen;
	}

	public void setNumGapopen(int numGapopen) {
		this.numGapopen = numGapopen;
	}

	public int getStartQuery() {
		return startQuery;
	}

	public void setStartQuery(int startQuery) {
		this.startQuery = startQuery;
	}

	public int getEndQuery() {
		return endQuery;
	}

	public void setEndQuery(int endQuery) {
		this.endQuery = endQuery;
	}

	public int getStartReference() {
		return startReference;
	}

	public void setStartReference(int startReference) {
		this.startReference = startReference;
	}

	public int getEndReference() {
		return endReference;
	}

	public void setEndReference(int endReference) {
		this.endReference = endReference;
	}

	public double getEvalue() {
		return evalue;
	}

	public void setEvalue(double evalue) {
		this.evalue = evalue;
	}

	public double getBitscore() {
		return bitscore;
	}

	public void setBitscore(double bitscore) {
		this.bitscore = bitscore;
	}

	public String getMatchedString() {
		return matchedString;
	}

	public double getLengthQuery() {
		return lengthQuery;
	}

	public void setLengthQuery(double lengthQuery) {
		this.lengthQuery = lengthQuery;
	}

	public double getLengthReference() {
		return lengthReference;
	}

	public void setLengthReference(double lengthReference) {
		this.lengthReference = lengthReference;
	}

	public double getCoverageQuery() {
		return coverageQuery;
	}

	public void setCoverageQuery(double coverageQuery) {
		this.coverageQuery = coverageQuery;
	}

	public double getCoverageReference() {
		return coverageReference;
	}

	public void setCoverageReference(double coverageReference) {
		this.coverageReference = coverageReference;
	}

	public void setMatchedString(String matchedString) {
		this.matchedString = matchedString;
	}
	
}
