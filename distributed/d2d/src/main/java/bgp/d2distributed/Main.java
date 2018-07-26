package bgp.d2distributed;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import bgp.d2distributed.D2D.IndexerMapper;
import bgp.d2distributed.D2D.PartialScoreReaderMapper;
import bgp.d2distributed.D2D.PartialScoreReducer;
import bgp.d2distributed.D2D.TotalScoreReducer;

public class Main {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		
		if( args.length != 3 ) {
			System.out.println("<INPUT_DIR> <OUTPUT_DIR> <NUM_REDUCE_TASK>");
			System.exit(-1);
		}
		
		String INPUT_DIR = args[0];
		String OUTPUT_DIR = args[1];
		int NUM_REDUCE_TASK = Integer.valueOf(args[2]);
		
		Configuration conf = new Configuration();
		
		Job job = Job.getInstance(conf, "D2_Score_fase1");
		job.setJarByClass(D2D.class); //setta la classe del programma
		job.setMapperClass(IndexerMapper.class); //setta la classe Mapper
		job.setReducerClass(PartialScoreReducer.class); //setta la classe Reducer
		job.setOutputKeyClass(Text.class); //setta la key class del reducer
		job.setOutputValueClass(LongWritable.class); //setta la value class del reducer
		job.setMapOutputKeyClass(Text.class); //setta la key class output del mapper
		job.setMapOutputValueClass(Text.class); //setta la value class output del mapper
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		System.out.println("Num reduce task: " + job.getNumReduceTasks());
		job.setNumReduceTasks(NUM_REDUCE_TASK);
		FileInputFormat.addInputPath(job, new Path(INPUT_DIR));
		
		FileSystem hdfs = FileSystem.get(conf);
		if (hdfs.exists(new Path(OUTPUT_DIR)))
			hdfs.delete(new Path(OUTPUT_DIR), true);
		
		FileOutputFormat.setOutputPath(job, new Path(OUTPUT_DIR));
		
		job.waitForCompletion(true);
		
		//System.exit(job.waitForCompletion(true) ? 0 : 1);	
		
		Job job2 = Job.getInstance(conf, "D2_Score_fase2");
		job2.setJarByClass(D2D.class);
		job2.setMapperClass(PartialScoreReaderMapper.class);
		job2.setReducerClass(TotalScoreReducer.class);
		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(LongWritable.class);
		job2.setMapOutputKeyClass(Text.class);
		job2.setMapOutputValueClass(LongWritable.class);
		job2.setInputFormatClass(TextInputFormat.class);
		job2.setOutputFormatClass(TextOutputFormat.class);
		job2.setNumReduceTasks(NUM_REDUCE_TASK);
		FileInputFormat.addInputPath(job2, new Path(OUTPUT_DIR));
		
		if (hdfs.exists(new Path(OUTPUT_DIR+"2")))
			hdfs.delete(new Path(OUTPUT_DIR+"2"), true);
		
		FileOutputFormat.setOutputPath(job2, new Path(OUTPUT_DIR+"2"));
		
		job2.waitForCompletion(true);
		
	}

}
