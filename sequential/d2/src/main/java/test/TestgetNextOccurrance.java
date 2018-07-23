package test;

import java.io.IOException;
import java.util.NoSuchElementException;

import kmer.D2;

public class TestgetNextOccurrance {
	public static void main(String[] args){
		D2 d2 = new D2(10);
		String file = "C:\\Users\\Mary\\Desktop\\KMC3.1.0.windows\\result1.res";
		int index = 0;
		int occurance=0;
		while(true) {
			try {
				occurance=d2.getNextOccurence(file, index);
			//	System.out.println(index+1 + ": " + occurance);
				index++;
			} catch (NoSuchElementException e) {
				e.printStackTrace();
				System.out.println("END FILE!!!");
				break;
			}
			
		}
	}
}
