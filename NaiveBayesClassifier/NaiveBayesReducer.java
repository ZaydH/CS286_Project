import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

/**
 * Reducer for training Naive Baye's Classifier.
 * CAUTION : Few variables are hard-coded here. I still not have found out the way to get this data to reducer 
 * hence, hard-coded : trainingCount = 26516 // total number of input records from training.txt
 *                     cuisineTotal = 20 // total number of cuisine
 *                     ingrTotal = 5926 // total number of ingredients
 * hence, I have removed 1st line that gives me this count from the training dataset.
 * 
 * @author "Shubhangi Rakhonde, CS298, SJSU, Fall 2015"
 *
 */
public class NaiveBayesReducer extends Reducer<Text, Text, Text, Text> 
{
	public int trainingCount = 26516; // Total input records in training.txt
	//public int trainingCount = 4; // Total input records in small training dataset
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		System.out.println("IN NAIVE BAYES REDUCER");
		Integer keyInt = Integer.parseInt(""+key);
		String out = "";
		int cuisineTotal = 20; // Total number of cuisine present in our dataset
		int ingrTotal = 5926; // Total number of ingredients present in our dataset
//		int cuisineTotal = 2; // Total number of cuisine present in small dataset
//		int ingrTotal = 6; // Total number of ingredients present in small dataset
		int recipeCount = 0; // Total recipe count belonging to a cuisine  
		
		int ingrCountForCuisine = 0; // count of total ingredients used in a cuisine
		Integer ingrCount[] = new Integer[ingrTotal]; // count to record how often an ingredient is used in a cuisine 
		//Initialize the arrays to 0
		for (int j = 0; j < ingrTotal; j++)
		{
			ingrCount[j] = 0;
		}
		for(Text value :values) 
		{
			recipeCount++;
			String[] tokens = value.toString().split("_");
			for (int i = 0; i < tokens.length; i++)
			{
				int ingrId = Integer.parseInt(""+tokens[i]);
				ingrCount[ingrId]++;
				ingrCountForCuisine++;
			}	
		}
		
		//COMPUTE PRIOR PROBABILITY
		Double priorProbability = NaiveBayesClassifier.computePriors(recipeCount, trainingCount);
		out += priorProbability+"_";
		
		//COMPUTE LIKELIHOODS
		Double[] likelihood = NaiveBayesClassifier.computeLikelihoods(ingrCount, ingrCountForCuisine, ingrTotal);
		out += prettyPrintArray(likelihood);
		context.write(key, new Text(out));
	}
	
	public static <E> String prettyPrintArray(E[] arr)
	{
		String out = "";
		for (int i = 0; i < arr.length; i++)
		{
			out += "_"+arr[i];
		}
		return out;
	}
}
