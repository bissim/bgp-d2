package kmer;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

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
	private int kLen;

	/**
	 * The <samp>D2(int)</samp> constructor creates an object whose goal is
	 * to find out the similarity score between two genomic sequences.
	 *
	 * @param kLen - The length of k-mers to consider
	 */
	public D2(int kLen) {
		this.kLen = kLen;
	}

	/**
	 * Calculate similarity score between sequences <em>S</em> and
	 * <em>T</em> according to <strong>D2</strong> algorithm.
	 *
	 * @param outputKMC_S - The first genomic sequence
	 * @param outputKMC_Q - The second genomic sequence
	 * @return score - The similarity score between <samp>S</samp>
	 * and <samp>T</samp>.
	 * @throws IOException Cannot access k-mer occurrences file
	 */
	public BigInteger score(String outputKMC_S, String outputKMC_Q) throws IOException {
		BigInteger score = BigInteger.ZERO;
		long Si = 0, Qi = 0;
		BigInteger partialScore = null;

		// D2(S,Q) = Σ(si - qi)^2, i=1 to n^k
		for (int i = 3; i <= kLen; i++) {
			for (int j = 0; j < Math.pow(4, i); j++) {
				Si = getNextOccurence(outputKMC_S,j);
				Qi = getNextOccurence(outputKMC_Q,j);
				partialScore = BigInteger.valueOf((long) Math.pow(Si*Qi,2));
				System.out.println("partialScore: "+partialScore);
				score = score.add(partialScore);
				System.out.println("j: " + j + " score: " + score);
			}
		}

		return score;
	}

	/**
	 * Retrieve the number of occurrences of the <em>i</em>-th k-mer.
	 *
	 * @param outputKMC - The k-mer occurrences file
	 * @param index - The index of k-mer occurrences to seek
	 * @return occurrance - Next k-mer occurrences value
	 * @throws IOException Occurrences file cannot be opened
	 */
	public int getNextOccurence(String outputKMC, int index) {
		String nextKmer = null;
		int occurrance = 0;
		String[] arr;
		try (Stream<String> lines = Files.lines(Paths.get(outputKMC))) {
			Optional<String> first = lines.skip(index).findFirst();
			if (first.isPresent()) {
				nextKmer = first.get();
				arr = nextKmer.split("\t");
				occurrance = Integer.valueOf(arr[1]);
//				System.out.println(index+1 + " index: "  + "nextKmer: "+ nextKmer);
			}		
		} catch (IOException e) {
//			e.printStackTrace();
			System.err.println(
					"Error " +
					e.getClass().getSimpleName() +
					": " +
					e.getLocalizedMessage()
			);
		} catch (NoSuchElementException e) {
			System.out.println("END FILE [getNextOccurence]");
		}

//		System.out.println(index+1 + " index: "  + "nextKmer: "+ nextKmer);
		return occurrance;	
	}

}
