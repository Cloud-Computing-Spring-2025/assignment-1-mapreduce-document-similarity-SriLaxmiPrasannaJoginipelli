package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.StringTokenizer;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, Text> {
    private Text docId = new Text();
    private Text word = new Text();

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        
        String[] parts = value.toString().split("\t", 2);
        if (parts.length == 2) {
            docId.set(parts[0]); // Document ID
            StringTokenizer tokenizer = new StringTokenizer(parts[1]); 
            while (tokenizer.hasMoreTokens()) {
                word.set(tokenizer.nextToken()); 
                context.write(docId, word);
            }
        }
    }
}