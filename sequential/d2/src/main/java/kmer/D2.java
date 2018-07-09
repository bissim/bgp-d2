package kmer;

import java.util.ArrayDeque;
import java.util.Queue;


public class D2 {

    /**
     * All possible chars for representing a number as a String
     */
    final static char[] SIGMA = {
        'A' , 'C' , 'G' , 'T'
    };

	private int k;
	
	public D2(int k) {
		this.k = k;
	}
	
	/**
	 * Calcola la somiglianza delle stringhe secondo l'algoritmo D2
	 * @param S
	 * @param Q
	 * @return
	 */
	public Number score(String S, String Q){
		return null;
	}
	
	/**
	 * Conta il numero di occorrenze del kmero kmer all'interno della stringa X
	 * @param X
	 * @param Kmer
	 * @return
	 */
	private Number occurrance( String X, String Kmer ){
		return null;
	}
	
	/**
	 * Ottieni il k-mero in base al prossimo indice rankIndex, che definisce il rank 
	 * in orgine lessicografico del k-mero
	 * @param rankIndex
	 * @return
	 */
	public String nextKmer( int rankIndex ){
		String s = this.convert2(rankIndex);
		int remainder = this.k - s.length();
		
		for( int i = 0; i < remainder; i++ ){
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
	 * @param radix
	 * @return
	 */
    public String convert2(int i) {
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
