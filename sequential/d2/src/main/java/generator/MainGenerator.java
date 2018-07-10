package generator;

import java.io.FileNotFoundException;

public class MainGenerator {
	public static void main(String[] args) {
		
		try {
			SequenceGenerator.datasetUnique("TestDataset1","",50000);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
