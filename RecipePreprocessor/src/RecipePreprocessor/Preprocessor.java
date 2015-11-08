package RecipePreprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;

public class Preprocessor {

	/**
	 * 
	 * @param args	
	 * 				- args[0] - Path to the full dataset
	 * 				- args[1] - Directory for outputting the training set data.
	 * 				- args[2] - Directory for outputting the test set data.
	 * 				- args[3] - Number of test set files.
	 * 				- args[4] - Filename for the cuisine type mapper
	 *  
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IllegalArgumentException, FileNotFoundException, IOException {
		
		// Verify the argument count is valid.
		if(args.length != 5)
			throw new IllegalArgumentException("Invalid number of input arguments.  Need to be five input arguments.");

		// Parse the input arguments
		int k = 0;
		String trainingFile = args[k++];
		String trainingSetDir = args[k++];
		String testSetDir = args[k++];
		int numbTestFiles = Integer.parseInt(args[k++]);
		String cuisinesFilePath = args[k++];
		
		// Read in the labeled data
		BufferedReader fileIn = new BufferedReader(new FileReader(trainingFile));

		Gson gson = new Gson();
		Recipe[] recipeArray = gson.fromJson(fileIn, Recipe[].class);
		
		// Perform Fisher-Yates Shuffle
		Random rnd = new Random();
		for(int i = 0; i < recipeArray.length; i++){
			int loc = rnd.nextInt(recipeArray.length - i); // Get the index to swap
			Recipe tempRecipe = recipeArray[recipeArray.length - 1 - i];
			recipeArray[recipeArray.length - 1 - i] = recipeArray[loc];
			recipeArray[loc] = tempRecipe;
		}
		
		// Process the ingredient in formation
		ArrayList<String> cuisines = new ArrayList<String>();	// This will be used to normalize the cuisine types to a string
		for(Recipe tempRecipe : recipeArray){
			// Clean ingredients
			String[] ingredientList = tempRecipe.getIngredients();	
			int m=0;
			for(int j = 0; j < ingredientList.length; j++ ){
				String filteredString = ingredientCleaner(ingredientList[j]);
				// Verify the filtered ingredient is not empty
				if(filteredString.length() > 0)
					ingredientList[j] = filteredString;
			}
			tempRecipe.updateIngredients(ingredientList);
			
			// Get the cuisine type
			if(!cuisines.contains(tempRecipe.getCuisine()))
				cuisines.add(tempRecipe.getCuisine());
		}
		
		// Sort the cuisines in alphabetical order
		Collections.sort(cuisines);
		printCuisineInfoToAFile(cuisines, cuisinesFilePath);
		
		
	}
	
	
	
	
	/**
	 * 
	 * This class is used as the basis for the GSON parser of the training dataset file.
	 * 
	 * @author Zayd
	 *
	 */
	public class Recipe{
		int id;
		String cuisine;
		String[] ingredients;
		
		public String[] getIngredients(){ return ingredients; }
		public void updateIngredients(String[] newIngredients){ ingredients = newIngredients; }; 
		public String getCuisine(){ return cuisine; };

	}
	
	
	public static String ingredientCleaner(String str){
		
		// Convert the string to lower case
		str = str.toLowerCase();
		
		String[] filteredPhrases = {",", "low sodium", "low fat", "low salt", "all purpose", "split and toasted",
									"lower sodium", "reduced fat", "in water", "less sodium", "extra firm", "reduced fat",
									"flat leaf", "firmly packed", "whole wheat", "reduced sodium" };
		// Remove any filtered phrases
		for(String word : filteredPhrases){
			str = str.replace(word, "");
		}
		
		String[] filteredWords = {"fresh", "freshly", "chopped", "plain", "powdered", "boneless", "skinless", "ground",
								  "warm", "extra-virgin",  "crushed", "finely", "fine", "large", "medium", 
								  "unsalted", "salted", "melted", "coarse", "1%", "2%", "low fat", "low-fat", "non-fat", 
								  "whole", "skimmed", "pods", "pod", "seeds", "seed", "all-purpose", 
								  "part-skim", "sliced", "grated", "minced", "diced", "cold", "reduced-fat",
								  "instant", "no-salt-added", "baby", "roasted", "halves", "softened", "soften",
								  "grated", "dried", "skimmed", "light", "dark", "russet", "wedges",
								  "shredded", "light", "frozen", "creamy", "pure", "cooked", "gluten-free", 
								  "waxy", "bottled", "unsweetened"};
		
		// Break the string into individual words
		String[] splitStr = str.split(" ");
		boolean[] keepWord = new boolean[splitStr.length];
		for(int cnt = 0; cnt < splitStr.length; cnt++){
			keepWord[cnt] = true;
			// Check the word against all the filter words
			for(String filter : filteredWords){
				if(splitStr[cnt].equals(filter)){
					keepWord[cnt] = false;
					break;
				}
			}
		}
		
		// Rebuild the ingredient string
		StringBuffer outputBuffer = new StringBuffer();
		boolean firstWord = true;
		for(int cnt = 0; cnt < splitStr.length; cnt++){
			if(keepWord[cnt]){
				// Add a space before each word except the first
				if(!firstWord) outputBuffer.append(" ");
				firstWord = false;
				
				outputBuffer.append(splitStr[cnt]);
			}
		}
		return outputBuffer.toString();
	}
		
	
	/**
	 * 
	 * This is function used to print the cuisine information to a dedicated file.
	 * 
	 * @param cuisines		- List<String> - All cuisine types in the file.  These are printed to a file.
	 * @param filePath		- String - Path to write the cuisineinfo to.
	 * @throws IOException
	 */
	public static void printCuisineInfoToAFile(List<String> cuisines, String filePath) throws IOException{
		
		BufferedWriter fileOut = new BufferedWriter(new FileWriter(filePath));
		
		for(int i = 0; i < cuisines.size(); i++){
			// Avoid an extra blank line at the end of the file.
			// Only put a blank line before the first entry.
			if(i != 0)
				fileOut.newLine();
			fileOut.write(cuisines.get(i) + "," + i);

		}
		fileOut.close(); // Close the file.
		
	}
}
