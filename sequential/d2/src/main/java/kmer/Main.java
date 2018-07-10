package kmer;

public class Main {

	public static void main(String[] args) {

		D2 d2 = new D2(12);	
		long starTime = System.currentTimeMillis();
		String Q="ACAAGATGCCAT";
		String S="GCTGAATGCATG";
		long score = d2.score(S, Q);
		long endTime = System.currentTimeMillis();
		double totTime = (double)(endTime - starTime);
		if(totTime<=60.0)
			System.out.println("Total time: "+totTime/1000+ " sec");
		else
			System.out.println("Total time: "+totTime/60+ " sec");
		System.out.println("Score di Q e S = "+score);
		
		
	}

}
