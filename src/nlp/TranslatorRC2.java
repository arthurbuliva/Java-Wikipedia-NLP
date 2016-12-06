/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import MongoDB.TitleTranslator;
import MongoDB.MongoDB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import nlp.Chunker;
import nlp.EnglishStopWords;
import nlp.SentenceDetector;
import nlp.SwahiliStopWords;
import nlp.TranslatorLogger;
import opennlp.tools.tokenize.SimpleTokenizer;

public class TranslatorRC2 extends TranslatorLogger implements EnglishStopWords, SwahiliStopWords
{

    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final MongoDB connection;
    private final HashMap<String, String> relationship;
    int retries = 1;

    private ArrayList<String> translation = new ArrayList<>();

    public TranslatorRC2()
    {
        connection = new MongoDB();
        relationship = new HashMap<>();
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase("corpus");
    }

    public String removeDoubleSpaces(String spacedOut)
    {
        while (spacedOut.contains("  "))
        {
            spacedOut = spacedOut.replaceAll("  ", " ");
        }

        return spacedOut;
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
        else if (original.toLowerCase().startsWith("in ") || original.equalsIgnoreCase("in"))
        {
            translation.add("kwa ");
            return translate(original.replaceFirst("in", "").trim());
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
        else if (original.toLowerCase().startsWith("that ") || original.equalsIgnoreCase("that"))
        {
            translation.add("ambayo ");
            return translate(original.toLowerCase().replaceFirst("that", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("for ") || original.equalsIgnoreCase("for"))
        {
            translation.add("ya ");
            return translate(original.toLowerCase().replaceFirst("for", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("from ") || original.equalsIgnoreCase("from"))
        {
            translation.add("kutoka ");
            return translate(original.toLowerCase().replaceFirst("from", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("have ") || original.equalsIgnoreCase("have"))
        {
            translation.add("nina ");
            return translate(original.toLowerCase().replaceFirst("have", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("with ") || original.equalsIgnoreCase("with"))
        {
            translation.add("na ");
            return translate(original.toLowerCase().replaceFirst("with", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("and ") || original.equalsIgnoreCase("and"))
        {
            translation.add("na ");
            return translate(original.toLowerCase().replaceFirst("and", "").trim().toLowerCase());
        }
        else if (original.toLowerCase().startsWith("i ") || original.equalsIgnoreCase("i"))
        {
            translation.add("mimi ");
            return translate(original.toLowerCase().replaceFirst("i", "").trim().toLowerCase());
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

//        return (occurranceCount.toString());
        String result = translation.toString();

        result = removeDoubleSpaces(result.replaceAll("\\p{Punct}", ""));

        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> cleanedwords = new ArrayList<>();

        System.out.println(result);

        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] wordTokens = tokenizer.tokenize(result.trim());

        try
        {
            for (int i = 0; i < wordTokens.length; i++)
            {
                String segment = wordTokens[i] + " " + wordTokens[i + 1];
                
                if (!words.contains(segment)
                        && !wordTokens[i].trim().equalsIgnoreCase(wordTokens[i + 1].trim()))
                {
                    words.add(segment);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {

        }
        
        String[] tokens = tokenizer.tokenize(words.toString().replaceAll("\\p{Punct}", ""));
        
        for (String token : tokens)
        {
            if (cleanedwords.size() > 0)
            {
                if ((!cleanedwords.get(cleanedwords.size() - 1).equalsIgnoreCase(token)) && !cleanedwords.contains(token))
                {
                    cleanedwords.add(token);
                }
            }
            else
            {
                cleanedwords.add(token);
            }
        }

        String cleanedString = cleanedwords.toString().replaceAll("\\p{Punct}", "");

        System.out.println(cleanedString);

        String[] nextSegmentArray = tokenizer.tokenize(cleanedString);
        String nextSegment = nextSegmentArray[0] + " " + nextSegmentArray[1];

        String clean = removeDoubleSpaces(nextSegment + cleanedString.replaceAll(nextSegment, ""));
        
        System.out.println("Original number of words: ");
        System.out.println("Translated number of words: ");

        return clean;
    }

    public String removeStopWords(String sentence)
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
    
    public static void main(String[] args)
    {
        TranslatorRC2 t = new TranslatorRC2();
        
        String sentence = "Question";
        
        sentence = t.removeStopWords(sentence);
        
        System.out.println(sentence);
        
        sentence = t.translate(sentence);
        
        System.out.println(sentence);
    }
}
