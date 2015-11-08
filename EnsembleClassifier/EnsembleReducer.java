package Ensemble;

import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.FloatWritable;
import java.io.IOException;
import java.util.StringTokenizer;


public class EnsembleReducer  extends Reducer <Text,Text,Text,Text> {
	
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		double[] cuisineScore = null;
		int actualCuisineType = -1;

		// Iterate through all mapped value lines.
		for(Text value: values) {
			
			// Parse the key's values.
			String[] splitStr = value.toString().split(",");
			
			// Initialize the class values for the cuisine score and actual cuisine type
			if(cuisineScore == null){
				actualCuisineType = Integer.parseInt(splitStr[1]);
				cuisineScore = new double[splitStr.length - 2];
				for(int i = 0; i < cuisineScore.length; i++)
					cuisineScore[i] = 0;
			}

			// Aggregate the class scores
			for(int i = 2; i < splitStr.length; i++)
				cuisineScore[i-2] += Double.parseDouble(splitStr[i]);

		} 

		// Determine the best cuisine
		int bestCuisine = 0;
		for(int i = 1; i < cuisineScore[i]; i++){
		
			if(cuisineScore[bestCuisine] < cuisineScore[i])
				bestCuisine = i;

		}
		
		// Key - Recipe ID
		// Value - Comma separated list.  Element 0 is the actual cuisine number. Element 1 is the predicted cuisine number	
		context.write(key, new Text( actualCuisineType + "," + bestCuisine));

	}
}
