package bgp.d2distributed;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class D2D {
	/**
	 * Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
	 * KEYIN: chiave fittizia per rispettare il paradigma Mapper
	 * VALUEIN: Text (i due file)
	 * KEYOUT: k-mero
	 * VALUEOUT: prodotto delle occurrenze Si*Qi
	 */

	public static class PartialScoreMapper extends Mapper<Object, Text[], Text, LongWritable> {

		@Override
		public void map(Object key, Text[] value, Context context) throws IOException, InterruptedException {
			StringTokenizer stTokFile1 = new StringTokenizer(value[0].toString());
			StringTokenizer stTokFile2 = new StringTokenizer(value[1].toString());
			String kmerS="", kmerQ="";
			long Si=0, Qi=0;
			HashMap<String, Long> file1 = new HashMap<String, Long>();
			HashMap<String, Long> file2 = new HashMap<String, Long>();
			while(stTokFile1.hasMoreTokens() && stTokFile2.hasMoreTokens()) {
				kmerS = stTokFile1.nextToken();
				kmerQ = stTokFile2.nextToken();
				Si = Long.valueOf(stTokFile1.nextToken());
				Qi = Long.valueOf(stTokFile2.nextToken());
				file1.put(kmerS, Si);
				file2.put(kmerQ, Qi);
			}
			Iterator<Entry<String,Long>> it1 = file1.entrySet().iterator();
			Entry<String, Long> currEntry = null;
			String currKmer = "";
			long currOcc1 = 0;
			long currOcc2 = 0;
			Long partialScore = new Long(0); 
			while(it1.hasNext()){
				currEntry = it1.next();
				currKmer = currEntry.getKey();
				currOcc1 = currEntry.getValue();
				if(file2.containsKey(currKmer)) {
					currOcc2 = file2.get(currKmer);
					partialScore = currOcc1*currOcc2;
					context.write(new Text(currKmer), new LongWritable(partialScore));
				}
				else
					context.write(new Text(currKmer), new LongWritable(0));			
			}

		}
	}

	public static class D2ScoreReducer extends Reducer<Text, LongWritable, Object, LongWritable> {

		private LongWritable result = new LongWritable();

		@Override
		protected void reduce(Text kmer, Iterable<LongWritable> partialScore, Context context) throws IOException, InterruptedException {
			long score = 0;
			for (LongWritable pScore : partialScore) {
				score += pScore.get();
			}
			this.result.set(score);
			context.write(kmer,this.result);
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		System.out.println("Configuration conf = new Configuration();");
		Job job = Job.getInstance(conf, "d2d");
		System.out.println("Job job = Job.getInstance(conf, \"d2d\");");
		job.setJarByClass(D2D.class);
		System.out.println("job.setJarByClass(D2D.class);");
		job.setMapperClass(PartialScoreMapper.class);
		job.setReducerClass(D2ScoreReducer.class);
		job.setOutputKeyClass(Object.class);
		job.setOutputValueClass(LongWritable.class);
		NLineInputFormat.setNumLinesPerSplit(job, 101);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}


}
