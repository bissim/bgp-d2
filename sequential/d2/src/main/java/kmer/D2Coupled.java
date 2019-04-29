package kmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class D2Coupled {	

	/**
	 * The <samp>top</samp> instance variable represent the number of k-mer and
	 * occurrences couple to consider.
	 */
	private int top;

	/**
	 * The <samp>D2Coupled(int)</samp> constructor.
	 *
	 * @param top - Number of k-mer - occurrences couples to consider
	 * @see D2Coupled#top
	 */
	public D2Coupled(int top) {
		this.top = top;
	}

	/**
	 * The <samp>score(String[])</samp> method generates the similarity matrix of D2
	 * scores by taking a list (formally, an array of <samp>String</samp> objects)
	 * of occurrences files of k-mers into sequences as input and returns a <samp>
	 * List</samp> of <samp>String</samp> containing the scores in the format 
	 * <samp>&lt;nameoffirstsequence&gt;-&lt;nameofthesecondsequence&gt;:
	 * &lt;d2scoreoftwosequences&gt;.
	 *
	 * @param pathsOutputKMC - Path to occurrences files
	 * @return logger - The list of scores among sequences
	 * @throws IOException Cannot access occurrences files
	 */
	public List<String> score(String[] pathsOutputKMC) throws IOException {

//		BigInteger[][] matrixD2 = new BigInteger[pathsOutputKMC.length][pathsOutputKMC.length];
		ArrayList<String> logger = new ArrayList<>();

		File osFSNaming;

		for (int i = 0; i < pathsOutputKMC.length; i++) {
			for (int j = i + 1; j < pathsOutputKMC.length; j++) {

				BigInteger currScore = this.score(pathsOutputKMC[i], pathsOutputKMC[j]);

				osFSNaming = new File(pathsOutputKMC[i]);
				String nameS = osFSNaming.getName();

				osFSNaming = new File(pathsOutputKMC[j]);
				String nameQ = osFSNaming.getName();

				logger.add(nameS + "-" + nameQ + ": " + currScore);
			}
		}

		return logger;
	}

	/**
	 * The <samp>score(String, String)</samp> method calculates the D2 distance
	 * between two sequences <em>S</em> and <em>Q</em> by taking their occurrences
	 * files as input.
	 * 
	 * @param outputKMC_S The k-mer occurrences file for sequence <em>S</em>
	 * @param outputKMC_Q The k-mer occurrences file for sequence <em>Q</em>
	 * @return D2 distance score between sequences <em>S</em> and <em>Q</em>
	 * @throws IOException Cannot access occurrences files
	 */
	public BigInteger score(String outputKMC_S, String outputKMC_Q) throws IOException {
		BigInteger score = BigInteger.ZERO;

		Map<String, BigInteger> kmerMapS = this.loadKmersFromFile(outputKMC_S);
		Map<String, BigInteger> kmerMapQ = this.loadKmersFromFile(outputKMC_Q);

		for (Entry<String, BigInteger> e: kmerMapS.entrySet()) {
			String kmerInS = e.getKey();
			BigInteger occInS = e.getValue();

			BigInteger occInQ = kmerMapQ.get(kmerInS);

			if (occInQ != null) {
				score = score.add(occInS.multiply(occInQ));
//				System.out.printf("KMER::%s \t>> occS=%d -- occQ=%d -- molt=%d--> patrialScore:%d\n", kmerInS, occInS, occInQ, occInS.multiply(occInQ),score);
			}

		}

		return score;
	}

	/**
	 * The <samp>loadKmersFromFile(String)</samp> method reads an occurrences file
	 * and returns a map of <samp>&lang;k-mer, occurrences&rang;</samp> pairs.
	 *
	 * @param pathFile - path to occurrences file
	 * @return map of k-mers with associated occurrences
	 * @throws IOException Cannot access <samp>pathFile</samp> file
	 */
	public Map<String, BigInteger> loadKmersFromFile(String pathFile) throws IOException {

		HashMap<String, BigInteger> kmerMap = new HashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader(pathFile));

		String line;

		int topCounter = this.top;
		while ((line = reader.readLine()) != null) {

			if (topCounter == 0) {
//				System.out.println("!!!!!!!!!!JUMP to next NewLine!!!!!!!!!!!!");
				while (!reader.readLine().equals(""));
				topCounter = this.top;
				continue;
			}

			if (line.equals("")) {
				topCounter = this.top;
//				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>END");
			}
			else {
//				System.out.printf("TOP:%d --  LINE: |%s|\n", topCounter, line);

				String[] arr = line.split("\t");
				String kmer = arr[1];
				BigInteger occurrance = new BigInteger(arr[2]);

				kmerMap.put(kmer, occurrance);
			}

			topCounter--;
		}

		reader.close();

		return kmerMap;
	}

}
