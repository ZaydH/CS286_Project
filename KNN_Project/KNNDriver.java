package KNN_YashiKamboj;

import java.util.*;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

public class KNNDriver extends Configured implements Tool {
   
   /**
    * This function checks the command line arguments and creates the map reduce job
    */

   public int run(String[] args) throws Exception {
      // check the CLI
      if (args.length != 5) {
         System.err.printf("usage: %s [generic options] <inputfile> <outputdir>\n", getClass().getSimpleName());
         System.exit(1);
      }

      getConf().set("trainingFile", args[0]);
      getConf().set("K", args[1]);
      getConf().set("distanceMetric", args[2]);

      Job job = new Job(getConf(), "Yashi_Kamboj");
      job.setJarByClass(KNNDriver.class);
      job.setMapperClass(KNNMapper.class);
      job.setReducerClass(KNNReducer.class);

      job.setInputFormatClass(TextInputFormat.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(Text.class);

      // setup input and output paths
      FileInputFormat.addInputPath(job, new Path(args[3]));
      FileOutputFormat.setOutputPath(job, new Path(args[4]));
 
      // launch job syncronously
      return  job.waitForCompletion(true) ? 0 : 1;
   }
   /**
    * This function runs map reduce jobs
    * @param args[0] = Directory path of Training Data
    *        args[1] = Value of k for finding nearest neighbors
    *        args[2] = Distance metric for implementing strategy pattern
    *        args[3] = Directory path of Test Data
    *        args[4] = Directory path of output data files 
    * @throws Exception
    */
   public static void main(String[] args) throws Exception { 
      Configuration conf = new Configuration();
      System.exit(ToolRunner.run(conf, new KNNDriver(), args));
   } 
}

