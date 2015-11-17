import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;

import java.io.IOException;
/**
 * Mapper for training Naive Bayes Classifier
 * @author "Shubhangi Rakhonde, CS286, SJSU, Fall 2015"
 */
public class NaiveBayesMapper extends Mapper<LongWritable, Text, Text, Text>
{
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException 
	{
		System.out.println("IN NAIVE BAYES MAPPER");
		String line = value.toString();
		String[] tokens = line.split(",");
		String v = "";
		for (int i = 2; i < tokens.length; i++)
		{
			if(i == 2)
			{
				v += tokens[i];
			}
			else {
				v += "_"+tokens[i];
			}
			
		}
		context.write(new Text(tokens[1]), new Text(v));
	}
}
