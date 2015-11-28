package KNN_YashiKamboj;

import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.FloatWritable;
import java.io.IOException;

public class KNNReducer extends Reducer <Text,Text,Text,Text> {

    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
    String val=null;    
    for(Text value: values) {
            val = value.toString();
        }
        context.write(key, new Text(val));
    }
}

