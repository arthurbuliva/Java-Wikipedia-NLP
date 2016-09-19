/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Keyword;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.apache.commons.collections4.CollectionUtils;

import org.bson.Document;

public class AlphaMongoTranslator extends TranslatorLogger implements StopWords
{

    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final HashMap<String, String> relationship;
    private String original;
    int counter = 1;

    public AlphaMongoTranslator()
    {
        relationship = new HashMap<>();
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase("corpus");
        original = "";
    }

    public static void main(String[] args)
    {
//        String english = "I feel sick";
        String english = "It is Christmas today";

        AlphaMongoTranslator t = new AlphaMongoTranslator();

        System.out.println(t.translate(english));
    }

    public String translate(String original)
    {
        log(Level.INFO, String.format("Translating \"%s\"", new Object[]
        {
            original
        }));
        
        this.original = original;

        ArrayList<String> translation = new ArrayList<>();

        // Get the key words
        AlchemyLanguage service = new AlchemyLanguage();
        service.setApiKey("e24397903a386ad615e7922ed5907557e76bb336");

        Map<String, Object> params = new HashMap<>();
        params.put(AlchemyLanguage.TEXT, original);

        List<Keyword> keyWords = service.getKeywords(params).execute().getKeywords();

        System.out.println("Key words: " + keyWords);

        Map<String, String> tokens = Chunker.getSpanTypes(original);

        System.out.println("Tokens: " + tokens);

        for (Keyword keyWord : keyWords)
        {
            original += " \"" + keyWord.getText().trim() + "\"";
        }

        System.out.println("Searching MongoDB: " + original);

        Document projection = new Document("score", new Document("$meta", "textScore"));

        try (
                MongoCursor<Document> cursor = db.getCollection("wikipedia")
                .find(new Document("$text",
                        new Document("$search", String.format("%s", original))
                        .append("$language", "en")))
                .projection(projection)
                .sort(projection)
                .limit(0)
                .iterator())
        {
            while (cursor.hasNext())
            {
                Document document = cursor.next();

                String swahiliTitle = document.getString("kichwa");
                String swahili = document.getString("sw");

                String englishTitle = document.getString("title");
                String english = document.getString("en");

                // Sometimes the original word is just a title in Wikipedia
                if (this.original.trim().equalsIgnoreCase(englishTitle))
                {
                    return swahiliTitle;
                }

                translation.add(swahili);
            }
        }

        Map<String, Integer> probabilities = new HashMap<>();

        Lemmatizer lemmatizer = new Lemmatizer();

        // Go through the tokens one by one
        for (Map.Entry<String, String> entry : tokens.entrySet())
        {
            String key = entry.getKey().trim();
            String value = entry.getValue();

            System.out.println("=================" + key + "=================");

            // Loop through the documents
            for (String sentence : translation)
            {
//                System.out.println(lemmatizer.lemmatize(key, value));
                if (sentence.toLowerCase().contains(removeStopWords(key).toLowerCase()))
                {
                    Map<String, Integer> newProbabilities = (ChunkFrequency.getFrequencies(sentence));

                    if (probabilities.isEmpty())
                    {
                        probabilities = newProbabilities;
                    }
                    else
                    {
                        Set<String> oldKeySet = probabilities.keySet();
                        Set<String> newKeySet = newProbabilities.keySet();

                        Collection intersection
                                = CollectionUtils.intersection(oldKeySet, newKeySet);

                        if (intersection.isEmpty())
                        {
                        }
                        else
                        {
                            for (Object intersectingObject : intersection)
                            {
                                int newValue = (probabilities.get(intersectingObject));
                                newValue += (newProbabilities.get(intersectingObject));

                                probabilities.put((String) intersectingObject, newValue);

                                newProbabilities.remove(intersectingObject);
                            }

                            probabilities.putAll(newProbabilities);
                        }
                    }
                }
            }
        }

        String topWords = getTopWords(probabilities);

        return topWords;
//        return probabilities.toString();

        //TODO: Go through the phrases in sw and see if the en equivalents have the phrase we need
    }

    public String getTopWords(Map<String, Integer> hashMap)
    {
        Set<Entry<String, Integer>> set = hashMap.entrySet();
        List<Entry<String, Integer>> topTen = new ArrayList<>(set);

        Collections.sort(
                topTen,
                (Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2)
                -> (o2.getValue())
                .compareTo(o1.getValue())
        );

        int loopCounter = 0;

        for (Map.Entry<String, Integer> topEntry : topTen)
        {
//                    
//                    System.out.print(topEntry.getKey() + " : " + topEntry.getValue() + "\t");

            if (loopCounter == 5)
            {
                break;
            }

            loopCounter++;
        }

        return topTen.toString();
    }

    private String removeStopWords(String sentence)
    {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] wordTokens = tokenizer.tokenize(sentence);

        StringBuilder clean = new StringBuilder();

        for (String token : wordTokens)
        {
            if (Arrays.asList(stopWords).contains(token))
            {
//                    System.out.println("Keyword found: "+ token);
            }
            else
            {
                clean.append(token).append(" ");
            }
        }

//            System.out.printf("%s cleaned to %s\n", sentence, clean.toString().trim());
        return clean.toString().trim();
    }
}
