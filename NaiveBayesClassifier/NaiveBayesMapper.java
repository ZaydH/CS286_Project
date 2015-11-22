import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Description of NaiveBayesMapper
 * @author "Shubhangi Rakhonde, CS286, SJSU, Fall 2015"
 */
public class NaiveBayesMapper extends Mapper<LongWritable, Text, Text, Text>
{
	private int count, cuisineTotal, ingrTotal, trainingCount;
	private Integer[] cuisineCount;
	private Integer[][] ingrCount;
	private Integer[] ingrCountForCuisine;
	private Integer[][] recipeCountForIngr;
	private Double[] priors;
	private Double[][] likelihoods;
	
	/**
	 * Description of setUp
	 * @param context
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	protected void setup(Context context) throws IOException, InterruptedIOException
	{
		//Initialize the parameters
		String thisLine = "";
		count = 0;
		cuisineTotal = 0;
		ingrTotal = 0; 
		trainingCount = 0;
		cuisineCount = null;
		ingrCount = null;
		ingrCountForCuisine = null;
		priors = null;
		likelihoods = null;
		recipeCountForIngr = null;
		
		//Get the training data path
		String trainingFile = context.getConfiguration().get("trainingFile");
		//System.out.println("Training file path from conf = "+trainingFile);
		//String trainingFile = "/Users/shubhangi/Documents/CS286_Project/RecipePreprocessor/training_set/training_set.txt";
		
		//Read the training data and build the classifier : Uncomment 3 lines and comment 4th
//		Path p = new Path(trainingFile);
//		FileSystem fs = FileSystem.get(new Configuration());
//		BufferedReader training = new BufferedReader(new InputStreamReader(fs.open(p)));
		BufferedReader training = new BufferedReader(new FileReader(trainingFile));

		while ((thisLine = training.readLine()) != null)
		{
			//System.out.println("thisLine = "+thisLine);
			if(count == 0)
			{
				String[] line1 = thisLine.split(",");
				cuisineTotal = Integer.parseInt(line1[0]);
				ingrTotal = Integer.parseInt(line1[1]);
				cuisineCount = new Integer[cuisineTotal];
				ingrCountForCuisine = new Integer[cuisineTotal];
				ingrCount = new Integer[cuisineTotal][ingrTotal];
				recipeCountForIngr = new Integer[cuisineTotal][ingrTotal];
				//initialize everything to 0
				for (int i = 0; i < cuisineCount.length; i++)
				{
					cuisineCount[i] = 0;
					ingrCountForCuisine[i] = 0;
				}
				for (int i = 0; i < ingrCount.length; i++)
				{
					for (int j = 0; j < ingrCount[0].length; j++)
					{
						ingrCount[i][j] = 0;
						recipeCountForIngr[i][j] = 0;
					}
				}
				count++;
				continue;
			}
			String[] tokens = thisLine.split(",");
			int cuisineId = Integer.parseInt(tokens[1]);
			cuisineCount[cuisineId]++;
			Set<Integer> ingrDistinct = new HashSet<Integer>();
			for (int i = 2; i < tokens.length; i++)
			{
				int ingrId = Integer.parseInt(tokens[i]);
				ingrCount[cuisineId][ingrId]++;
				ingrCountForCuisine[cuisineId]++;
				ingrDistinct.add(ingrId);
			}
			for (Integer ing : ingrDistinct)
			{	
				recipeCountForIngr[cuisineId][ing]++;
			}
			count++;
		}
		trainingCount = count-1;
//		System.out.println("trainingCount = "+trainingCount);
//		System.out.println("cuisineTotal = "+cuisineTotal);
//		System.out.println("ingrTotal = "+ingrTotal);
//		System.out.println("======== CUISINE COUNT =======");
//		System.out.println(NaiveBayesClassifier.prettyPrintArray(cuisineCount));
//		System.out.println("======== ingredient COUNT =======");
//		System.out.println(NaiveBayesClassifier.prettyPrintArray(ingrCount));
		
		priors = NaiveBayesClassifier.computePriors(cuisineCount, trainingCount);
		//likelihoods = NaiveBayesClassifier.computeLikelihoodsMultinomial(ingrCount, ingrCountForCuisine, ingrTotal);
		likelihoods = NaiveBayesClassifier.computeLikelihoodsBernoulli(cuisineCount, ingrCount, ingrCountForCuisine, recipeCountForIngr, cuisineTotal);
		// PRINT PRIORS and LIKELIHOODS
//		System.out.println("=============== PRIORS ===============");
//		System.out.println(NaiveBayesClassifier.prettyPrintArray(priors));
//		System.out.println("=============== LIKELIHOODS ==========");
//		System.out.println(NaiveBayesClassifier.prettyPrintArray(likelihoods));		
	}
	
	/**
	 * Description of map function
	 */
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException 
	{
		//System.out.println("IN NAIVE BAYES MAPPER");
		String line = value.toString();
		ArrayList<Integer> recipeIngr = new ArrayList<Integer>();
		String val  = "";
		String[] tokens = line.split(",");
		int recipeId = Integer.parseInt(tokens[0]);
		int realCuisineId = Integer.parseInt(tokens[1]);
		for (int i = 2; i < tokens.length; i++)
		{
			recipeIngr.add(Integer.parseInt(tokens[i]));
		}
		//GET POSTERIORS
		Double[] logPosteriors = NaiveBayesClassifier.computePosterior(recipeIngr, priors, likelihoods);
		val = recipeId+","+realCuisineId+","+NaiveBayesClassifier.getCommaSeparatedArray(logPosteriors);
		context.write(new Text(""+recipeId), new Text(val));	
	}
}
