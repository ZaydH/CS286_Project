package zayd_hammoudeh;

import java.io.*;
import java.util.*;
import java.net.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

import com.google.gson.Gson;

public class Preprocessor {

	/**
	 * 
	 * @param args	
	 * 				- args[0] - Path to the full dataset
	 * 				- args[1] - Folder for outputting the training set data.
	 * 				- args[2] - Directory for outputting the test set data.
	 * 				- args[3] - Number of test set files.
	 * 				- args[4] - Folder for the cuisine type mapper
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
		String trainingSetDir = appendPathSlash(args[k++]); 
		String testSetDir = appendPathSlash(args[k++]); 
		int numbTestFiles = Integer.parseInt(args[k++]);
		String cuisinesDir = appendPathSlash(args[k++]);
		
		// Read in the labeled data
		Path pt = new Path(trainingFile);
		FileSystem fs = FileSystem.get(new Configuration());
		BufferedReader fileIn = new BufferedReader(new InputStreamReader(fs.open(pt)));

		Gson gson = new Gson();
		Recipe[] recipeArr = gson.fromJson(fileIn, Recipe[].class);
		
		// Perform Fisher-Yates Shuffle
		Random rnd = new Random();
		for(int i = 0; i < recipeArr.length; i++){
			int loc = rnd.nextInt(recipeArr.length - i); // Get the index to swap
			Recipe tempRecipe = recipeArr[recipeArr.length - 1 - i];
			recipeArr[recipeArr.length - 1 - i] = recipeArr[loc];
			recipeArr[loc] = tempRecipe;
		}
		
		// Process the ingredient in formation
		Hashtable<String, Integer> ingredientIDs = new Hashtable<String, Integer>();
		ArrayList<String> cuisines = new ArrayList<String>();	// This will be used to normalize the cuisine types to a string
		for(Recipe tempRecipe : recipeArr){
			// Clean ingredients
			String[] ingredientList = tempRecipe.getIngredients();	
			for(int j = 0; j < ingredientList.length; j++ ){
				String filteredString = ingredientCleaner(ingredientList[j]);
				
				// Verify the filtered ingredient is not empty
				if(filteredString.length() > 0)
					ingredientList[j] = filteredString;
				
				// Check if the ingredient has an ID number. if not assign it the next open ID
				if(ingredientIDs.get(ingredientList[j]) == null){
					Integer id = ingredientIDs.size();
					ingredientIDs.put(ingredientList[j], id);
				}
			}
			tempRecipe.updateIngredients(ingredientList);
			
			// Get the cuisine type
			if(!cuisines.contains(tempRecipe.getCuisine()))
				cuisines.add(tempRecipe.getCuisine());
		}
		
		// Sort the cuisines in alphabetical order
		Collections.sort(cuisines);
		printCuisineInfoToAFile(cuisinesDir + "cuisines.txt", cuisines);
		
		// Print the training set data
		int trainingSetSize = (int)(2.0 / 3 * recipeArr.length);
		printRecipesToFile(trainingSetDir + "training_set.txt", recipeArr, 0, trainingSetSize, ingredientIDs, cuisines);
		
		// Calculate the number of records per file
		int recordsPerFile = (recipeArr.length - trainingSetSize) / numbTestFiles;
		

		// Output the test set information
		for(int i = 0; i < numbTestFiles; i++){
		
			
			// Add preceding zeros to the file number
			String fileNumber = ("00000" + i).substring( Integer.toString(i).length());
			String filePath = testSetDir + "test_set_" + fileNumber + ".txt";
			
			// Will need to handle the last file differently since it will need to include the last record. 
			if(i != numbTestFiles - 1)
				printRecipesToFile(filePath, recipeArr, trainingSetSize + i*recordsPerFile, 
								   trainingSetSize + (i+1)*recordsPerFile, ingredientIDs, cuisines);
			else
				printRecipesToFile(filePath, recipeArr, trainingSetSize + i*recordsPerFile, 
								   recipeArr.length, ingredientIDs, cuisines);
		}
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
	public static void printCuisineInfoToAFile(String filePath, List<String> cuisines ) throws IOException{
		
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
	
	
	/**
	 * 
	 * Used to print the recipe information to a file.
	 * 
	 * @param filePath			String - Path to print the file to.
	 * @param recipes			Recipe[] - Array of Recipes
	 * @param startLoc			int - Inclusive first index of the recipes array to print to the file
	 * @param endLoc			int - Exclsuive last index of the recipes array to print to the file
	 * @param ingredientIDs		Map<String, Integer> - Converts String objects (i.e. ingredients) to integers
	 * @param cuisineIDs		List<String> Array of the cuisine type names
	 * @throws IOException
	 */
	public static void printRecipesToFile(String filePath, Recipe[] recipes, int startLoc, int endLoc, 
										  Map<String, Integer>ingredientIDs, List<String> cuisineIDs) throws IOException{
		
		BufferedWriter fileOut = new BufferedWriter(new FileWriter(filePath));
		
		// Print the dataset information settings at the top of the file.
		fileOut.write(cuisineIDs.size() + "," + ingredientIDs.size());
		
		// Print one recipe at a time.
		for(int i = startLoc; i < endLoc; i++){
			// Put a blank line before new lines to prevent an empty blank line at the of the file.
			fileOut.newLine();
			StringBuffer sb = new StringBuffer();
			
			// Extract the temporary recipe
			Recipe tempRecipe = recipes[i];
			
			// Build the output string
			sb.append( tempRecipe.id + "," + cuisineIDs.indexOf(tempRecipe.getCuisine()) ); // Add the recipe and cuisine ID numbers 
			
			// Append the ingredient list
			for(String ingredient : tempRecipe.getIngredients())
				sb.append("," + ingredientIDs.get(ingredient));
		
			// Write the string to a file.
			fileOut.write(sb.toString());
		}
		fileOut.close(); // Close the file.
		
	}
	
	public static String appendPathSlash(String dirPath){
		
		// Verify the directory contains a terminating slash if applicable
		if( !dirPath.endsWith("\\") && !dirPath.endsWith("/") ){
			if(dirPath.contains("\\"))
				dirPath = dirPath.concat("\\");
			else if(dirPath.contains("/"))
				dirPath = dirPath.concat("/");
		}
		
		return dirPath;
	}
}
