package bgp.d2distributed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class D2D {
	/**
	 * Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
	 * KEYIN: chiave fittizia per rispettare il paradigma Mapper
	 * VALUEIN: Text (i due file)
	 * KEYOUT: k-mero
	 * VALUEOUT: prodotto delle occurrenze Si*Qi
	 */

	public static class PartialScoreMapper extends Mapper<Object, Text, Text, LongWritable> {

		private HashMap<String, Long> Mapfile1 = new HashMap<String, Long>();
		private HashMap<String, Long> Mapfile2 = new HashMap<String, Long>();

		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			Path path1 = new Path("hdfs:/user/user/INPUT/seq0_all.res");
			Path path2 = new Path("hdfs:/user/user/INPUT/seq1_all.res");
			FileSystem fs = FileSystem.get(new Configuration());
			BufferedReader br1 = new BufferedReader(new InputStreamReader(fs.open(path1)));
			BufferedReader br2 = new BufferedReader(new InputStreamReader(fs.open(path2)));
			Mapfile1 = new HashMap<String, Long>();
			Mapfile2 = new HashMap<String, Long>();
			String kmerS="", kmerQ="";
			String line1 = "",line2 = "";
			long occS=0, occQ=0;
			String[] arr1, arr2;
			while((line1 = br1.readLine()) != null && !line1.equals("")) {
				arr1=line1.split("\t");
				System.out.println("arr1[0]:"+arr1[0]+ " arr1[1]:" + arr1[1]);
				occS = Long.valueOf(arr1[0]);
				kmerS = arr1[1];			
				Mapfile1.put(kmerS, occS);
			}
			while((line2 = br2.readLine()) != null && !line2.equals("")) {
				arr2=line2.split("\t");
				System.out.println("arr2[0]:"+arr2[0]+ " arr2[1]:" + arr2[1]);
				occQ = Long.valueOf(arr2[0]);
				kmerQ = arr2[1];
				Mapfile2.put(kmerQ, occQ);
			}
			System.out.println("SETUP COMPLETE");
		}

		@Override
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			Iterator<Entry<String,Long>> it1 = Mapfile1.entrySet().iterator();
			Entry<String, Long> currEntry = null;
			String currKmer = "";
			long currOcc1 = 0, currOcc2 = 0;
			Long partialScore = new Long(0); 
			currEntry = it1.next();
			currKmer = currEntry.getKey();
			currOcc1 = currEntry.getValue();
			if(Mapfile2.containsKey(currKmer)) {
				currOcc2 = Mapfile2.get(currKmer);
				partialScore = currOcc1*currOcc2;
			}	
			context.write(new Text("Seq0-Seq1"), new LongWritable(partialScore));
			System.out.println("END MAP \n partialScore: "+ partialScore);
		}
	}

	public static class D2ScoreReducer extends Reducer<Text, LongWritable, Object, LongWritable> {

		private LongWritable result = new LongWritable();

		@Override
		protected void reduce(Text kmer, Iterable<LongWritable> partialScore, Context context) throws IOException, InterruptedException {
			long score = 0;
			int count = 0;
			for (LongWritable pScore : partialScore) {
				System.out.println("Iterazione num: "+ ++count);
				score += pScore.get();
				System.out.println("pScore: "+ pScore.get() + " score: "+ score);
			}
			System.out.println("END REDUCER: \n score: "+score);
			this.result.set(score);
			context.write("Score",this.result);
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "d2d");
		job.setJarByClass(D2D.class); //setta la classe del programma
		job.setMapperClass(PartialScoreMapper.class); //setta la classe Mapper
		job.setReducerClass(D2ScoreReducer.class); //setta la classe Reducer
		job.setOutputKeyClass(Object.class); //setta la key class del reducer
		job.setOutputValueClass(LongWritable.class); //setta la value class del reducer
		job.setMapOutputKeyClass(Text.class); //setta la key class output del mapper
		job.setMapOutputValueClass(LongWritable.class); //setta la value class output del mapper
		job.setInputFormatClass(LineInputFormat.class);
		System.out.println("Num reduce task: " + job.getNumReduceTasks());
		job.setNumReduceTasks(1);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
