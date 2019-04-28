package bgp.d2distributed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * The <samp>D2D</samp> class provide Mapper/Reducer implementation to the job computation of D2 Algorithm.
 * <ul>
 * <li><samp>{@link IndexerMapper}</samp> reads lines from the <samp>FileInputFormat</samp> splitter and produces
 * <samp>&lang;Kmer, line&rang;</samp> pairs</li>
 * <li><samp>{@link ScoreReducer}</samp>, via the MapReduce shuffle phase, reads <samp>&lang;Kmer, List[line]&rang;</samp>
 * pairs and creates an <samp>HashMap</samp> map with all the partial score and at the and of the task emits
 * <samp>&lang;LABEL(S,Q), pScore(S,Q)&rang;</samp> pairs</li>
 * </ul>
 *
 * <p><strong>Please note.</strong> If the number of reduce task is greater than 1, a sum phase with another job is necessary.
 * For these reason, another couple of Mapper/Reducer classes are implemented into {@link SumPhase} class.</p>
 *
 * @see IndexerMapper
 * @see ScoreReducer
 * @see SumPhase
 */
public class D2D {

	///////////////  PHASE 1  /////////////////
	/**
	 * The <samp>IndexerMapper</samp> class reads lines from <samp>FileInputFormat</samp> splitter and produce <samp>&lt;Kmer,
	 * line&gt;</samp> pairs.<br />
	 * Part of Phase 1.
	 *
	 */
	public static class IndexerMapper extends Mapper<Object, Text, Text, Text> {

