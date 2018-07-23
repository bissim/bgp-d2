package test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

import kmer.D2Coupled;

public class TestD2Coupled {

	public static void main(String[] args) throws IOException {

		String SEQ0 = "/home/sergio/UniSpec/corsi/SOA/tmp_kmc/occ/seq0_all.res";
		String SEQ1 = "/home/sergio/UniSpec/corsi/SOA/tmp_kmc/occ/seq1_all.res";
		
		D2Coupled d2c = new D2Coupled(1000);
		
		/*
		Map<String, BigInteger> kmerMap = d2c.loadKmersFromFile(SEQ0);
		
		System.out.println("HashMap di Occorrenze:");
		System.out.println(kmerMap);
		
		BigInteger result = d2c.score(SEQ0, SEQ1);
		
		System.out.println("D2_score(SEQ0,SEQ1) = " + result);
		
		
		result = d2c.score(SEQ1, SEQ0);
		
		System.out.println("D2_score(SEQ1,SEQ0) = " + result);
		*/
		
		
		
		String[] listSequences = {
				"/home/sergio/UniSpec/corsi/SOA/tmp_kmc/occ/seq0_all.res",
				"/home/sergio/UniSpec/corsi/SOA/tmp_kmc/occ/seq1_all.res",
				"/home/sergio/UniSpec/corsi/SOA/tmp_kmc/occ/seq2_all.res",
				"/home/sergio/UniSpec/corsi/SOA/tmp_kmc/occ/seq3_all.res",
				"/home/sergio/UniSpec/corsi/SOA/tmp_kmc/occ/seq4_all.res",
				/*
				"/home/sergio/UniSpec/corsi/SOA/tmp_kmc/occ/seq5_all.res",
				"/home/sergio/UniSpec/corsi/SOA/tmp_kmc/occ/seq6_all.res",
				"/home/sergio/UniSpec/corsi/SOA/tmp_kmc/occ/seq7_all.res",
				"/home/sergio/UniSpec/corsi/SOA/tmp_kmc/occ/seq8_all.res",
				"/home/sergio/UniSpec/corsi/SOA/tmp_kmc/occ/seq9_all.res" */
		};
		
		/*
		BigInteger[][] matrixD2 = d2c.score(listSequences);
		printMatrixD2(matrixD2);
		*/
		
		for( int i = 0; i < listSequences.length; i++ ){
			String pathS = listSequences[i];
			for( int j = i+1; j < listSequences.length; j++ ){
				String pathQ = listSequences[j];
				
				BigInteger d2res = d2c.score(pathS, pathQ);
				
				System.out.printf("%s-%s :: %d\n", pathS, pathQ, d2res);
				
			}
		}
		
		
	}
	
	private static void printMatrixD2( BigInteger[][] matrixD2 ){
		for( int i = 0; i < matrixD2.length; i++ ){
			for( int j = 0; j < matrixD2.length; j++ ){
				System.out.printf("%d\t", matrixD2[i][j]);
			}
			System.out.println("");
		}
	}

}
