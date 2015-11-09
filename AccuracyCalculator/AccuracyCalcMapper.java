package Accuracy;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;

import java.io.IOException;



public class AccuracyCalcMapper extends Mapper <LongWritable,Text,Text,Text> {
	
	private static final boolean USE_STRING_TOKENIZER = false;
	
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    
		// Split the input text by commas.
		String[] splitStr = value.toString().split("\\s+", -1); // "-1" means ignore blank strings
		String[] cuisineTypes = splitStr[1].split(",");
		
		Text outputText;
		if(cuisineTypes[0].equals(cuisineTypes[1]))
			outputText = new Text("1");
		else
			outputText = new Text("0");

		// Key is the recipe ID
		// Value is the normalized class values.	
		context.write(new Text("accuracy"), outputText);
   }
}