		/**
		 * The <samp>map(Object, Text, Context)</samp> method takes as input a line of input file with the format
		 * <samp>IdSeq\tKmer\tOccurrences</samp>, where <samp>IdSeq</samp> is the id of sequence, <samp>Kmer</samp> is the
		 * k-mer and <samp>Occurrences</samp> is the number of occurrences of k-mer in sequence with id <samp>idSeq</samp>.
		 * <br />
		 * For each line read from input file, a <samp>&lang;Kmer, Line&rang;</samp> couple is emitted, where <samp>Line</samp>
		 * is the same line received as input.
		 */
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			
			//System.out.println("ID MAP: "+context.getTaskAttemptID().getTaskID());
			String line = value.toString();
			if (!line.equals("")) {
				// idSeq   \t   k-mer   \t   occurance
				//System.out.println("line(nonVuota) : |"+ line +"|");
				String[] params = line.split("\t");
				//System.out.println("Params: "+params[0]+ " "+params[1]+ " "+ params[2]);
				
				Text outputKey = new Text(params[1]);

				//System.out.println("outputKey: "+ outputKey);
				//System.out.println("outputValue: " + value);
				
				context.write(outputKey, value);		
				//System.out.println("END MAPPER...");
			}

		}
	}

	/**
	 * The <samp>ScoreReducer</samp> class reads <samp>&lang;Kmer, List[line]&rang;</samp> pairs through MapReduce shuffle and
	 * creates an <samp>HashMap</samp> with all the partial score and at the and of the task emits <samp>&lang;LABEL(S,Q), 
	 * pScore(S,Q)&rang;</samp> pairs.<br />
	 * Part of Phase 1.
	 *
	 */
	public static class ScoreReducer extends Reducer<Text, Text, Text, LongWritable> {
		/**
		 * A local <samp>HashMap</samp> map to store <samp>&lang;IDseqS-ISseqQ, P&rang;</samp> key-value pairs.
		 * 
		 * @see ScoreReducer#reduce(Text, Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
		 */
		private HashMap<String, Long> pScoreMap = new HashMap<>();

		/**
		 * The <samp>reduce(Text, Iterable&lt;Text&gt;, Context)</samp> method takes as input <samp>&lang;Kmer, L&rang;</samp>
		 * couples, where <samp>Kmer</samp> is a k-mer and <samp>L</samp> is a list of lines with format 
		 * <samp>IdSeq\tKmer&prime;\tOccurrences</samp> where <samp>Kmer&prime;</samp> = <samp>Kmer</samp>.<br />
		 * It builds oredered unrepeated <samp>(IDseqS, IDseqQ)</samp> couples where <samp>IDseqS</samp> and <samp>IDseqQ</samp> 
		 * are identifiers of two sequences and calculates the product <samp>P</samp> of <samp>IDseqS</samp> and <samp>IDseqQ
		 * </samp> occurrences and locally stores <samp>&lang;IDseqS-ISseqQ, P&rang;</samp> key-value pairs into a <samp>HashMap
		 * </samp> map, where <samp>IDseqS-IDseqQ</samp> is a label associated to product of occurrences of <samp>IDseqS</samp>
		 * and <samp>IDseqQ</samp> sequences.
		 * 
		 * @see ScoreReducer#pScoreMap
		 */
		@Override
		protected void reduce(Text kmer, Iterable<Text> klCombined, Context context) throws IOException, InterruptedException {
			
			///System.out.println("ID REDUCE: "+context.getTaskAttemptID().getTaskID());
			
			/*
			System.out.printf("REDUCER key:%s \n", kmer.toString());
			for( Text t : klCombined ) {
				System.out.println(">> " + t.toString());
			}
			System.out.println("END");
			*/
			
			ArrayList<KmerLine> sequence = new ArrayList<>();
			String[] params = null;
			
			///System.out.println("kmer (KEY) = " + kmer.toString());
			
			for (Text txt: klCombined) {
				params = txt.toString().split("\t");
				KmerLine newKL = new KmerLine(params[0], params[1], Long.valueOf(params[2]));
				sequence.add(newKL);
				///System.out.println("klCombined["+ r++ +"] = |" + txt + "|");
			}

			///System.out.println("sequence : " + sequence.toString() );

			KmerLine seqInS, seqInQ;
			String label;
			Long pScore = null, iProduct = null;

			for (int i=0; i<sequence.size(); i++) {
				seqInS = sequence.get(i);

				for (int j=i+1; j<sequence.size(); j++) {
					seqInQ = sequence.get(j);

					//construct label
					if (seqInS.getIdSeq().compareTo(seqInQ.getIdSeq()) < 0) {
						label = seqInS.getIdSeq() + "-" + seqInQ.getIdSeq();
					}
					else {
						label = seqInQ.getIdSeq() + "-" + seqInS.getIdSeq();
					}

					iProduct = seqInS.getOccurrances() * seqInQ.getOccurrances();

					//partialD2Score.put(label, iProduct);
					///////context.write(new Text(label), new LongWritable(iProduct) );

					pScore = pScoreMap.get(label);
					if (pScore == null) {
						//System.out.printf("PUTTING %s to %d\n", label, iProduct);
						pScoreMap.put(label, iProduct);
					}
					else {
						//System.out.printf("PUTTING %s to +%d\n", label, iProduct);
						pScoreMap.put(label, pScore + iProduct);
					}

					//System.out.println("i= "+i+" j= "+j+"\n Label: "+label+ " iProduct: "+ iProduct);
				}

			}
			//System.out.println("END REDUCER...");
		}

		/**
		 * The <samp>cleanup(Context)</samp> method deals with emitting <samp>&lang;IDseqS-ISseqQ, P&rang;</samp> key-value
		 * pairs before the end of {@link ScoreReducer#reduce(Text, Iterable, org.apache.hadoop.mapreduce.Reducer.Context)}
		 * task.<br />
		 * Records are saved in a directory over HDFS and represent final result if there's only a single reduce task.
		 * 
		 * @see ScoreReducer#reduce(Text, Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
		 */
		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException {

			//System.out.println("CLEANUP!!!!");

			//System.out.println("pScoreMap size::" + pScoreMap.size());

			for (String k: pScoreMap.keySet()) {
				context.write(new Text(k), new LongWritable(pScoreMap.get(k)));
			}

		}
	}

}
