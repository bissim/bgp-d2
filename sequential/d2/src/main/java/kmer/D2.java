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
	 * @param S
	 * @param Q
	 * @return score
	 */
	public long score(String S, String Q){
		long score=0,Si=0,Qi=0;
		String kmer="";
		for(int i = 0; i<k_len; i++) {
			for(int j = 0; j<Math.pow(4, i); j++) {
				kmer = nextKmer(i,j);
				Si = occurrance(S, kmer);
				Qi = occurrance(Q, kmer);
				score += Math.pow((Si - Qi), 2);
			}
		}
		return score;
	}
	
	/**
	 * Conta il numero di occorrenze del kmero kmer all'interno della stringa X
	 * @param x
	 * @param kmer
	 * @return occurrance
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
	 * @param klength
	 * @param rankIndex
	 * @return nextKmer
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
