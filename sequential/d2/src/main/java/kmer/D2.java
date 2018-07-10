package kmer;

public class D2 {

    /**
     * Alfabeto SIGMA delle sequenze del DNA 
     */
    final static char[] SIGMA = {
        'A' , 'C' , 'G' , 'T'
    };

	private int k_len;
	
	
	public D2(int k_len) {
		this.k_len = k_len;
	}
	
	/**
	 * Calcola la somiglianza delle stringhe secondo l'algoritmo D2
	 * @param S sequenza genomica
	 * @param Q sequenza genomica
	 * @return score
	 */
	public long score(String S, String Q){
		long score=0,Si=0,Qi=0;
		String kmer="";
		// D2(S,Q) = Σ(si - qi)^2, i=1 to n^k
		for(int i = 0; i<k_len; i++) {
			for(int j = 0; j<Math.pow(4, i); j++) {
				kmer = nextKmer(i,j);
				Si = occurrance(S, kmer);
				Qi = occurrance(Q, kmer);
				score += Si*Qi;
			}
		}
		return score;
	}
	
	/**
	 * Calcola la somiglianza delle stringhe secondo l'algoritmo D2 normalizzato
	 * @param A sequenza genomica
	 * @param B sequenza genomica
	 * @return scoreNorm
	 */
	public double scoreNormalized(String A,String B){
		double scoreNorm=0;
		int S=0;
		double radical2 = Math.sqrt(2);
		long sigmaAA = score(A,A);
		long sigmaBB = score(B,B);
		double distanceEucl = Math.sqrt(sigmaAA*sigmaAA+sigmaBB*sigmaBB);
		// D2 = −ln(S/(√2 * Σ(A,A) * Σ(B,B)/√((Σ(A,A))2 + (Σ(B,B))2))).
		scoreNorm = - Math.log(S/(radical2*sigmaAA*sigmaBB/distanceEucl));
		return scoreNorm;
	}
	
	/**
	 * Conta il numero di occorrenze del kmero kmer all'interno della stringa X
	 * @param x sequenza genomica
	 * @param kmer k-mero da ricercare
	 * @return occurrance occorrenza del kmer
	 */
	private long occurrance(String x, String kmer){
		long result = 0;
		int i = 0;
        while ((i = x.indexOf(kmer, i)) != -1) {
            i += kmer.length();
            result++;
        }		
		return result;		
	}
	
	/**
	 * Ottieni il k-mero in base al prossimo indice rankIndex, che definisce il rank 
	 * in orgine lessicografico del k-mero
	 * @param klength lunghezza k-mero
	 * @param rankIndex rank lessicografico del k-mero
	 * @return nextKmer prossimo k-mero
	 */
	private String nextKmer(int klength,int rankIndex){
		String s = this.convert(rankIndex);
		int remainder = klength - s.length();	
		for(int i = 0; i < remainder; i++){
			s = "A".concat(s);
		}
		return s;
	}

	/**
	 * Ottieni il k-mero in base al prossimo indice rankIndex, che definisce il rank 
	 * in orgine lessicografico del k-mero.
	 * Quindi si converte l'indice lessicografico decimale in base-4 per poi codificarlo nell'alfabeto sigma {A,C,G,T}
	 * L'implentazione è basato su toString(x, radix) di Integer.
	 * @param i
	 * @return string convert 
	 */
    private String convert(int i) {
    	int radix = 4;
        char buf[] = new char[33];
        boolean negative = (i < 0);
        int charPos = 32;
        if (!negative) {
            i = -i;
        }
        while (i <= -radix) {
            buf[charPos--] = SIGMA[-(i % radix)];
            i = i / radix;
        }
        buf[charPos] = SIGMA[-i];
        if (negative) {
            buf[--charPos] = '-';
        }
        return new String(buf, charPos, (33 - charPos));
    }
	
	
}
