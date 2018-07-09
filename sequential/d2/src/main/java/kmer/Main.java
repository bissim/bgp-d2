package kmer;

public class Main {

	public static void main(String[] args) {

		D2 d2 = new D2(2);	
		String Q="ATCGGCTGACCCGT";
		String S="CTGAATGCATGGCA";
		long score = d2.score(S, Q);
		System.out.println("Score di Q e S = "+score);
		
	}

}
