package generator;

import java.io.FileNotFoundException;

public class MainGenerator {
	public static void main(String[] args) {
		
		long starTime = System.currentTimeMillis();
		try {
			SequenceGenerator.datasetUnique("TestDataset1","",200000);
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
