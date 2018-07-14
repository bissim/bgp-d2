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
	public static void sequence(int length, PrintStream out){
		int counter = 0;
		for(int i = 0; i < length; i++){
			if(counter==LENGTH_LINE_SEQ) {
				out.print('\n');
				counter=0;
			}
			int nextChoice = RNG.nextInt(SIGMA.length);
			char nextChar = SIGMA[nextChoice];
			out.print(nextChar);
			counter++;
		}
	}

	/**
	 * Generate a dataset made by <samp>num_seq</samp> different files.<br />
	 * Those files represent sequences with random lenght spacing into
	 * <samp>[MIN_LENGTH_SEQ; MAX_LENGTH_SEQ]</samp> range.
	 *
	 * @param namedataset - The name of the dataset to generate
	 * @param out_dir - The output file wich sequences will be written into
	 * @param num_seq - The  number of sequences to generate
	 * @throws FileNotFoundException - Specified output file has not been found
	 */
	public static void dataset(String namedataset, String out_dir, int num_seq) throws FileNotFoundException{

		PrintStream listfiles = new PrintStream(new File(out_dir + namedataset + ".lst"));

		for( int i = 0; i < num_seq; i++ ){
			String path_file = "seq" + i + ".fasta";
			PrintStream ps = new PrintStream( new File(out_dir + path_file) );
			listfiles.println(path_file);
			ps.printf(">gnl|%s|%d %s_SEQ_%d\n", namedataset, i, namedataset, i);
			//ps.printf(">%sSEQ%d\n", namedataset, i);
			int nextSeqLenght = MIN_LENGTH_SEQ + RNG.nextInt(MAX_LENGTH_SEQ - MIN_LENGTH_SEQ);
			sequence(nextSeqLenght, ps);
			ps.close();
		}

		listfiles.close();
	}

	/**
	 * Generate a dataset made of <samp>num_seq</samp> different sequences
	 * into a single output file.<br />
	 * Those files represent sequences with random lenght spacing into
	 * <samp>[MIN_LENGTH_SEQ; MAX_LENGTH_SEQ]</samp> range.
	 *
	 * @param namedataset - The name of the dataset to generate
	 * @param out_dir - The output file wich sequences will be written into
	 * @param num_seq - The  number of sequences to generate
	 * @throws FileNotFoundException - Specified output file has not been found
	 */
	public static void datasetUnique(String namedataset, String out_dir, int num_seq) throws FileNotFoundException{

		PrintStream ps = new PrintStream( new File(out_dir + "dataset.fasta") );

		for( int i = 0; i < num_seq; i++ ){
			ps.printf(">gnl|%s|%d %s_SEQ_%d\n", namedataset, i, namedataset, i);
			int nextSeqLenght = MIN_LENGTH_SEQ + RNG.nextInt(MAX_LENGTH_SEQ - MIN_LENGTH_SEQ);
			sequence(nextSeqLenght, ps);
			ps.println("\n");
		}
		ps.close();
	}



}
