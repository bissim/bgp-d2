package bgp.d2distributed;

import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import bgp.d2distributed.D2D.ScoreReducer;

/**
 * The <samp>SumPhase</samp> class provides Mapper/Reducer implementation to sum the partial D2 scores.
 * <ul>
 * <li><samp>{@link IdentityMapper}</samp> reads lines from the <samp>FileInputFormat</samp> splitter and produces
 * <samp>&lang;LABEL(S,Q), pScore(S,Q)&rang;</samp> pairs</li>
 * <li><samp>{@link SumReducer}</samp>, via the MapReduce shuffle phase, reads <samp>&lang;LABEL(S,Q),
 * List[pScore(S,Q)]&rang;</samp> pairs and calculates the final D2 score and emits it</li>
 * </ul>
 * 
 * <p><strong>Please note.</strong> If the number of reduce task of {@link D2D} is more than one, it is necessary a sum phase, 
 * with another job. For these reason are implemented another Mapper/Reducer class in {@link SumPhase}</p>
 *
 * @see IdentityMapper
 * @see SumReducer
 * @see D2D
 */
public class SumPhase {

	/**
	 * The <samp>{@link IdentityMapper}</samp> class reads lines from the <samp>FileInputFormat</samp> splitter and produces
	 * <samp>&lang;LABEL(S,Q), pScore(S,Q)&rang;</samp> pairs.<br />
	 * Part of Phase 2.
	 */
	public static class IdentityMapper extends Mapper<Object, Text, Text, LongWritable> {

		/**
		 * The separator character used in input lines.
		 * 
		 * @see IdentityMapper#setup(org.apache.hadoop.mapreduce.Mapper.Context)
		 */
		String separator;

		/**
		 * The <samp>setup(Context)</samp> method sets the tabulation character as separator for the input lines.
		 * 
		 * @see IdentityMapper#separator
		 */
		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			//this.separator = context.getConfiguration().get("mapreduce.output.textoutputformat.separator");
			this.separator = "\t";
		}

		/**
		 * The <samp>map(Object, Text, Context)</samp> method takes as input the <samp>&lang;IDseqS-ISseqQ, P&rang;</samp>
		 * pairs as a <samp>Text</samp> object and emits it as a proper couple of <samp>Text</samp> and <samp>LongWritable</samp>
		 * objects.
		 */
		@Override
		protected void map(Object key, Text line, Context context) throws IOException, InterruptedException {

			String params[] = line.toString().split(separator);

			context.write(new Text(params[0]), new LongWritable(Long.valueOf(params[1])));

		}

	}

	/**
	 * The <samp>{@link SumReducer}</samp> class, via the MapReduce shuffle phase, reads <samp>&lang;LABEL(S,Q),
	 * List[pScore(S,Q)]&rang;</samp> pairs and calculates the final D2 score and emits it.<br />
	 * Part of Phase 2.
	 */
	public static class SumReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

		/**
		 * A local <samp>HashMap</samp> map to store <samp>&lang;IDseqS-ISseqQ, P&rang;</samp> key-value pairs.
		 * 
		 * @see SumReducer#reduce(Text, Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
		 */
		HashMap<String, Long> allScores = new HashMap<>();

		/**
		 * The <samp>reduce(Text, Iterable&lt;LongWritable&gt;, Context)</samp> method takes the <samp>&lang;IDseqS-ISseqQ,
		 * L&rang;</samp> pairs, where <samp>L</samp> is a list of partial scores for <em>S</em> and <em>Q</em> for the same
		 * k-mer, calculates total couple score by summing all the partial scores and emits the <samp>&lang;IDseqS-IDseqQ,
		 * D2(S, Q)&rang;</samp> where <samp>D2(S, Q)</samp> is the distance between <em>S</em> and <em>Q</em> sequences.
		 * 
		 * @see SumReducer#allScores
		 */
		@Override
		protected void reduce(Text labelSQ, Iterable<LongWritable> pScoresSQ, Context context) throws IOException, InterruptedException {
			long d2ScoreSQ = 0L;
			for (LongWritable ps: pScoresSQ) {
				d2ScoreSQ += ps.get();
			}

			Long currScore = allScores.get(labelSQ.toString()); // it should always be null
			if (currScore == null) {
				allScores.put(labelSQ.toString(), d2ScoreSQ);
			}
			else {
				allScores.put(labelSQ.toString(), currScore + d2ScoreSQ);
			}
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

			for (String labelSQ: allScores.keySet()) {
				context.write(new Text(labelSQ), new LongWritable(allScores.get(labelSQ)));
			}

		}
	}
	
}
