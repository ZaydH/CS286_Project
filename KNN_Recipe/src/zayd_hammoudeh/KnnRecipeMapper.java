package zayd_hammoudeh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class KnnRecipeMapper  extends Mapper <LongWritable,Text,Text,Text> {

	int k;
	int totalNumbCuisines;
	int totalNumbIngredients;
	ArrayList<Recipe> trainingRecipes = new ArrayList<Recipe>();
	RecipeDistance calc;
	int numbCorrectClassifications=0;
	int numbTestRecipes = 0;
	Hashtable<Integer, int[]> ingredientCusineBreakdown = new Hashtable<Integer, int[]>();
	
	protected void setup(Context context) throws IOException, InterruptedIOException
	{	
		
		// Extract the value of K
		k = Integer.parseInt(context.getConfiguration().get("kValue"));
		
		// Distance Metric
		String distanceMetric = context.getConfiguration().get("distanceMetric");
		if(distanceMetric.equals("mvdm"))
			calc = new MVDM();
		else if(distanceMetric.equals("overlap"))
			calc = new Overlap();
		
		//Get the training data path
		String trainingFile = context.getConfiguration().get("trainingFile");
		Path p = new Path(trainingFile);
		FileSystem fs = FileSystem.get(new Configuration());
		BufferedReader training = new BufferedReader(new InputStreamReader(fs.open(p)));
		
		// Parse the header line
		String[] splitLine = training.readLine().split(",");
		totalNumbCuisines = Integer.parseInt(splitLine[0]);
		totalNumbIngredients = Integer.parseInt(splitLine[1]);
		
		// Create an ingredient lookup table
		if(distanceMetric.equals("mvdm")){
			for(int i = 0; i < totalNumbIngredients; i++){
				int[] tempArr = new int[totalNumbCuisines];
				ingredientCusineBreakdown.put(i, tempArr);
			}
		}
		
		// Collect the test recipes
		String fileLine;
		while ((fileLine = training.readLine()) != null){
			trainingRecipes.add(new Recipe(fileLine));
			
			if(distanceMetric.equals("mvdm")){
				Recipe trainingRec = trainingRecipes.get(trainingRecipes.size() -1);
				int[] recipeIngred = trainingRec.getIngredients();
				int cuisineNumb = trainingRec.getCuisineNumb();
				
				// Update the ingredient per cusine count
				for(int i = 0; i < recipeIngred.length; i++){
					int[] cusineBreakdown = ingredientCusineBreakdown.get(recipeIngred[i]);
					cusineBreakdown[cuisineNumb]++;
					ingredientCusineBreakdown.put(recipeIngred[i], cusineBreakdown);
				}
				
			}
		}
		training.close();
		

	}
	
	
	/**
	 * Description of map function
	 */
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException 
	{
		numbTestRecipes++;
		
		Recipe testRecipe = new Recipe(value.toString());
		ArrayList<DistanceResultsWrapper> knnResults = new ArrayList<DistanceResultsWrapper>();
		
		for(int i = 0; i < trainingRecipes.size(); i++){
			Recipe tempTrainingRecipe = trainingRecipes.get(i);
			
			//Ignore tiny recipes 
			if(tempTrainingRecipe.ingredients.length <4) continue;
			double distance = calc.dist(testRecipe, tempTrainingRecipe);
			
			// Add the results
			knnResults.add(new DistanceResultsWrapper(distance, tempTrainingRecipe.getCuisineNumb()));
		}
		Collections.sort(knnResults);
		
		// Perform the voting.
		int[] voteArray = new int[totalNumbCuisines];
		for(int i = 0; i < k; i++)
			voteArray[knnResults.get(i).getCuisineNumb()]++;
		
		int bestClass = -1, bestScore = Integer.MIN_VALUE;
		for(int i = 0; i < totalNumbCuisines; i++)
			if(voteArray[i] > bestScore){
				bestScore = voteArray[i];
				bestClass = i;
			}
		
		if(bestClass == testRecipe.getCuisineNumb())
			numbCorrectClassifications++;
		
		if(numbTestRecipes % 100 == 0)
			System.out.println("Out of " + numbTestRecipes + " recipes, the number correct is: " + numbCorrectClassifications);
		
		// Construct the output the map reduce output
		StringBuffer val = new StringBuffer(testRecipe.getID()+","+testRecipe.getCuisineNumb());
		for(int i = 0; i < totalNumbCuisines; i++)
			val.append(","+voteArray[i]);
		context.write(new Text(Integer.toString(testRecipe.getID())), new Text(val.toString()));
	
	}
	
	
	class DistanceResultsWrapper implements Comparable<DistanceResultsWrapper> {
		double dist;
		int cuisineNumb;
		
		public DistanceResultsWrapper(double dist, int cuisineNumb){
			this.dist = dist;
			this.cuisineNumb = cuisineNumb;
		}
		
		public int getCuisineNumb(){ return cuisineNumb; }
		
		public int compareTo(DistanceResultsWrapper other){ 
			if(this.dist < other.dist)
				return -1;
			if(this.dist == other.dist)
				return 0;
			else
				return 1;
		}
	}
	
	
	class Recipe {
		
		int id;
		int cuisineNumb;
		int[] ingredients;
		
		public Recipe(String line){
			
			String[] splitLine = line.split(",");
			id = Integer.parseInt(splitLine[0]);
			cuisineNumb = Integer.parseInt(splitLine[1]);
			
			ingredients = new int[splitLine.length - 2];
			for(int i = 0; i < ingredients.length; i++)
				ingredients[i] = Integer.parseInt(splitLine[i+2]);
			
		}
		
		int getID(){ return id; }
		int getCuisineNumb(){ return cuisineNumb; }
		// THis is bad class design here but I am being lazy.
		int[] getIngredients(){ return ingredients; }
		
	}
	
	
	interface RecipeDistance {
		double dist(Recipe r1, Recipe r2);
	}
	
	
	class MVDM implements RecipeDistance {
		Hashtable<String, Double> interIngredDistance = new Hashtable<String,Double>();
		
		public double dist(Recipe r1, Recipe r2){
			
			int ingredientPairs = 0;
			double totalDistSum = 0;
			for(int i = 0; i < r1.ingredients.length; i++){
				// Ensure r1 ingredient exists.  If not continue.
				int[] r1IngredBreakdown = ingredientCusineBreakdown.get(r1.ingredients[i]);
				if(r1IngredBreakdown == null)
					continue; 
				// Pair with r2 ingredients.
				for(int j = 0; j < r2.ingredients.length; j++){
					
					// Ensure r2 ingredient exists
					int[] r2IngredBreakdown = ingredientCusineBreakdown.get(r2.ingredients[j]);
					if(r2IngredBreakdown == null)
						continue; 
					
					ingredientPairs++; // Increment number of valid ingredient pairs
					
					String distKey = r1.ingredients[i] + "_" + r2.ingredients[j];
					Double tempInterIngredDist = interIngredDistance.get(distKey);
					if(tempInterIngredDist != null){
						totalDistSum += tempInterIngredDist;
						continue;
					}
					
					// Recalculate the number of times each recipe has been used
					int r1IngredientSum = 0, r2IngredientSum = 0;
					for(int c = 0; c < totalNumbCuisines; c++){
						r1IngredientSum += r1IngredBreakdown[c];
						r2IngredientSum += r2IngredBreakdown[c];
					}
					
					// Calculate the inter-recipe distance
					double tempDist = 0;
					for(int c = 0; c < totalNumbCuisines; c++)
						tempDist += Math.abs(1.0 * r1IngredBreakdown[c]/r1IngredientSum - 1.0*r2IngredBreakdown[c]/r2IngredientSum);
					totalDistSum += tempDist;
					
					//Update the lookup table.  Implementation here is lazy.  Should have a unique key.  In this case, adding both key pairs
					distKey = r1.ingredients[i] + "_" + r2.ingredients[j];
					interIngredDistance.put(distKey, new Double(tempDist));
					distKey = r2.ingredients[j] + "_" + r1.ingredients[i];
					interIngredDistance.put(distKey, new Double(tempDist));
					
				}
			}
			return totalDistSum / ingredientPairs;
		}
		
		
		
	}
	
	class Overlap implements RecipeDistance {
		
		// THis is not the most efficient implementation, but again laziness
		public double dist(Recipe r1, Recipe r2){
			double overlapDis = 0;
		
			int matches = 0;
			for(int i = 0; i < r1.ingredients.length; i++)
				for(int j = 0; j < r2.ingredients.length; j++)
					if(r1.ingredients[i] == r2.ingredients[j])
						matches++;
			
			//Return the distance as a negative since it makes the math easier and KNN is based off minimum distance
			// Also I changed to Jaccard here.
			overlapDis = -1.0 * matches / (r1.ingredients.length + r2.ingredients.length);
			return overlapDis;
		}
	}
}

