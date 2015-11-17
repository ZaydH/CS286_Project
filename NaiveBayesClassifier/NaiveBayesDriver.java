

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
 * This class is a driver for mapreduce task used for training the naive bayes classifier.
 * Input path : path of training.txt file
 * Output path : path of output directory
 * @author "Shubhangi Rakhonde, CS286, SJSU, Fall 2015"
 *
 */
public class NaiveBayesDriver extends Configured implements Tool
{
	public static void main(String[] args) throws Exception
	{
		Configuration conf = new Configuration();
		System.exit(ToolRunner.run(conf, new NaiveBayesDriver(), args));
	}
	
	public int run(String[] args) throws Exception
	{
		 // check the CLI
		Job job = new Job(getConf(), "build NB Classifier");
		job.setJarByClass(NaiveBayesDriver.class);
		job.setMapperClass(NaiveBayesMapper.class);
		job.setReducerClass(NaiveBayesReducer.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		//setup input and output paths
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		//launch job synchronously
		return job.waitForCompletion(true) ? 0:1;
	}
}
