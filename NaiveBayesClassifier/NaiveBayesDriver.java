

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;

/**
 * This class is a driver for map reduce task.
 * @author "Shubhangi Rakhonde, CS286, SJSU, Fall 2015"
 */
public class NaiveBayesDriver extends Configured implements Tool
{
	/**
	 * Main function to run the map reduce job
	 * @param args[0] = Training File path
	 *        args[1] = Naive Bayes Model name : NaiveBayesModel = "bernoulli" or "multinomial"
	 *        args[2] = Directory path of Testing Files
	 *        args[3] = Directory path of output directory
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		Configuration conf = new Configuration();
		System.exit(ToolRunner.run(conf, new NaiveBayesDriver(), args));
	}
	
	/**
	 * This function checks the command line arguments and creates the map reduce job
	 */
	public int run(String[] args) throws Exception
	{
		//Check CLI
//		if (args.length != 3) {
//	         System.err.printf("usage: %s [generic options] <Training_Set_File> <Testing_Set_Directory> <Output_Directory>\n", getClass().getSimpleName());
//	         System.exit(1);
//	      }
		getConf().set("trainingFile",args[0]);
		getConf().set("NaiveBayesModel",args[1]);
		Job job = new Job(getConf(), "build NB Classifier");
		job.setJarByClass(NaiveBayesDriver.class);
		job.setMapperClass(NaiveBayesMapper.class);
		job.setReducerClass(NaiveBayesReducer.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		//setup input and output paths
		FileInputFormat.addInputPath(job, new Path(args[2]));
		FileOutputFormat.setOutputPath(job, new Path(args[3]));
		
		//launch job synchronously
		return job.waitForCompletion(true) ? 0:1;
	}
}
