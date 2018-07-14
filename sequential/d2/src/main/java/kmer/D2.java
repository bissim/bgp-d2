package kmer;

public class D2 {

	/**
	 * <samp>SIGMA</samp> (Σ) is the alphabet of nucleotides
	 * whose compositions create DNA sequences.
	 */
	final static char[] SIGMA = {
		'A' , 'C' , 'G' , 'T'
	};

	/**
	 * <samp>k_len</samp> is the length of k-mers to consider.
	 */
	private int k_len;

	/**
	 * This constructor creates an object whose goal is
	 * to find out the similarity score between two
	 * genomic sequences.
	 *
	 * @param k_len - The length of k-mers to consider.
	 */
	public D2(int k_len) {
		this.k_len = k_len;
	}

	/**
	 * Calculate similarity between sequences <samp>S</samp> and <samp>T</samp>
	 * according to <strong>D2</strong> algorithm.
	 *
	 * @param S - The first genomic sequence
	 * @param Q - The second genomic sequence
	 * @return score - The similarity score between <samp>S</samp> and <samp>T</samp>.
	 */
	public long score(String S, String Q) {
		long score = 0, Si = 0, Qi = 0;
		String kmer = "";
		// D2(S,Q) = Σ(si - qi)^2, i=1 to n^k
		for (int i = 0; i < k_len; i++) {
			for (int j = 0; j < Math.pow(4, i); j++) {
				kmer = nextKmer(i, j);
				Si = occurrance(S, kmer);
				Qi = occurrance(Q, kmer);
				score += Si * Qi;
			}
		}
		return score;
	}

	/**
	 * Calculate similarity between sequences <samp>A</samp> and <samp>B</samp>
	 * according to <strong>D2</strong> algorithm and return a normalized score.
	 *
	 * @param A - The first genomic sequence
	 * @param B - The second genomic sequence
	 * @return scoreNorm - The normalized similarity score between <samp>A</samp> and <samp>B</samp>.
	 */
	public double scoreNormalized(String A, String B){
		double scoreNorm = 0;
		int S = 0;
		double radical2 = Math.sqrt(2);
		long sigmaAA = score(A, A);
		long sigmaBB = score(B,B);
		double distanceEucl = Math.sqrt(sigmaAA * sigmaAA + sigmaBB * sigmaBB);

		// D2 = −ln(S/(√2 * Σ(A,A) * Σ(B,B)/√((Σ(A,A))2 + (Σ(B,B))2))).
		scoreNorm = - Math.log(S / (radical2 * sigmaAA * sigmaBB / distanceEucl));

		return scoreNorm;
	}

	/**
	 * Count how many times the k-mer <samp>kmer</samp>
	 * occurs within the string <samp>X</samp>.
	 *
	 * @param X - The string which search k-mers into
	 * @param kmer - The k-mer to search within <samp>X</samp>
	 * @return occurrance - k-mer occurrences
	 */
	private long occurrance(String X, String kmer) {
		long result = 0;
		int i = 0;

		while ((i = X.indexOf(kmer, i)) != -1) {
			i += kmer.length();
			result++;
		}

		return result;
	}

	/**
	 * Get a k-mer according to next <samp>rankIndex</samp> index
	 * defining its position in Σ<sup>k</sup> according to
	 * lexicographic order.
	 *
	 * @param klength - The length of the k-mer
	 * @param rankIndex - The k-mer rank according to lexicographic order
	 * @return nextKmer - The next k-mer
	 */
	private String nextKmer(int klength, int rankIndex) {
		String s = this.convert(rankIndex);
		int remainder = klength - s.length();

		// add leading As to imcomplete k-mers
		for (int i = 0; i < remainder; i++) {
			s = "A".concat(s);
		}

		return s;
	}

	/**
	 * Get a k-mer according to its lexicographic rank.<br />
	 * Rank <samp>i</samp> is converted into a base 4 value that
	 * gets codificated into a value from <strong>Σ = {A, C, G,
	 * T}</strong>
	 * alphabet.<br >
	 * This implementation is based on {@link Integer#toString(int, int)} method.
	 *
	 * @param i - The rank of a k-mer
	 * @return The k-mer associated to given rank <samp>i</samp>
	 * @see Integer#toString(int, int)
	 */
	private String convert(int i) {
		int radix = 4;
		char buf[] = new char[33];
		boolean negative = (i < 0);
		int charPos = 32;

		if (!negative) {
			i = -i;
		}

		while (i <= -radix) {
			buf[charPos--] = SIGMA[-(i % radix)];
			i = i / radix;
		}

		buf[charPos] = SIGMA[-i];
		if (negative) {
			buf[--charPos] = '-';
		}

		return new String(buf, charPos, (33 - charPos));
	}
}
