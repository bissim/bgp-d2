package test;

import java.io.IOException;
import java.math.BigInteger;

import kmer.D2;

/**
 * The <samp>TestD2</samp> class tests the <samp>D2</samp> class.
 */
public class TestD2 {

	/**
	 * The <samp>main(String[])</samp> method tests the <samp>D2</samp> class by
	 * loading a couple of k-mer occurrences files and calculating their D2
	 * distance score.<br />
	 * It prints out elapsed time for calculation.
	 *
	 * @param args - Arguments to the command line
	 */
	public static void main(String[] args) {

		D2 d2 = new D2(4);
		long starTime = System.currentTimeMillis();
		String Q = "C:\\Users\\Mary\\Desktop\\KMC3.1.0.windows\\dataset1_k4.res";
		String S = "C:\\Users\\Mary\\Desktop\\KMC3.1.0.windows\\dataset2_k4.res";
		BigInteger score = null;
		try {
			score = d2.score(S, Q);
		} catch (IOException e) {
			System.err.println(
					"Error " +
					e.getClass().getSimpleName() +
					": " +
					e.getLocalizedMessage()
			);
		}
		long endTime = System.currentTimeMillis();

		double totTime = (double) (endTime - starTime);

		if (totTime <= 60.0) {
			System.out.println("Total time: " + totTime/1000 + " sec.");
		} else {
			System.out.println("Total time: " + totTime/60000 + " min.");
		}

		System.out.println("Similarity score between Q and S is " + score);
	}

}
