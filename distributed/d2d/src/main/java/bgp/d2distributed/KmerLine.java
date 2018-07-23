package bgp.d2distributed;

import org.apache.hadoop.io.Text;

public class KmerLine {

	private Text idSeq;
	private Text Kmer;
	private Long occurrances;
	
	/**
	 * Costruttore di default per Hadoop (collector)
	 */
	public KmerLine() {
		this.idSeq = new Text();
		this.Kmer = new Text();
		this.occurrances = new Long(0);
	}
	
	public KmerLine(String idSeq, String kmer, Long occurrances) {
		this.idSeq = new Text(idSeq);
		this.Kmer = new Text(kmer);
		this.occurrances = occurrances;
	}
	
	public KmerLine(Text idSeq, Text kmer, Long occurrances) {
		this.idSeq = idSeq;
		this.Kmer = kmer;
		this.occurrances = occurrances;
	}
	
	
	public Text getIdSeq() {
		return idSeq;
	}
	public void setIdSeq(String idSeq) {
		this.idSeq = new Text(idSeq);
	}
	public void setIdSeq(Text idSeq) {
		this.idSeq = idSeq;
	}
	public Text getKmer() {
		return Kmer;
	}
	public void setKmer(Text kmer) {
		Kmer = kmer;
	}
	public void setKmer(String kmer) {
		Kmer = new Text(kmer);
	}
	public Long getOccurrances() {
		return occurrances;
	}
	public void setOccurrances(Long occurrances) {
		this.occurrances = occurrances;
	}

	
	@Override
	public String toString() {
		return "KmerLine [idSeq=" + idSeq + ", Kmer=" + Kmer + ", occurrances=" + occurrances + "]";
	}
	
}
