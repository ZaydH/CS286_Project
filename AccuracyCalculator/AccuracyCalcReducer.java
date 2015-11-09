package Accuracy;

import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.FloatWritable;
import java.io.IOException;
import java.util.StringTokenizer;


public class AccuracyCalcReducer extends Reducer <Text,Text,Text,Text> {
	
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		int numbElements = 0;
		int numbOnes = 0;

		// Iterate through all mapped value lines.
		for(Text value: values) {

			// Increment the count for the number elements in the data set.			
			numbElements++;

			String valText = value.toString();
			if(valText.equals("1"))
				numbOnes++;
		} 

		double accuracy = (double) numbOnes / numbElements;
		String outputText = Double.toString(accuracy);		
		// Key - Recipe ID
		// Value - Percentage of correct classifications.
		context.write(key, new Text( outputText ));

	}
}
