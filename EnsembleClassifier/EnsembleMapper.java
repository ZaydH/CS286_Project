package Iris;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;

import java.io.IOException;
import java.util.StringTokenizer;

public class IrisMapper extends Mapper <LongWritable,Text,Text,Text> {
	
	private static final boolean USE_STRING_TOKENIZER = false;
	
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    
		if(!USE_STRING_TOKENIZER){
			// TODO create array of string tokens from record assuming space-separated fields using split() method of String class
			String[] tempString = value.toString().split("\\s+");
			
			// TODO pull out sepal length from columns var
			String sepalLength = tempString[0];
	
			//  TODO pull out sepal width from columns var
			String sepalWidth = tempString[1];
			
			// TODO pull out petal length from columns var
			String petalLength = tempString[2];
			
			// TODO pull out petal width from columns var
			String petalWidth = tempString[3];
	
			// TODO pull out flower id from columns var
			String flowerID = tempString[4];
			// TODO write output to context as key-value pair where key is 
			// flowerId and value is underscore-separated concatenation of 
			// sepal/petal length/widths
			String outputValue =   sepalLength + "_" + sepalWidth + "_"
								 + petalLength + "_" + petalWidth;
			context.write(new Text(flowerID), new Text(outputValue));
		}
		else{
			// TODO create array of string tokens from record assuming space-separated fields using split() method of String class
			StringTokenizer token = new StringTokenizer(value.toString(), "\t");
			String[] stringPieces = new String[4];
			StringBuilder strBuild = new StringBuilder();
			int cnt;
			
			// Get the tokens and construct the output string.
			for(cnt = 0; cnt < stringPieces.length; cnt++){
				if(cnt!=0) strBuild.append("_");
				stringPieces[cnt] = token.nextToken();
				strBuild.append(stringPieces[cnt]);
			}
			// TODO pull out flower id from columns var
			String flowerID = token.nextToken();
			
			// TODO write output to context as key-value pair where key is 
			context.write(new Text(flowerID), new Text(strBuild.toString()));
		}
   }
}
