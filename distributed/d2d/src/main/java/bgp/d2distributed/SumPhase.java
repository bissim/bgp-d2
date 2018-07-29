package bgp.d2distributed;

import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * This class provide Mapper/Reducer implementation to sum the partial D2 score.
 * - {@link IdentityMapper} read lines from the FileInputFormat splitter and produce pair <LABEL(S,Q), pScore(S,Q)>
 * - {@link SumReducer} via the shuffle, read <LABEL(S,Q), List[pScore(S,Q)]> and calculate the final D2 score and emits it
 * 
 * N.B.
 * If the number of reduce task of {@link D2D} is more than one, it is necessary a sum phase, 
 * with another job. For these reason are implemented another Mapper/Reducer class in {@link SumPhase}
 *
 */
public class SumPhase {

	public static class IdentityMapper extends Mapper<Object, Text, Text, LongWritable>{
		
		String separator;
		
		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			//this.separator = context.getConfiguration().get("mapreduce.output.textoutputformat.separator");
			this.separator = "\t";
		}
		
		@Override
		protected void map(Object key, Text line, Context context) throws IOException, InterruptedException {
			
			String params[] = line.toString().split(separator);
			
			context.write(new Text(params[0]), new LongWritable( Long.valueOf(params[1]) ));
			
		}
		
	}
	
	public static class SumReducer extends Reducer<Text, LongWritable, Text, LongWritable>{
		
		HashMap<String, Long> allScores = new HashMap<>(); 
		
		@Override
		protected void reduce(Text labelSQ, Iterable<LongWritable> pScoresSQ, Context context) throws IOException, InterruptedException {
			long d2ScoreSQ = 0L;
			for( LongWritable ps : pScoresSQ ) {
				d2ScoreSQ += ps.get();
			}
			
			Long currScore = allScores.get(labelSQ.toString()); //dovrebbe essere sempre null
			if( currScore == null ) {
				allScores.put(labelSQ.toString(), d2ScoreSQ);
			}
			else {
				allScores.put(labelSQ.toString(), currScore + d2ScoreSQ);
			}
		}
		
		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException {
			
			for( String labelSQ : allScores.keySet() ) {
				context.write(new Text(labelSQ), new LongWritable( allScores.get(labelSQ)) );
			}
			
		}
	}
	
}
