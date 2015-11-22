package Ensemble;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;

import java.io.IOException;



public class EnsembleMapper extends Mapper <LongWritable,Text,Text,Text> {
	
	private static final boolean USE_STRING_TOKENIZER = false;
	
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    
		// Split the input text by commas.
		String[] splitKeyIfExists = value.toString().split("\t");
		String[] splitValues = splitKeyIfExists[splitKeyIfExists.length - 1].toString().split(",");
		
		double sumOfCuisineVals = 0;
		for(int j = 2; j < splitValues.length; j++)
			sumOfCuisineVals += Double.parseDouble(splitValues[j]);
		
		// Build the output string.
		StringBuffer sb = new StringBuffer();

		// Add the recipe ID and stored label
		sb.append( splitValues[0] + "," + splitValues[1] );
		
		// Build the cuisine values
		for(int j = 2; j < splitValues.length; j++){
			sb.append(",");
			double normalizedVal = Double.parseDouble(splitValues[j])/sumOfCuisineVals;
			sb.append(normalizedVal);
			
		}

		// Key is the recipe ID
		// Value is the normalized class values.	
		context.write(new Text(splitValues[0]), new Text(sb.toString()));
   }
}
