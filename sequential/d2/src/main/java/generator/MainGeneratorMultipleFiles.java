package generator;

import java.io.FileNotFoundException;

public class MainGeneratorMultipleFiles {
	public static void main(String[] args) {

		if( args.length < 5 ) {
			System.out.println("You must specify <MIN_LENGTH_SEQ> <MAX_LENGTH_SEQ> <NUM_SEQ> <DATABASE_NAME> <OUTDIR> [<BUFSIZE>]");
			System.out.println("   MIN_LENGTH_SEQ: max lenght of a sequence");
			System.out.println("   MAX_LENGTH_SEQ: min lenght of a sequence");
			System.out.println("   NUM_SEQ: num of sequence to generate");
			System.out.println("   DATABASE_NAME: name of database of sequences");
			System.out.println("   OUTDIR: path where create the sequences, must terminate with '//'");
			System.out.println("   BUFSIZE: optional, buffer size for writing process in MB");
			System.exit(-1);
		}
		

		
		int MIN_LENGTH_SEQ = Integer.valueOf(args[0]);
		int MAX_LENGTH_SEQ = Integer.valueOf(args[1]);
		int NUM_SEQ = Integer.valueOf(args[2]);
		String DATABASE_NAME = args[3];
		String OUTDIR = args[4];
		
		if( args.length == 6 ) {
			try {
				SequenceGenerator.BUF_SIZE = Integer.valueOf(args[5]) * 1024 * 1024;
			}
			catch (NumberFormatException e) {
				System.out.println("You must specify a integer number for BUFSIZE");
				System.exit(-1);
			}
		}
		
		SequenceGenerator.MIN_LENGTH_SEQ = MIN_LENGTH_SEQ;
		SequenceGenerator.MAX_LENGTH_SEQ = MAX_LENGTH_SEQ;

		long starTime = System.currentTimeMillis();
		try {
			SequenceGenerator.dataset(DATABASE_NAME, OUTDIR, NUM_SEQ);
		} catch (FileNotFoundException e) {
			System.err.println("Error: " + e.getMessage() + ".");
		}
		long endTime = System.currentTimeMillis();

		double totTime = (double)(endTime - starTime);
		if (totTime <= 60000) {
			System.out.println("Total time: " + totTime/1000 + " sec.");
		} else {
			System.out.println("Total time: " + totTime/1000 + "sec (" + totTime/60000 + " min).");
		}
	}
}
