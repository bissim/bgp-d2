package generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Random;

public class SequenceGenerator {

	public static final char[] SIGMA = {'A', 'C', 'G', 'T'};
	
	public static int MIN_LENGTH_SEQ = 50;
	public static int MAX_LENGTH_SEQ = 200;
	
	public static Random RNG = new Random();
	
	
	/**
	 * Genera una sequenza di lunghezza length sul file out
	 * @param length
	 * @param out
	 */
	public static void sequenceGen(int length, PrintStream out){
		
		for( int i = 0; i < length; i++ ){
			int nextChoice = RNG.nextInt(SIGMA.length);
			char nextChar = SIGMA[nextChoice];
			
			out.print( nextChar );
		}
		
	}
	
	/**
	 * Genera un dataset composto da num_seq file differenti
	 * I file rappresentano sequenza avente lunghezza casuale comeprese tra [MIN_LENGHT_SEQ; MAX_LENGTH_SEQ]
	 * TODO aggiungere la prima riga di intestazione!!!!
	 * @param num_seq
	 * @throws FileNotFoundException
	 */
	public static void datasetGen(String out_dir, int num_seq) throws FileNotFoundException{
		
		for( int i = 0; i < num_seq; i++ ){
			
			int nextSeqLenght = MIN_LENGTH_SEQ + RNG.nextInt(MAX_LENGTH_SEQ - MIN_LENGTH_SEQ);
			
			PrintStream ps = new PrintStream( new File(out_dir + "seq_" + i + ".fasta") );
			sequenceGen(nextSeqLenght, ps);
			ps.close();
			
		}
	}
	
	/**
	 * Genera un dataset composto da num_seq file differenti
	 * I file rappresentano sequenza avente lunghezza casuale comeprese tra [MIN_LENGHT_SEQ; MAX_LENGTH_SEQ]
	 * @param num_seq
	 * @throws FileNotFoundException
	 */
	public static void datasetGenUnique(String namedataset, String out_dir, int num_seq) throws FileNotFoundException{
		
		PrintStream ps = new PrintStream( new File(out_dir + "dataset.fasta") );
		
		for( int i = 0; i < num_seq; i++ ){
			ps.printf("> %s_SEQ_%d\n", namedataset, i);
			int nextSeqLenght = MIN_LENGTH_SEQ + RNG.nextInt(MAX_LENGTH_SEQ - MIN_LENGTH_SEQ);
			sequenceGen(nextSeqLenght, ps);
			ps.println("");
		}
		
		ps.close();
	}
	
	
	
}
