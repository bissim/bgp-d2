package bgp.d2distributed;

import org.apache.hadoop.io.Text;

/**
 * The <samp>KmerLine</samp> class realizes the abstraction of the line of a occurrences file with format
 * <samp>IdSeq\tKmer\tOccurrences</samp>, where <samp>IdSeq</samp> is the id of sequence, <samp>Kmer</samp> is the
 * k-mer and <samp>Occurrences</samp> is the number of occurrences of k-mer in sequence with id <samp>idSeq</samp>.
 * 
 */
public class KmerLine {

	private Text idSeq;
	private Text Kmer;
	private Long occurrances;

	/**
	 * Default empty constructor for Hadoop (collector).
	 */
	public KmerLine() {
		this.idSeq = new Text();
		this.Kmer = new Text();
		this.occurrances = new Long(0);
	}

	/**
	 * Parameterised constructor with first two arguments as <samp>String</samp> objects and the third one as
	 * <samp>Long</samp> objects.
	 *
	 * @param idSeq The ID of the sequence where <samp>kmer</samp> has been found
	 * @param kmer A k-mer
	 * @param occurrances The number of occurrences of <samp>kmer</samp> k-mer into the <samp>idSeq</samp> sequence
	 */
	public KmerLine(String idSeq, String kmer, Long occurrances) {
		this.idSeq = new Text(idSeq);
		this.Kmer = new Text(kmer);
		this.occurrances = occurrances;
	}

	/**
	 * Parameterised constructor with first two arguments as <samp>Text</samp> objects and the third one as
	 * <samp>Long</samp> objects.
	 *
	 * @param idSeq The ID of the sequence where <samp>kmer</samp> has been found
	 * @param kmer A k-mer
	 * @param occurrances The number of occurrences of <samp>kmer</samp> k-mer into the <samp>idSeq</samp> sequence
	 */
	public KmerLine(Text idSeq, Text kmer, Long occurrances) {
		this.idSeq = idSeq;
		this.Kmer = kmer;
		this.occurrances = occurrances;
	}

	/**
	 * Gets the ID of the sequence.
	 *
	 * @return the ID of the sequence
	 */
	public Text getIdSeq() {
		return idSeq;
	}

	/**
	 * Sets the sequence ID in the line.
	 *
	 * @param idSeq The new sequence ID as {@link String}
	 */
	public void setIdSeq(String idSeq) {
		this.idSeq = new Text(idSeq);
	}

	/**
	 * Sets the sequence ID in the line.
	 *
	 * @param idSeq The sequence ID as {@link Text}
	 */
	public void setIdSeq(Text idSeq) {
		this.idSeq = idSeq;
	}

	/**
	 * Gets the k-mer of the sequence.
	 *
	 * @return the k-mer of the sequence
	 */
	public Text getKmer() {
		return Kmer;
	}

	/**
	 * Sets the k-mer of the line.
	 *
	 * @param kmer The k-mer as {@link String}
	 */
	public void setKmer(String kmer) {
		Kmer = new Text(kmer);
	}

	/**
	 * Sets the k-mer of the line.
	 *
	 * @param kmer The k-mer as {@link Text}
	 */
	public void setKmer(Text kmer) {
		Kmer = kmer;
	}

	/**
	 * Gets the number of occurrences of k-mer into the sequence.
	 *
	 * @return the number of occurrences of k-mer into the sequence
	 */
	public Long getOccurrances() {
		return occurrances;
	}

	/**
	 * Sets the number of occurrences of k-mer into the sequence.
	 *
	 * @param occurrances The number of occurrences into the sequence
	 */
	public void setOccurrances(Long occurrances) {
		this.occurrances = occurrances;
	}

	/**
	 * <samp>toString()</samp> method for <samp>KmerLine</samp> class.
	 *
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "KmerLine [idSeq=" + idSeq + ", Kmer=" + Kmer + ", occurrances=" + occurrances + "]";
	}

}
