import java.util.ArrayList;

import org.apache.hadoop.io.retry.RetryPolicies.MultipleLinearRandomRetry;
/**
 * Class having Naive Bayes classification algorithms
 * @author "Shubhangi Rakhonde, CS298, SJSU, Fall 2015"
 */
public class NaiveBayesClassifier 
{	
	public static final int NORMALISER= 10;
	/**
	 * This function computes prior probability of each class
	 * @param cuisineCount An array having count of recipes for each cuisine in training set
	 * @param trainingCount Total number of elements in training set
	 * @return Double[] an array of prior probabilities
	 */
	public static Double[] computePriors(Integer[] cuisineCount, int trainingCount)
	{
		Double[] cuisineProbability = new Double[cuisineCount.length];
		for (int i = 0; i < cuisineProbability.length; i++)
		{
			cuisineProbability[i] = 1.0 * cuisineCount[i]/trainingCount;
		}
		return cuisineProbability;
	}
	
	/**
	 * This function computes the likelihood of each ingredient in given cuisine using Multinomial method
	 * @param ingrCount An array having counts for each ingredient in each cuisine
	 * @param ingrCountForCuisine An array having total count of ingredients in each cuisine
	 * @param ingrTotal Total number of ingredients
	 * @return Double[][] likelihood probabilities of each ingredient in each cuisine
	 */
	public static Double[][] computeLikelihoodsMultinomial(Integer[][] ingrCount, Integer[] ingrCountForCuisine, int ingrTotal)
	{
		Double[][]ingrProbability = new Double[ingrCount.length][ingrCount[0].length];
		for (int i = 0; i < ingrCount.length; i++)
		{
			for (int j = 0; j < ingrCount[0].length; j++)
			{
				ingrProbability[i][j] = (ingrCount[i][j]+1.0)/(ingrCountForCuisine[i]+ingrTotal);
			}
			
		}
		return ingrProbability;
	}
	
	
	/**
	 * This function computes the likelihood of each ingredient in given cuisine using Bernoulli method
	 * @param cuisineCount An array having count of recipes for each cuisine in training set
	 * @param ingrCount Integer[i][j] Counts of ingredient j in cuisine i
	 * @param ingrCountForCuisine Integer[i] Total number of ingredients in cuisine i
	 * @param recipeCountForIngr Integer[i][j] Number of recipes in training set that has ingredient j for cuisine i
	 * @param cuisineTotal Total number of cuisine for given model
	 * @return
	 */
	public static Double[][] computeLikelihoodsBernoulli(Integer[] cuisineCount, Integer[][] ingrCount, Integer[] ingrCountForCuisine, Integer[][] recipeCountForIngr, int cuisineTotal)
	{
		Double[][]ingrProbability = new Double[ingrCount.length][ingrCount[0].length];
		for (int i = 0; i < ingrCount.length; i++) 
		{
			for (int j = 0; j < ingrCount[0].length; j++)
			{
				if(ingrCount[i][j] == 0)
				{
					ingrProbability[i][j] = 1 - (recipeCountForIngr[i][j]+1.0)/(cuisineCount[i]+ingrCount.length);
				}
				else
				{
					ingrProbability[i][j] = (recipeCountForIngr[i][j]+1.0)/(cuisineCount[i]+cuisineTotal);
				}
			}
		}
		return ingrProbability;
	}
	/**
	 * This function computes the posterior probability of a cuisine given a set of vectors
	 * @param testSet An arraylist of test set elements
	 * @param priors Prior probability matrix
	 * @param likelihoods Ingredient likelihood matrix
	 * @return Double[] Probability of cuisine given set of ingredients
	 */
	public static Double[] computePosterior(ArrayList<Integer> testSet, Double[] priors, Double likelihoods[][])
	{
		Double[] logPosteriors = new Double[priors.length];

			for (int j = 0; j < priors.length; j++)
			{
				double pOfCuisineGivenIngr = 0.0;
				pOfCuisineGivenIngr += Math.log(priors[j]);
				for (int k = 0; k < testSet.size(); k++)
				{
					int testIngrId = testSet.get(k);
					pOfCuisineGivenIngr += Math.log(likelihoods[j][testIngrId])+NORMALISER;
				}
				logPosteriors[j] = pOfCuisineGivenIngr;
			}	
		return logPosteriors;
	}
	
	/**
	 * This function pretty prints one-dimensional array
	 * @param arr An array to print
	 * @return String buffer to print
	 */
	public static <E> StringBuffer prettyPrintArray(E[] arr)
	{
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < arr.length; i++)
		{
			out.append(arr[i]);
			out.append("\t");
		}
		return out;
	}
	
	/**
	 * This function prints a two-dimensional array
	 * @param arr A two dimensional array
	 * @return StringBuffer to print
	 */
	public static <T> StringBuffer prettyPrintArray(T[][] arr)
	{
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < arr.length; i++)
		{
			for (int j = 0; j < arr[0].length; j++)
			{
				out.append(arr[i][j]);
				out.append("\t");
			}
			out.append("\n");
		}
		return out;
	}
	
	/**
	 * This function creates an output string to print in given format
	 * @param arr A one dimensional array to print comma separated.
	 * @return StringBuffer to print to file
	 */
	public static <E> StringBuffer getCommaSeparatedArray(E[] arr)
	{
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < arr.length; i++)
		{
			if (i==0)
			{
				out.append(arr[i]);
			}
			else 
			{
				out.append(",");
				out.append(arr[i]);
			}
		}
		return out;
	}
}			
