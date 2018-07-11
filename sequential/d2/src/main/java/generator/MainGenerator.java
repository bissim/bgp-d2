package generator;

import java.io.FileNotFoundException;

public class MainGenerator {
	public static void main(String[] args) {
		
		SequenceGenerator.MIN_LENGTH_SEQ = 25000;
		SequenceGenerator.MAX_LENGTH_SEQ = 50000;
		
		long starTime = System.currentTimeMillis();
		try {
			SequenceGenerator.dataset("TestDataset1","single_fasta/",10);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		double totTime = (double)(endTime - starTime);
		if(totTime<=60000)
			System.out.println("Total time: "+totTime/1000+ " sec");
		else
			System.out.println("Total time: "+totTime/1000+"sec ("+totTime/60000+" min"+")");
	}
}
