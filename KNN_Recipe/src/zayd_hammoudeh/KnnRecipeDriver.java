package zayd_hammoudeh;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * This class is a driver for map reduce task.
 * 
 * This is repurposed from code originally written by Shubhangi Rakhonde.
 * 
 * @author Zayd Hammoudeh
 */

public class KnnRecipeDriver extends Configured implements Tool
{
	/**
	 * Main function to run the map reduce job
	 * @param args[0] = Training File path
	 * 		  args[1] = Value of K for knn
	 * 	      args[2] = Distance metric for the strategy pattern
	 *        args[3] = Directory path of Testing Files
	 *        args[4] = Directory path of output directory
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		
		//Path p = new Path(args[4]);
		if(Files.isDirectory(Paths.get(args[4]))){
			File f = new File(args[4]);
			FileUtils.deleteDirectory(f);
		}
		
		Configuration conf = new Configuration();
		System.exit(ToolRunner.run(conf, new KnnRecipeDriver(), args));
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
		getConf().set("kValue",args[1]);
		getConf().set("distanceMetric",args[2]);
		
		@SuppressWarnings("deprecation")
		Job job = new Job(getConf(), "build knn");
		job.setJarByClass(zayd_hammoudeh.KnnRecipeDriver.class);
		job.setMapperClass(zayd_hammoudeh.KnnRecipeMapper.class);
		job.setReducerClass(zayd_hammoudeh.KnnRecipeReducer.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		//setup input and output paths
		FileInputFormat.addInputPath(job, new Path(args[3]));
		FileOutputFormat.setOutputPath(job, new Path(args[4]));
		
		//launch job synchronously
		return job.waitForCompletion(true) ? 0:1;
	}
}

