package benchmark;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kmer.D2Coupled;

/**
 * The <code>SequentialBenchmark</code> class performs benchmark for D2
 * sequential algorithm.
 * <br />
 * It takes as input a directory containing genomic sequences coded according
 * to FASTA format, the number of genomic sequences files to consider (oredered
 * according to lexycographic order) and the number of scores to consider
 * (in non-ascendent order).
 * <br />
 * It calculates the execution time of D2 algorithm over the specified number
 * of files and optionally prints the resulting scores.
 *
 */
public class SequentialBenchmark {

	/**
	 * The <code>main(String[])</code> method deals with running the D2
	 * algorithm by taking the following parameters on the command line:
	 * <ol>
	 *   <li>the directory containing the genomic sequences represented
	 *   as FASTA files;</li>
	 *   <li>the number of files to consider (in lexycographic order);</li>
	 *   <li>the number of resulting scores to consider;</li>
	 *   <li>the score printing flag.</li>
	 * </ol>
	 * <br />
	 * It prints a help line if a wrong number of parameters is specified.
	 * 
	 * @param args - Parameters to the command line
	 * @throws IOException Unable to access input directory
	 */
	public static void main(String[] args) throws IOException {

		if (args.length != 4) {
			System.out.println("You must specify <INPUT_DIR> <NUM_FILES> <TOP_LINES_OCCURANCES> <FLAG_ONLY_TIME>");
			System.out.println("   FLAG_ONLY_TIME: must be [yes,no] -- if yes, print also the result of d2 score on stdout");
			System.exit(-1);
		}

		String INPUT_DIR = args[0]; // "/Users/your/folder/"
		int NUM_FILES = Integer.valueOf(args[1]);
		int TOP_LINES_OCCURANCES = Integer.valueOf(args[2]);
		String FLAG_ONLY_TIME = args[3];

		File folder = new File(INPUT_DIR);
		File[] listOfFiles = folder.listFiles();
		List<String> listOfFileString = new ArrayList<>();

		int i = 0;
		for (File f: listOfFiles) {
			if (NUM_FILES != i) {
				if (f.isFile()) {
//					System.out.println("--> " + f.getAbsolutePath());
					listOfFileString.add(f.getAbsolutePath());
					i++;
				}
			}
		}

		long startTime, endTime, msTime;
		startTime = System.currentTimeMillis();

		D2Coupled d2c = new D2Coupled(TOP_LINES_OCCURANCES);

		List<String> results = d2c.score(listOfFileString.toArray(new String[listOfFileString.size()]));

		endTime = System.currentTimeMillis();
		msTime = endTime - startTime;

		System.out.println("TIME : " + msTime + "ms");

		if (FLAG_ONLY_TIME.equals("no")) {
			System.out.println("Results:");
			for (String e: results) {
				System.out.println(e);
			}
		}
	}

}
