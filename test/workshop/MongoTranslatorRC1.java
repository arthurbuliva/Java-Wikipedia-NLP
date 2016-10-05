/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import MongoDB.MongoDB;
import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Keyword;
import nlp.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import opennlp.tools.tokenize.SimpleTokenizer;

public class MongoTranslatorRC1 extends TranslatorLogger implements EnglishStopWords, SwahiliStopWords
{

    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final MongoDB connection;
    private final HashMap<String, String> relationship;
    int retries = 1;
    private ArrayList<String> translation = new ArrayList<>();

    public MongoTranslatorRC1()
    {
        connection = new MongoDB();
        relationship = new HashMap<>();
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase("corpus");
    }

    public static void main(String[] args)
    {

//        String english = "Sin is disobeying God's will by not following commandments";
//        String english = "Barack Obama is Kenyatta National Hospital";
        String english = "The Skeleton";

        MongoTranslatorRC1 t = new MongoTranslatorRC1();

        System.out.println(t.translate(english));

    }

    public String translate(String original)
    {
        log(Level.INFO, String.format("Translating \"%s\"", new Object[]
        {
            original
        }));
        
        if(original.trim().isEmpty())
        {
            return translation.toString();
        }

        if (original.toLowerCase().startsWith("an "))
        {
            return translate(original.toLowerCase().replaceFirst("an", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("a "))
        {
            return translate(original.toLowerCase().replaceFirst("a", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("of"))
        {
            translation.add("ya ");
            return translate(original.toLowerCase().replaceFirst("of", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().trim().startsWith("is ") 
                || original.equalsIgnoreCase("is")
                || original.toLowerCase().trim().startsWith("is") )
        {
            translation.add("ni ");
            System.out.println(original.replaceFirst("is", "").trim());
            return translate(original.replaceFirst("is", "").trim());
        }
        else if (original.toLowerCase().startsWith("the"))
        {
            return translate(original.toLowerCase().replaceFirst("the", "").trim().toLowerCase());
        }
        else
        {
            // Sometimes the original word is just a title in Wikipedia
            TitleMatcher titleMatcher = new TitleMatcher();

            if ((titleMatcher.translate(original.trim()).replaceAll("\\[", "").replaceAll("\\]", "").length() > 0))
            {
                return (titleMatcher.translate(original.trim()));
            }
        }

        log(Level.INFO, "Searching MongoDB: " + original);

        HashMap<String, String> mongoData = connection.fetchFromMongoDB(String.format("\"%s\"", new Object[]
        {
            original
        }));

        if (mongoData.isEmpty()) // Whole phrase not in Wikipedia. We break it down
        {

            for (String segment : Chunker.chunk(original))
            {
                String subTranslation = translate(segment.trim());

                log(Level.INFO, "Empty segment. Translating -> " + (segment));

                if (subTranslation.isEmpty())
                {
                    System.out.println(removeStopWords(segment));

                    String segmentTranslation = translate(removeStopWords(segment));

                    if (!segmentTranslation.contains("|"))
                    {
                        translation.add(segmentTranslation);
                    }

                }
                else
                {
                    System.out.println(segment);

                    String segmentTranslation = translate(segment);

                    if (!segmentTranslation.contains("|"))
                    {
                        translation.add(segmentTranslation);
                    }
                }

                System.gc();
            }
        }
        else // Whole phrase exists in Wikipedia
        {
            for (Map.Entry<String, String> entry : mongoData.entrySet())
            {
                String[] englishSentences = SentenceDetector.detectSentences(entry.getKey().replaceAll("\\(.*?\\) ?", ""));
                String[] swahiliSentences = SentenceDetector.detectSentences(entry.getValue().replaceAll("\\(.*?\\) ?", ""));

                if (englishSentences[0].toLowerCase().trim().contains(original.toLowerCase().trim()))
                {
                    return swahiliSentences[0];
                }
                else
                {
                    for (String sentensi : swahiliSentences)
                    {
                        translation.add(sentensi);
                    }
                }
            }
        }

        HashMap<String, Integer> occurranceCount = new HashMap<>();

        for (String s : translation)
        {
            if (occurranceCount.keySet().contains(s))
            {
                int initialValue = occurranceCount.get(s);
                occurranceCount.put(s, initialValue + 1);
            }
            else
            {
                occurranceCount.put(s, 1);
            }
        }

//        System.out.println(ChunkFrequency.sortByValue(occurranceCount));
//        return translation.toString();
        return translation.toString() + ChunkFrequency.sortByValue(occurranceCount).toString();
    }

    private String removeStopWords(String sentence)
    {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] wordTokens = tokenizer.tokenize(sentence);

        StringBuilder clean = new StringBuilder();

        for (String token : wordTokens)
        {
            if (Arrays.asList(englishStopWords).contains(token.toLowerCase()))
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
