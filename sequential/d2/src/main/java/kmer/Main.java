package kmer;

public class Main {

	public static void main(String[] args) {

		D2 d2 = new D2(15);	
		long starTime = System.currentTimeMillis();
		String Q="ATCGGCTGACCCGTA";
		//String S="ATCGGCTGACCCGTA";
		String S="GCTGAATGCATGGCA";
		long score = d2.score(S, Q);
		long endTime = System.currentTimeMillis();
		double totTime = (double)(endTime - starTime)/1000;
		System.out.println("Score di Q e S = "+score);
		System.out.println("Tempo totale: "+totTime+ " sec");
		
	}

}
