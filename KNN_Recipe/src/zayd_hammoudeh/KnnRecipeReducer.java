package zayd_hammoudeh;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

/**
 *
 * @author zayd
 *
 * This code is just Shubhangi's Naive Bayes code repackaged.
 *
 */

public class KnnRecipeReducer extends Reducer <Text,Text,Text,Text>
{
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
	{
		String val = "";
		for(Text value: values) 
		{
			val = value.toString();
		}
		context.write(key, new Text(val));
		System.out.println("THE END");
		
	}

}
