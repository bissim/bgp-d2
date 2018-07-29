package bgp.d2distributed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * This class provide Mapper/Reducer implementation to the job computation of D2 Algorithm.
 * - {@link IndexerMapper} read lines from the FileInputFormat splitter and produce pair <Kmer, line>
 * - {@link ScoreReducer} via the shuffle, read <Kmer, List[line]> and create an HashMap with all the 
 *   partial score and at the and of the task emit pair <LABEL(S,Q), pScore(S,Q)>
 *
 * N.B.
 * If the number of reduce task is more than one, it is necessary a sum phase, with another job.
 * For these reason are implemented another Mapper/Reducer class in {@link SumPhase}
 *
 */
public class D2D {
	
	///////////////  FASE 1  /////////////////
	
	public static class IndexerMapper extends Mapper<Object, Text, Text, Text>{
		
		@Override
		protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			
			//System.out.println("ID MAP: "+context.getTaskAttemptID().getTaskID());
			String line = value.toString();
			if( !line.equals("") ) {
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
	
	public static class ScoreReducer extends Reducer<Text, Text, Text, LongWritable>{
		
		private HashMap<String, Long> pScoreMap = new HashMap<>();
		
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
			
			for(Text txt: klCombined) {
				params = txt.toString().split("\t");
				KmerLine newKL = new KmerLine(params[0], params[1], Long.valueOf(params[2]));
				sequence.add(newKL);
				///System.out.println("klCombined["+ r++ +"] = |" + txt + "|");
			}
			
			///System.out.println("sequence : " + sequence.toString() );
			
			KmerLine seqInS, seqInQ;
			String label;
			Long pScore = null, iProduct = null;
			
			for(int i=0; i<sequence.size(); i++) {
				seqInS = sequence.get(i);
				
				for(int j=i+1; j<sequence.size(); j++) {
					seqInQ = sequence.get(j);
					
					//construct label
					if(seqInS.getIdSeq().compareTo( seqInQ.getIdSeq() ) < 0)
						label = seqInS.getIdSeq() + "-" + seqInQ.getIdSeq();
					else 
						label = seqInQ.getIdSeq() + "-" + seqInS.getIdSeq();
					
					iProduct = seqInS.getOccurrances() * seqInQ.getOccurrances();
					
					//partialD2Score.put(label, iProduct);
					///////context.write(new Text(label), new LongWritable(iProduct) );
					
					pScore = pScoreMap.get(label);
					if( pScore == null ) {
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
		
		@Override
		protected void cleanup(Context context) throws IOException, InterruptedException {
			
			//System.out.println("CLEANUP!!!!");
			
			//System.out.println("pScoreMap size::" + pScoreMap.size());
			
			for( String k : pScoreMap.keySet() ) {
				context.write(new Text(k), new LongWritable( pScoreMap.get(k)) );
			}
			
		}
	}
	
	
	

	

}

