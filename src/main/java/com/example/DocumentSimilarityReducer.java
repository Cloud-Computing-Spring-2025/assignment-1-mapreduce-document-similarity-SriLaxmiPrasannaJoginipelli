package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {
    private Map<String, Set<String>> documentWords = new HashMap<>();

    @Override
protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
    Set<String> words = new HashSet<>();
    for (Text word : values) {
        words.add(word.toString());
    }
    documentWords.put(key.toString(), words);
    System.out.println("Reducer: Collected words for " + key.toString() + " -> " + words); // Debugging log
}

@Override
protected void cleanup(Context context) throws IOException, InterruptedException {
    System.out.println("Reducer: Starting similarity calculation"); // Debugging log
    List<String> docList = new ArrayList<>(documentWords.keySet());
    for (int i = 0; i < docList.size(); i++) {
        for (int j = i + 1; j < docList.size(); j++) {
            String doc1 = docList.get(i);
            String doc2 = docList.get(j);
            Set<String> words1 = documentWords.get(doc1);
            Set<String> words2 = documentWords.get(doc2);

            Set<String> intersection = new HashSet<>(words1);
            intersection.retainAll(words2);

            Set<String> union = new HashSet<>(words1);
            union.addAll(words2);

            double jaccardSimilarity = (double) intersection.size() / union.size();
            System.out.println("Reducer: Comparing " + doc1 + " and " + doc2 + " -> Similarity = " + jaccardSimilarity); // Debugging log

            if (jaccardSimilarity > 0.5) {
                context.write(new Text("(" + doc1 + ", " + doc2 + ")"),
                        new Text(String.format("%.2f%%", jaccardSimilarity * 100)));
            }
        }
    }
}
}