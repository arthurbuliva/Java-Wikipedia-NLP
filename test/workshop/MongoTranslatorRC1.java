/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import MongoDB.TitleTranslator;
import MongoDB.MongoDB;
import nlp.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        String english = "Sexually transmitted disease";

        MongoTranslatorRC1 t = new MongoTranslatorRC1();

        System.out.println(t.translate(english));

    }

    public String translate(String original)
    {
        log(Level.INFO, String.format("Translating \"%s\"", new Object[]
        {
            original
        }));

        if (original.trim().isEmpty())
        {
            return translation.toString();
        }

        if (original.toLowerCase().startsWith("an ") || original.equalsIgnoreCase("an"))
        {
            return translate(original.toLowerCase().replaceFirst("an", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("a ") || original.equalsIgnoreCase("a"))
        {
            return translate(original.toLowerCase().replaceFirst("a", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("of ") || original.equalsIgnoreCase("of"))
        {
            translation.add("ya ");
            return translate(original.toLowerCase().replaceFirst("of", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("is ") || original.equalsIgnoreCase("is"))
        {
            translation.add("ni ");
            return translate(original.replaceFirst("is", "").trim());
        }
        else if (original.toLowerCase().startsWith("the ") || original.equalsIgnoreCase("the"))
        {
            return translate(original.toLowerCase().replaceFirst("the", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("had ") || original.equalsIgnoreCase("had"))
        {
            translation.add("na ");
            return translate(original.toLowerCase().replaceFirst("had", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("has ") || original.equalsIgnoreCase("has"))
        {
            translation.add("na ");
            return translate(original.toLowerCase().replaceFirst("has", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("from ") || original.equalsIgnoreCase("from"))
        {
            translation.add("kutoka ");
            return translate(original.toLowerCase().replaceFirst("from", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("and ") || original.equalsIgnoreCase("and"))
        {
            translation.add("na ");
            return translate(original.toLowerCase().replaceFirst("and", "").trim().toLowerCase());
        }
        else
        {
            // Sometimes the original word is just a title in Wikipedia
            TitleTranslator titleMatcher = new TitleTranslator();

            if ((titleMatcher.translate(original.trim()).replaceAll("\\[", "").replaceAll("\\]", "").length() > 0))
            {
                return (titleMatcher.translate(original.trim()));
            }
        }

        log(Level.INFO, "Searching MongoDB: " + original);

        System.out.println("Searching MongoDB: " + original);

        HashMap<String, String> mongoData = connection.fetchFromMongoDB(String.format("\"%s\"", new Object[]
        {
            original
        }));

        if (mongoData.isEmpty()) // Whole phrase not in Wikipedia. We break it down
        {
            log(Level.INFO, "Whole phrase not in Wikipedia. Breaking phrase into segments");

            ArrayList<String> segments = Chunker.chunk(original);

            System.out.println("Segments: " + segments);

            if (segments.isEmpty() || segments.get(0).trim().equalsIgnoreCase(original.trim()))
            {
                SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
                String[] wordTokens = tokenizer.tokenize(original.trim());

                log(Level.INFO, "Tokenized to " + Arrays.toString(wordTokens));

                for (String token : wordTokens)
                {
                    translation.add(translate(token));
                }
            }
            else
            {
                for (String segment : segments)
                {
                    String subTranslation = translate(segment.trim());

                    log(Level.INFO, "Empty segment. Translating -> " + (segment));

                    if (subTranslation.isEmpty())
                    {
                        String segmentTranslation = translate(removeStopWords(segment));

                        if (!segmentTranslation.contains("|"))
                        {
                            translation.add(segmentTranslation);
                        }
                    }
                    else
                    {
                        String segmentTranslation = translate(segment.trim());

                        if (!segmentTranslation.contains("|"))
                        {
                            translation.add(segmentTranslation);
                        }
                    }

                    System.gc();
                }
            }
        }
        else // Whole phrase exists in Wikipedia
        {
            for (Map.Entry<String, String> entry : mongoData.entrySet())
            {
                if (entry.getKey().trim().isEmpty() || entry.getKey().trim().isEmpty())
                {
                    return "";
                }

                String[] englishSentences = SentenceDetector.detectSentences(entry.getKey().replaceAll("\\(.*?\\) ?", ""));
                String[] swahiliSentences = SentenceDetector.detectSentences(entry.getValue().replaceAll("\\(.*?\\) ?", ""));

                if (englishSentences[0].toLowerCase().trim().contains(original.toLowerCase().trim()))
                {

                    try
                    {
                        return (swahiliSentences[0]);
                    }
                    catch (ArrayIndexOutOfBoundsException ex)
                    {
                        translation.add("");
                    }
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

        return (occurranceCount.toString());
//        return (translation.toString());

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
