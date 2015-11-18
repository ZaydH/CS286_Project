

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;


public class NaiveBayesSeqTester 
{
	final static Integer CUISINE_TOTAL = 20;
	final static Integer INGR_TOTAL = 5926;
	
//	final static Integer CUISINE_TOTAL = 2;
//	final static Integer INGR_TOTAL = 6;
	
	static ArrayList<Double[]> logPosteriors;
	static Double[] priors = null;
	static Double[][] likelihood = null;
//	static Integer[] ingrCountForCuisine = null;

	public static void main(String[] args)
	{
		listFilesForFolder(new File(args[0]));
		getModel(args[1]);
		
		//Test files and write the output in one output file
		logPosteriors = new ArrayList<Double[]>();
		testNaiveBayesClassifier(new File(args[0]), args[0], args[2]);
		
		//GET POSTERIORS
		//Double[][] logPosteriors = NaiveBayesClassifier.computePosteriors(testSet, priors, likelihoods);
	}
	
	public static void getModel(String modelPath)
	{
		priors = new Double[CUISINE_TOTAL];
		likelihood = new Double[CUISINE_TOTAL][INGR_TOTAL];
		try {
			BufferedReader training = new BufferedReader(new FileReader(modelPath));
			String thisLine = "";
			
			//MODEL
			while ((thisLine = training.readLine()) != null)
			{
				String[] tokens = thisLine.split("\\s+");
				int cuisineId = Integer.parseInt(tokens[0]);
				priors[cuisineId] = Double.parseDouble(tokens[1]);
				for (int i = 2; i < tokens.length; i++)
				{
					int col = i-2;
					Double ingrProb = Double.parseDouble(tokens[i]);
					likelihood[cuisineId][col] = ingrProb;
				}
			}
			training.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e);
		}
		
	}
	
	public static void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            System.out.println(fileEntry.getName());
	        }
	    }
	}
	
	public static void testNaiveBayesClassifier(final File folder, String testInputDir, String outFile)
	{
		BufferedWriter bw = null;
		try 
		{
			File out = new File(outFile);
			if (!out.exists())
			{
				out.createNewFile();
			}
			FileWriter fw = new FileWriter(out.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			System.out.println("THE END");
		} catch (Exception e)
		{
			System.out.println("Error while opening an output file "+e.getMessage());
		}
		
		for(final File fileEntry : folder.listFiles())
		{
			
			System.out.println("Test file for classifier");
			ArrayList<Integer> recipeIngr = null;
			Integer recipeId = null;
			Integer realCuisineId = null;
			//TESTING SET
			try
			{
				BufferedReader testing = new BufferedReader(new FileReader(testInputDir+"/"+fileEntry.getName()));
				String thisLine = "";
				int count = 0;
				while ((thisLine = testing.readLine()) != null)
				{
					if (count == 0)
					{
						count++;
						continue;
					}
					recipeIngr = new ArrayList<Integer>();
					String[] tokens = thisLine.split(",");
					recipeId = Integer.parseInt(tokens[0]);
					realCuisineId = Integer.parseInt(tokens[1]);
					
					for (int i = 2; i < tokens.length; i++)
					{
						recipeIngr.add(Integer.parseInt(tokens[i]));
					}
				}	
				testing.close();
			}
			catch (Exception e) 
			{
				System.out.println("Error in file read"+e);
			}
			
			//GET POSTERIORS
			Double[] logPosteriors = NaiveBayesClassifier.computePosterior(recipeIngr, priors, likelihood);
			String s = "";
			s = recipeId + " " + realCuisineId + " " + NaiveBayesReducer.prettyPrintArray(logPosteriors);
			try {
				bw.write(s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
