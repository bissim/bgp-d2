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


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.apache.commons.io.IOUtils;

/**
 * Main class for Hadoop.<br />
 * Runs the D2D algorithm: if <samp>NUM_REDUCE_TASK</samp> is equal to 1, only one job is started;
 * otherwise, 2 jobs are started: the first create partial D2 score and the other sum these results.
 */
public class Main {

	/**
	 * 
	 */
	private static double TASK_REDUCE_FACTOR = 0.95; // 1.75;

	/**
	 * Main method to execute the D2D algorithm.
	 *
	 * @param args Arguments to the command line
	 * @throws IOException HTTP client cannot execute request
	 * @throws ClientProtocolException HTTP client cannot execute request
	 * @throws JSONException JSON object received as response by HTTP client is null or invalid
	 * @throws InterruptedException Job interrupts abruptly
	 * @throws ClassNotFoundException Class not found
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException, JSONException, ClassNotFoundException, InterruptedException {

		if (args.length < 3) {
			System.out.println("<INPUT_DIR> <OUTPUT_DIR> <MEM_MB_SIZE_CONTAINER> [<TASK_REDUCE_FACTOR>]");
			System.out.println("   MEM_MB_SIZE_CONTAINER :: max memory per container, defined in mapred-site.xml");
			System.out.println("   TASK_REDUCE_FACTOR :: optional, factor defined in Hadoop doc to set number of task reduce; default 0.95");
			System.exit(-1);
		}

		String INPUT_DIR = args[0];
		String OUTPUT_DIR = args[1];
		/////int NUM_REDUCE_TASK = Integer.valueOf(args[2]);
		String MASTER_NAME = "Master";
		int MEM_MB_SIZE_CONTAINER = Integer.valueOf(args[2]);////5734; //7168;

		if (MEM_MB_SIZE_CONTAINER < 1) {
			System.out.println("Sorry, MEM_MB_SIZE_CONTAINER must be greater than 0!");
			System.exit(-1);
		}

		if (args.length == 4) {
			TASK_REDUCE_FACTOR = Integer.valueOf(args[3]);
		}

		/////GET METRICS
		System.out.println("Getting Metrics with REST call...");
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://" + MASTER_NAME + ":8088/ws/v1/cluster/metrics");
		HttpResponse response = client.execute(request);
		String jsonString = IOUtils.toString(response.getEntity().getContent());
		//System.out.println("jsonString : |" + jsonString + "|");
		JSONObject jsonObj = new JSONObject(jsonString);
		//System.out.println("jsonObj : |" + jsonObj.toString() + "|");
		int availableMB = jsonObj.getJSONObject("clusterMetrics").getInt("availableMB");
		int activeNodes = jsonObj.getJSONObject("clusterMetrics").getInt("activeNodes");

		System.out.println("availableMB : " + availableMB);
		System.out.println("activeNodes : " + activeNodes);
		System.out.println("MEM_MB_SIZE_CONTAINER : " + MEM_MB_SIZE_CONTAINER);
		System.out.println("TASK_REDUCE_FACTOR : " + TASK_REDUCE_FACTOR);

		int MAX_CONTAINER_PER_NODE = availableMB / activeNodes / MEM_MB_SIZE_CONTAINER;

		int NUM_REDUCE_TASK_DYN = (int) (TASK_REDUCE_FACTOR * (activeNodes * MAX_CONTAINER_PER_NODE));

		System.out.println("NUM_REDUCE_TASK_DYN : " + NUM_REDUCE_TASK_DYN);


		Configuration conf = new Configuration();		
		Job job;

		if (NUM_REDUCE_TASK_DYN == 1) {
			job = Job.getInstance(conf, "D2_Score_UNICO");
		}
		else {
			job = Job.getInstance(conf, "D2_Score_FASE1");
		}

		job.setJarByClass(D2D.class); // set program class

		job.setMapperClass(IndexerMapper.class); // set mapper class
		job.setReducerClass(ScoreReducer.class); // set reducer class

		// REDUCER
		job.setOutputKeyClass(Text.class); // set reducer key
		job.setOutputValueClass(LongWritable.class); // set reducer value class

		// MAPPER
		job.setMapOutputKeyClass(Text.class); // set mapper output class for key
		job.setMapOutputValueClass(Text.class); // set mapper output class for value

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setNumReduceTasks(NUM_REDUCE_TASK_DYN);
		System.out.println("Num reduce task: " + job.getNumReduceTasks());
		FileInputFormat.addInputPath(job, new Path(INPUT_DIR));

		FileSystem hdfs = FileSystem.get(conf);
		if (hdfs.exists(new Path(OUTPUT_DIR)))
			hdfs.delete(new Path(OUTPUT_DIR), true);

		FileOutputFormat.setOutputPath(job, new Path(OUTPUT_DIR));

		job.waitForCompletion(true);

		//System.exit(job.waitForCompletion(true) ? 0 : 1);	

		if (NUM_REDUCE_TASK_DYN > 1) {
			//// phase 2, sum partial scores
			Job job2 = Job.getInstance(conf, "D2_Score_FASE2");
			job2.setJarByClass(D2D.class); // set program class

			job2.setMapperClass(SumPhase.IdentityMapper.class); //setta la classe Mapper
			job2.setReducerClass(SumPhase.SumReducer.class); //setta la classe Reducer

			//REDUCER
			job2.setOutputKeyClass(Text.class); //setta la key class del reducer
			job2.setOutputValueClass(LongWritable.class); //setta la value class del reducer

			//MAPPER
			job2.setMapOutputKeyClass(Text.class); //setta la key class output del mapper
			job2.setMapOutputValueClass(LongWritable.class); //setta la value class output del mapper


			String OUTPUT_DIR2 = args[1] + "2";

			job2.setInputFormatClass(TextInputFormat.class);
			job2.setOutputFormatClass(TextOutputFormat.class);
			System.out.println("Num reduce task (fase2): " + job2.getNumReduceTasks());
			//job2.setNumReduceTasks(NUM_REDUCE_TASK);
			FileInputFormat.addInputPath(job2, new Path(OUTPUT_DIR));

			if (hdfs.exists(new Path(OUTPUT_DIR2))) {
				hdfs.delete(new Path(OUTPUT_DIR2), true);
			}

			FileOutputFormat.setOutputPath(job2, new Path(OUTPUT_DIR2));

			job2.waitForCompletion(true);
		}

	}

}
