package KNN_YashiKamboj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class KNNMapper extends Mapper <LongWritable,Text,Text,Text> {
	int kNeighbors,totalCuisines,totalIngredients;
	//object of interface
	DistanceMetric calc; 
	// for strong training data
	ArrayList<ArrayList<Integer>> trainingDataSet = new ArrayList<ArrayList<Integer>>();
        Hashtable<Integer, int[]> ingredientHashTable = new Hashtable<Integer, int[]>();	
	
	protected void setup(Context context) throws IOException, InterruptedIOException
	{
	
	    int[] cuisinesForOneTrainingIngredient;
		
	    // Extract the value of K
	    kNeighbors = Integer.parseInt(context.getConfiguration().get("K"));
	    //Extract the training file path
	    String trainingFilePath = context.getConfiguration().get("trainingFile");
            // Distance Metric
	    String distanceMetric = context.getConfiguration().get("distanceMetric");
		
	    //Initializing the distance metric object depending on the metric
	    if(distanceMetric.equals("MVDM"))
		calc = new MVDM();
	    else if(distanceMetric.equals("Overlap"))
		calc = new Overlap();
			
	    //Reading the training file
	    try {
                Path training = new Path(trainingFilePath);
                FileSystem fs = FileSystem.get(new Configuration());
                BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(training)));

                //Reading the first line of training data text file
                String infoInFirstLine = br.readLine();
                String[] values= infoInFirstLine.split(",");
                totalCuisines = Integer.parseInt(values[0]);
                totalIngredients = Integer.parseInt(values[1]);
		String currentLine;
		//Reading the rest part of the file
		while((currentLine=br.readLine())!=null){
		    String[] temp = currentLine.split(",");
		    Integer[] ingredientsForOneTrainingData = new Integer[temp.length];
		    //Entering the data into trainingDataSet in the form of ArrayList of ArrayList
		    for(int i=0;i<temp.length;i++){
	            	ingredientsForOneTrainingData[i]=Integer.valueOf(Integer.parseInt(temp[i]));
	            }
		    List<Integer> s = Arrays.asList(ingredientsForOneTrainingData);
	            ArrayList<Integer> one =  new ArrayList<Integer>(s);
	            trainingDataSet.add(one);
		    if(distanceMetric.equals("MVDM")){
		        int cuisineName = ingredientsForOneTrainingData[1];
			for(int i=2; i<ingredientsForOneTrainingData.length;i++){
				cuisinesForOneTrainingIngredient = new int[totalCuisines];
				int currentIngredient = ingredientsForOneTrainingData[i];	
				//initializing the cuisineCounterArray
				for(int k=0;k<totalCuisines;k++){
					cuisinesForOneTrainingIngredient[k]=0;
				}
				if(ingredientHashTable.containsKey(currentIngredient)){
					cuisinesForOneTrainingIngredient = ingredientHashTable.get(currentIngredient);
					cuisinesForOneTrainingIngredient[cuisineName]= cuisinesForOneTrainingIngredient[cuisineName]+1;
				}	    
				else{
					cuisinesForOneTrainingIngredient[cuisineName]=cuisinesForOneTrainingIngredient[cuisineName]+1;
				}
				ingredientHashTable.put(currentIngredient, cuisinesForOneTrainingIngredient);
	            	}
		    }//end of putting data into hashmap for distance metric MVDM
		}//while loop for reading file ends here
		br.close();	
		}catch(IOException e){
			System.out.println(e);
		}
    }
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    	//getting the test data from value into an ArrayList<Integer>
	String[] testValues = value.toString().split(",");
	Integer[] testIngredients = new Integer[(testValues.length)];
	//arraylist for storing test data values
	ArrayList<Integer> testDataIngredients;
	for(int i=0;i<testValues.length;i++){
		testIngredients[i]=Integer.valueOf(Integer.parseInt(testValues[i]));
        }
	List<Integer> test = Arrays.asList(testIngredients);
	testDataIngredients =  new ArrayList<Integer>(test);
	//for storing the results
	ArrayList<DistanceResultsWrapper> knnResults = new ArrayList<DistanceResultsWrapper>();
	for(ArrayList<Integer> trainingIngredientsPerRecipe : trainingDataSet) {
		if(trainingIngredientsPerRecipe.size()<6) continue; //ignoring recipes with less than 4 ingredients
		double distance = calc.dist(testDataIngredients,trainingIngredientsPerRecipe);
		//Adding the results
		knnResults.add(new DistanceResultsWrapper(distance,trainingIngredientsPerRecipe.get(1)));
	}
	Collections.sort(knnResults);
        //Voting to get the closest k neighbors
	int[] votingArray = new int[totalCuisines];
	//initializing to 0
	for(int i=0;i<votingArray.length;i++) {
		votingArray[i]=0;
	}
	for(int i=0;i<kNeighbors;i++){
		votingArray[knnResults.get(i).getCuisineNumb()]++;
	}
	//MapReduce Output
	StringBuffer val = new StringBuffer(testDataIngredients.get(0)+","+testDataIngredients.get(1));
	for(int i=0;i<totalCuisines;i++) {
		val.append(","+votingArray[i]);
	}
	context.write(new Text(Integer.toString(testDataIngredients.get(0))),new Text(val.toString()));	    
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
    interface DistanceMetric {
        double dist(ArrayList<Integer> r1, ArrayList<Integer> r2);
    }
    class MVDM implements DistanceMetric {
    	public double dist(ArrayList<Integer> testData, ArrayList<Integer> trainingData) {
		int validIngredientPairs=0;
		double totalDistSum = 0;
		for(int i=2;i<testData.size();i++) {
			int testIngredient = testData.get(i);
			int[] testIngredientFromHashTable = ingredientHashTable.get(testIngredient);
			//checking if test ingredient is present in hash table or not
			if(testIngredientFromHashTable==null) continue;
			for(int j=2;j<trainingData.size();j++) {
				int trainingIngredient = trainingData.get(j);
				int[] trainingIngredientFromHashTable = ingredientHashTable.get(trainingIngredient);
				if(trainingIngredientFromHashTable==null) continue;
				validIngredientPairs++;
				String distKey = testIngredient+"_"+trainingIngredient;
				// Recalculate the number of times each recipe has been used
				int testIngredientSum = 0, trainIngredientSum = 0;
				for(int k=0;k<totalCuisines;k++){
					testIngredientSum += testIngredientFromHashTable[k];
					trainIngredientSum += trainingIngredientFromHashTable[k];
				}
					//inter-recipe distance
					double result=0;
				for(int l=0;l<totalCuisines;l++) {
					int n1m = testIngredientFromHashTable[l];
					double firsthalf=(1.0*n1m)/testIngredientSum;
					int n2m = trainingIngredientFromHashTable[l];
					double secondhalf =(1.0*n2m)/trainIngredientSum;
					result = result + (double) Math.abs(firsthalf-secondhalf);
				}
				totalDistSum=result;
			}//for all training data ingredients		
		}//for all test data ingredients
		return totalDistSum / validIngredientPairs;
	}	
    }
    class Overlap implements DistanceMetric {
	public double dist(ArrayList<Integer> testData, ArrayList<Integer> trainingData) {
		
            double distance=0;
            //to obtain only the common elements of both ArrayLists in result ArrayList
            ArrayList<Integer> testSublistData= new ArrayList<Integer>(testData.subList(2, testData.size()));
	    ArrayList<Integer> trainSublistData= new ArrayList<Integer>(trainingData.subList(2, trainingData.size()));
	    ArrayList<Integer> result = new ArrayList<Integer>(testSublistData);
            result.retainAll(trainSublistData);
	    int size = Math.abs(result.size());
            distance = -1.0 * size/((testData.size()-2)+(trainingData.size()-2));
            return distance;
        }	
    }
}
