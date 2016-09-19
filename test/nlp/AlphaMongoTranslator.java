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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.apache.commons.collections4.CollectionUtils;

import org.bson.Document;

public class AlphaMongoTranslator extends TranslatorLogger implements EnglishStopWords, SwahiliStopWords
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
        String english = "He has left for home";

        StringBuilder finalTranslation = new StringBuilder();

        AlphaMongoTranslator t = new AlphaMongoTranslator();

        // Step 1: Search entire string in MongoDB
        String wholeTranslation = t.translateWhole(english);

        if (wholeTranslation.isEmpty() || wholeTranslation.equalsIgnoreCase("[]"))
        {
            // If step 1 does not find the whole string, then chunk it
            Map<String, String> tokens = Chunker.getSpanTypes(english);

            for (Map.Entry<String, String> entry : tokens.entrySet())
            {
                String key = entry.getKey().trim();
                String value = entry.getValue();

                System.out.println(key);
                
                String keyTranslation = t.translateWhole(key);

                if (keyTranslation.isEmpty() || keyTranslation.equalsIgnoreCase("[]"))
                {
                    log(Level.INFO, "No exact match for: " + key);
                    finalTranslation.append(t.translate(key)).append(" ");
                }
                else
                {
                    log(Level.INFO, "No data for: " + key);
                    finalTranslation.append(t.translate(t.removeEnglishStopWords(key)));
                }
                
//                System.out.println(" = " + keyTranslation);

            }

//            System.out.println(finalTranslation);
//            System.out.println(t.translate(english));
        }
        else
        {
            finalTranslation.append(wholeTranslation);
//            System.out.println(wholeTranslation);
        }

        System.out.println("===============================");
        System.out.println(english);
        System.out.println(finalTranslation.toString());
        System.out.println("===============================");

    }
    
    public String reverseTranslate()
    {
        return null;
    }

    public String translate(String original)
    {
        log(Level.INFO, String.format("Translating \"%s\"", new Object[]
        {
            original
        }));

        this.original = original;
        
        if(original.equalsIgnoreCase("is") || original.equalsIgnoreCase("am"))
        {
            return " ni ";
        }
        if(original.equalsIgnoreCase("a"))
        {
            return " ";
        }
        if(original.equalsIgnoreCase("this"))
        {
            return " hii ";
        }
        if(original.equalsIgnoreCase("I"))
        {
            return " mimi ";
        }
        if(original.equalsIgnoreCase("have"))
        {
            return " nina ";
        }

        ArrayList<String> translation = new ArrayList<>();

        // Get the key words
        AlchemyLanguage service = new AlchemyLanguage();
        service.setApiKey("e24397903a386ad615e7922ed5907557e76bb336");

        Map<String, Object> params = new HashMap<>();
        params.put(AlchemyLanguage.TEXT, original);

        List<Keyword> keyWords = null;
        
        try
        {
        keyWords = service.getKeywords(params).execute().getKeywords();
        }
        catch (Exception ex)
        {
            log(Level.SEVERE, "Fetching from Alchemy: " + params);
        }

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

                translation.add(removeSwahiliStopWords(swahili));
//                translation.add(swahili);
            }
        }

        Map<String, Integer> probabilities = new HashMap<>();

        Lemmatizer lemmatizer = new Lemmatizer();

        // Go through the tokens one by one
        for (Map.Entry<String, String> entry : tokens.entrySet())
        {
            String key = entry.getKey().trim();
            String value = entry.getValue();
            
            log(Level.INFO, "Token: " + key);

            // Loop through the documents
            for (String sentence : translation)
            {
//                System.out.println(lemmatizer.lemmatize(key, value));
                if (sentence.toLowerCase().contains(removeEnglishStopWords(key).toLowerCase()))
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

        return getTopWords(probabilities).toString();
//        return probabilities.toString();

        //TODO: Go through the phrases in sw and see if the en equivalents have the phrase we need
    }

    public String translateWhole(String original)
    {
        log(Level.INFO, String.format("Translating \"%s\"", new Object[]
        {
            original
        }));

        ArrayList<String> translation = new ArrayList<>();

        Map<String, Integer> frequencies = new HashMap<>();

        Document projection = new Document("score", new Document("$meta", "textScore"));

        try (
                MongoCursor<Document> cursor = db.getCollection("wikipedia")
                .find(new Document("$text",
                        new Document("$search", String.format("\"%s\"", original))
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
                if (original.trim().equalsIgnoreCase(englishTitle))
                {
                    return swahiliTitle;
                }

//                ============
                SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
                String[] wordTokens = tokenizer.tokenize(removeSwahiliStopWords(swahili));

                StringBuilder clean = new StringBuilder();

                for (String token : wordTokens)
                {
                    if (frequencies.containsKey(token))
                    {
                        int currentValue = frequencies.get(token);

                        frequencies.put(token, currentValue + 1);
                    }
                    else
                    {
                        frequencies.put(token, 1);
                    }

                }
//                        ====================

//                System.out.println(frequencies);
                translation.add(swahili);
            }
        }

//        return translation.toString();
//        return frequencies.toString();
        return getTopWords(frequencies).toString();
    }

    public List getTopWords(Map<String, Integer> hashMap)
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
            if (loopCounter == 5)
            {
                break;
            }

            loopCounter++;
        }

        return topTen;
    }

    @Deprecated
    private String removeEnglishStopWords(String sentence)
    {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] wordTokens = tokenizer.tokenize(sentence);

        StringBuilder clean = new StringBuilder();

        for (String token : wordTokens)
        {
            if (Arrays.asList(englishStopWords).contains(token.toLowerCase()))
            {
//                System.out.println("Keyword found: " + token);
            }
            else
            {
                clean.append(token).append(" ");
            }
        }

//        System.out.printf("%s cleaned to %s\n", sentence, clean.toString().trim());
        return clean.toString().trim();
    }

    private String removeSwahiliStopWords(String sentence)
    {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] wordTokens = tokenizer.tokenize(sentence);

        StringBuilder clean = new StringBuilder();

        for (String token : wordTokens)
        {
            if (Arrays.asList(swahiliStopWords).contains(token))
            {
//                System.out.println("Keyword found: " + token);
            }
            else
            {
                clean.append(token).append(" ");
            }
        }

//        System.out.printf("%s cleaned to %s\n", sentence, clean.toString().trim());
        return clean.toString().trim();
    }
}
