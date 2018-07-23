package test;

import java.io.IOException;
import java.math.BigInteger;

import kmer.D2;

public class TestD2 {

	public static void main(String[] args) {

		D2 d2 = new D2(4);
		long starTime = System.currentTimeMillis();
		String Q = "C:\\Users\\Mary\\Desktop\\KMC3.1.0.windows\\dataset1_k4.res";
		String S = "C:\\Users\\Mary\\Desktop\\KMC3.1.0.windows\\dataset2_k4.res";
		BigInteger score = null;
		try {
			score = d2.score(S, Q);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();

		double totTime = (double)(endTime - starTime);

		if (totTime <= 60.0) {
			System.out.println("Total time: " + totTime/1000 + " sec.");
		} else {
			System.out.println("Total time: " + totTime/60000 + " min.");
		}

		System.out.println("Similarity score between Q and S is " + score);
	}
}
