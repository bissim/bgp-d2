package generator;

import java.io.FileNotFoundException;

/**
 * The <samp>MainGeneratorSingleFile</samp> generates a single file of random
 * k-mer - occurrences couples, in order to test the <strong>D2</strong> and the
 * <strong>D2D</strong> algorithms implementations.
 */
public class MainGeneratorSingleFile {

	/**
	 * The <samp>main(String[])</samp> method deals with generating a file of
	 * k-mer - occurrences couples.<br />
	 * It requires the following parameters:
	 * <ol>
	 *   <li>the minimum length of a sequence</li>
	 *   <li>the maximum length of a sequence</li>
	 *   <li>the number of sequence to generate</li>
	 *   <li>the name of the sequences database</li>
	 *   <li>the output directory for sequences, terminating with '//'</li>
	 *   <li>the name of the file that will contain all sequences</li>
	 * </ol>
	 *
	 * @param args - Arguments to the command line
	 */
	public static void main(String[] args) {

		if (args.length != 6) {
			System.out.println("You must specify <MIN_LENGTH_SEQ> <MAX_LENGTH_SEQ> <NUM_SEQ> <DATABASE_NAME> <OUTDIR> <FILE_NAME>");
			System.out.println("   MIN_LENGTH_SEQ: min lenght of a sequence");
			System.out.println("   MAX_LENGTH_SEQ: max lenght of a sequence");
			System.out.println("   NUM_SEQ: num of sequence to generate");
			System.out.println("   DATABASE_NAME: name of database of sequences");
			System.out.println("   OUTDIR: path where create the sequences, must terminate with '//'");
			System.out.println("   FILE_NAME: name of the file that will contain all the sequences");
			System.exit(-1);
		}

		int MIN_LENGTH_SEQ = Integer.valueOf(args[0]);
		int MAX_LENGTH_SEQ = Integer.valueOf(args[1]);
		int NUM_SEQ = Integer.valueOf(args[2]);
		String DATABASE_NAME = args[3];
		String OUTDIR = args[4];
		String FILE_NAME = args[5];

		SequenceGenerator.MIN_LENGTH_SEQ = MIN_LENGTH_SEQ;
		SequenceGenerator.MAX_LENGTH_SEQ = MAX_LENGTH_SEQ;

		long starTime = System.currentTimeMillis();
		try {
			SequenceGenerator.datasetUnique(DATABASE_NAME, FILE_NAME, OUTDIR, NUM_SEQ);
		} catch (FileNotFoundException e) {
			System.err.println("Error: " + e.getMessage() + ".");
		}
		long endTime = System.currentTimeMillis();

		double totTime = (double) (endTime - starTime);
		if (totTime <= 60000) {
			System.out.println("Total time: " + totTime/1000 + " sec.");
		} else {
			System.out.println("Total time: " + totTime/1000 + "sec (" + totTime/60000 + " min).");
		}
	}

}
