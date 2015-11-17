import java.util.ArrayList;

public class NaiveBayesClassifier 
{	
	public static Double[] computePriors(Integer[] cuisineCount, int trainingCount)
	{
		Double[] cuisineProbability = new Double[cuisineCount.length];
		for (int i = 0; i < cuisineProbability.length; i++)
		{
			cuisineProbability[i] = 1.0 * cuisineCount[i]/trainingCount;
		}
		return cuisineProbability;
	}
	
	public static Double computePriors(int cuisineCount, int trainingCount)
	{
		Double cuisineProbability = 0.0;
		cuisineProbability = 1.0 * cuisineCount/trainingCount;
		return cuisineProbability;
	}
	
	public static Double[][] computeLikelihoods(Integer[][] ingrCount, Integer[] ingrCountForCuisine, int ingrTotal)
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
	
	public static Double[] computeLikelihoods(Integer[] ingrCount, Integer ingrCountForCuisine, int ingrTotal)
	{
		Double[]ingrProbability = new Double[ingrTotal];
		for (int i = 0; i < ingrTotal; i++)
		{
			ingrProbability[i] = (ingrCount[i]+1.0)/(ingrCountForCuisine+ingrTotal);
		}
		return ingrProbability;
	}
	public static Double[][] computePosteriors(ArrayList<ArrayList<Integer>> testSet, Double[] priors, Double likelihoods[][])
	{
		Double[][] logPosteriors = new Double[testSet.size()][priors.length];
		for (int i = 0; i < testSet.size(); i++)
		{
			for (int j = 0; j < priors.length; j++)
			{
				double pOfCuisineGivenIngr = 0.0;
				pOfCuisineGivenIngr += Math.log(priors[j]);
				for (int k = 0; k < testSet.get(i).size(); k++)
				{
					int testIngrId = testSet.get(i).get(k);
					pOfCuisineGivenIngr += Math.log(likelihoods[j][testIngrId]);
				}
				logPosteriors[i][j] = pOfCuisineGivenIngr;
			}	
		}
		return logPosteriors;
	}
}			
