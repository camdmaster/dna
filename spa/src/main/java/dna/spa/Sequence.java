package dna.spa;

public class Sequence {
	
	private String header;
	private String string;
	
	public Sequence(String header, String string) {
		this.header = header;
		this.string = string;
	}
	
	public Sequence(String string) {
		this.header = "unknown";
		this.string = string;
	}

	public String getHeader() {
		return header;
	}

	public String getString() {
		return string;
	}

	@Override
	public String toString() {
		return "Sequence [header=" + header + ", length=" + string.length() + "]";
	}

}
