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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    int counter = 1;

    public AlphaMongoTranslator()
    {
        relationship = new HashMap<>();
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase("corpus");

    }

    public static void main(String[] args)
    {
        String english = "I have a headache";
//        String english = "I have a headache";

        AlphaMongoTranslator t = new AlphaMongoTranslator();

        System.out.println(t.translate(english));
    }

    public String translate(String original)
    {
        log(Level.INFO, String.format("Translating \"%s\"", new Object[]
        {
            original
        }));

        ArrayList<String> translation = new ArrayList<>();

        // Sometimes the original word is just a title in Wikipedia
        TitleMatcher titleMatcher = new TitleMatcher();

        if ((titleMatcher.translate(original.trim()).replaceAll("\\[", "").replaceAll("\\]", "").length() > 0))
        {
            return (titleMatcher.translate(original.trim()));
        }

        // Get the key words
        AlchemyLanguage service = new AlchemyLanguage();
        service.setApiKey("e24397903a386ad615e7922ed5907557e76bb336");

        Map<String, Object> params = new HashMap<>();
        params.put(AlchemyLanguage.TEXT, original);

        List<Keyword> keyWords = service.getKeywords(params).execute().getKeywords();

        System.out.println("Key words: " + keyWords);

        Map<String, String> tokens = Chunker.getSpanTypes(original);

        System.out.println("Tokens: " + tokens);

//        // Go through the tokens one by one
//        for (Map.Entry<String, String> entry : tokens.entrySet())
//        {
//            String key = entry.getKey().trim();
////            String value = entry.getValue();
//
//            original += " \"" + key.trim() + "\" ";
//        }
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

                String swahili = document.getString("sw");

                translation.add(swahili);
            }
        }

        Map<String, Integer> probabilities = new HashMap<>();

        // Go through the tokens one by one
        for (Map.Entry<String, String> entry : tokens.entrySet())
        {
            String key = entry.getKey().trim();
            String value = entry.getValue();

            System.out.println("=================" + key + "=================");

            // Remove stop words from the key
            ArrayList<String> stopWords = Chunker.chunk(key);

            SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
            String[] keyTokens = tokenizer.tokenize(key);
            
            

            System.exit(0);

            // Loop through the documents
            for (String sentence : translation)
            {
                if (sentence.toLowerCase().contains(key.toLowerCase()))
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

        return probabilities.toString();
    }
}
