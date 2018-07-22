package generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Random;

public class SequenceGenerator {

	private static final char[] SIGMA = {'A', 'C', 'G', 'T'};

	public static int MIN_LENGTH_SEQ = 50;
	public static int MAX_LENGTH_SEQ = 200;
	public static int LENGTH_LINE_SEQ = 100;

	public static Random RNG = new Random();

	/**
	 * Generate a sequence of <samp>length</samp> lenght
	 * into file <samp>out</samp>.
	 *
	 * @param length - The lenght of the sequence to generate
	 * @param out - The output file which sequence will be written into
	 */
	public static void sequence(int length, PrintStream out) {
		int counter = 0;
		for (int i = 0; i < length; i++) {
			if (counter == LENGTH_LINE_SEQ) {
				out.print('\n');
				counter = 0;
			}

			int nextChoice = RNG.nextInt(SIGMA.length);
			char nextChar = SIGMA[nextChoice];
			out.print(nextChar);
			counter++;
		}
	}

	/**
	 * Generate a dataset made by <samp>numSeq</samp> different files.<br />
	 * Those files represent sequences with random lenght spacing into
	 * <samp>[MIN_LENGTH_SEQ; MAX_LENGTH_SEQ]</samp> range.
	 *
	 * @param datasetName - The name of the dataset to generate
	 * @param outDir - The output file which sequences will be written into
	 * @param numSeq - The  number of sequences to generate
	 * @throws FileNotFoundException - Specified output file has not been found
	 */
	public static void dataset(String datasetName, String outDir, int numSeq) throws FileNotFoundException {

		PrintStream listFiles = new PrintStream(new File(outDir + datasetName + ".lst"));

		for (int i = 0; i < numSeq; i++ ) {
			String pathFile = "seq" + i + ".fasta";
			PrintStream printer = new PrintStream(new File(outDir + pathFile));
			listFiles.println(pathFile);
			printer.printf(">gnl|%s|%d %s_SEQ_%d\n", datasetName, i, datasetName, i);
			//ps.printf(">%sSEQ%d\n", namedataset, i);
			int nextSeqLenght = MIN_LENGTH_SEQ + RNG.nextInt(MAX_LENGTH_SEQ - MIN_LENGTH_SEQ);
			sequence(nextSeqLenght, printer);
			printer.close();
		}

		listFiles.close();
	}

	/**
	 * Generate a dataset made of <samp>numSeq</samp> different sequences
	 * into a single output file.<br />
	 * Those files represent sequences with random lenght spacing into
	 * <samp>[MIN_LENGTH_SEQ; MAX_LENGTH_SEQ]</samp> range.
	 *
	 * @param datasetName - The name of the dataset to generate
	 * @param nameFile - The name of file that contains all sequences
	 * @param outDir - The output file which sequences will be written into
	 * @param numSeq - The  number of sequences to generate
	 * @throws FileNotFoundException - Specified output file has not been found
	 */
	public static void datasetUnique(String datasetName, String nameFile, String outDir, int numSeq) throws FileNotFoundException {

		PrintStream printer = new PrintStream(new File(outDir + nameFile));

		for (int i = 0; i < numSeq; i++) {
			printer.printf(">gnl|%s|%d %s_SEQ_%d\n", datasetName, i, datasetName, i);
			int nextSeqLenght = MIN_LENGTH_SEQ + RNG.nextInt(MAX_LENGTH_SEQ - MIN_LENGTH_SEQ);
			SequenceGenerator.sequence(nextSeqLenght, printer);
			printer.println("\n");
		}

		printer.close();
	}
}
