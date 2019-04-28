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
public class Main2 {

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

		Job job = Job.getInstance(conf, "D2_Score_unico");
		job.setJarByClass(D2D.class); // set program class

		job.setMapperClass(IndexerMapper.class); // set Mapper class
		job.setReducerClass(ScoreReducer.class); // set Reducer class

		// REDUCER
		job.setOutputKeyClass(Text.class); // set reducer key class
		job.setOutputValueClass(LongWritable.class); // set reducer value class

		// MAPPER
		job.setMapOutputKeyClass(Text.class); // set mapper key class output
		job.setMapOutputValueClass(Text.class); // setta mapper value class output

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

		System.exit(job.waitForCompletion(true) ? 0 : 1);	

	}

}
