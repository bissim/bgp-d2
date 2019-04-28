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
import bgp.d2distributed.D2D.ScoreReducer;

/**
 * Main class for Hadoop.
 */
public class Main1 {

	/**
	 * Main method to execute the D2D algorithm.
	 *
	 * @param args Arguments to the command line
	 * @throws IOException Could not get Job instance
	 * @throws InterruptedException Job interrupts abruptly
	 * @throws ClassNotFoundException Class not found
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

		if (args.length != 3) {
			System.out.println("<INPUT_DIR> <OUTPUT_DIR> <NUM_REDUCE_TASK>");
			System.exit(-1);
		}

		String INPUT_DIR = args[0];
		String OUTPUT_DIR = args[1];
		int NUM_REDUCE_TASK = Integer.valueOf(args[2]);

		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf, "D2_Score_FASE1");
		job.setJarByClass(D2D.class); // set program class

		job.setMapperClass(IndexerMapper.class); // set Mapper class
		job.setReducerClass(ScoreReducer.class); // set Reducer class

		// REDUCER
		job.setOutputKeyClass(Text.class); // set reducer key class
		job.setOutputValueClass(LongWritable.class); // set reducer value class

		// MAPPER
		job.setMapOutputKeyClass(Text.class); // set mapper key class output
		job.setMapOutputValueClass(Text.class); // set mapper value class output

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setNumReduceTasks(NUM_REDUCE_TASK);
		System.out.println("Num reduce task: " + job.getNumReduceTasks());
		FileInputFormat.addInputPath(job, new Path(INPUT_DIR));

		FileSystem hdfs = FileSystem.get(conf);
		if (hdfs.exists(new Path(OUTPUT_DIR)))
			hdfs.delete(new Path(OUTPUT_DIR), true);

		FileOutputFormat.setOutputPath(job, new Path(OUTPUT_DIR));

		job.waitForCompletion(true);

		//System.exit(job.waitForCompletion(true) ? 0 : 1);	


		//// phase 2, sum partial scores
		Job job2 = Job.getInstance(conf, "D2_Score_FASE2");
		job2.setJarByClass(D2D.class); // set program class

		job2.setMapperClass(SumPhase.IdentityMapper.class); // set Mapper class
		job2.setReducerClass(SumPhase.SumReducer.class); // set Reducer class

		// REDUCER
		job2.setOutputKeyClass(Text.class); // set reducer key class
		job2.setOutputValueClass(LongWritable.class); // set reducer value class

		// MAPPER
		job2.setMapOutputKeyClass(Text.class); // set mapper key class output
		job2.setMapOutputValueClass(LongWritable.class); // set mapper value class output


		String OUTPUT_DIR2 = args[1] + "2";

		job2.setInputFormatClass(TextInputFormat.class);
		job2.setOutputFormatClass(TextOutputFormat.class);
		System.out.println("Num reduce task (fase2): " + job2.getNumReduceTasks());
		//job2.setNumReduceTasks(NUM_REDUCE_TASK);
		FileInputFormat.addInputPath(job2, new Path(OUTPUT_DIR));

		if (hdfs.exists(new Path(OUTPUT_DIR2)))
			hdfs.delete(new Path(OUTPUT_DIR2), true);

		FileOutputFormat.setOutputPath(job2, new Path(OUTPUT_DIR2));

		job2.waitForCompletion(true);

	}

}
