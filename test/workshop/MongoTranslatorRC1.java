/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import nlp.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import opennlp.tools.tokenize.SimpleTokenizer;

import org.bson.Document;

public class MongoTranslatorRC1 extends TranslatorLogger implements EnglishStopWords, SwahiliStopWords
{

    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final HashMap<String, String> relationship;
    int retries = 1;
    private ArrayList<String> translation = new ArrayList<>();

    public MongoTranslatorRC1()
    {
        relationship = new HashMap<>();
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase("corpus");

    }

    public static void main(String[] args)
    {
//        String english = "The red elephant had a headache at 12 o'clock";
//        String english = "The city of Nairobi";
//        String english = "They went to the zoo";
//        String english = "The red elephant had a headache at noon";
//        String english = "Cancer of the blood";
//        String english = "Visit the doctor";
//        String english = "The exams will be done next week";
//        String english = "The horn of Africa";
//        String english = "Kenya is a country";
//        String english = "Machakos is a town in Kenya";
        String english = "That is a pretty girl";
//        String english = "The teacher will not come today";

        MongoTranslatorRC1 t = new MongoTranslatorRC1();

        
        System.out.println(t.translate(english.toLowerCase()));

    }

    public String translate(String original)
    {
        log(Level.INFO, String.format("Translating \"%s\"", new Object[]
        {
            original
        }));
        
        

        // Sometimes the original word is just a title in Wikipedia
        TitleMatcher titleMatcher = new TitleMatcher();

        if ((titleMatcher.translate(original.trim()).replaceAll("\\[", "").replaceAll("\\]", "").length() > 0))
        {
            return (titleMatcher.translate(original.trim()));
        }
        else if (original.startsWith("of"))
        {
            translation.add("ya ");
            return translate(original.replaceFirst("of", "").trim());
        }
        else if (original.startsWith("is"))
        {
            translation.add("ni ");
            return translate(original.replaceFirst("is", "").trim());
        }
        else if (original.startsWith("the"))
        {
            return translate(original.replaceFirst("the", "").trim());
        }

        String parsedKeyWords = original;

        System.out.println("Searching MongoDB: " + parsedKeyWords);

        HashMap<String, String> mongoData = fetchFromMongoDB(String.format("\"%s\"", new Object[]
        {
            parsedKeyWords
        }));

        if (mongoData.isEmpty())
        {
            ArrayList<String> whole = new BetaStringBreaker().breakString(original);

            System.out.println("Segmented: " + whole);

            for (String segment : whole)
            {

//                System.out.println(segment);
                System.out.println("Empty segment. Translating -> " + (segment));
                System.gc();
                translation.add(translate(segment));
            }
        }
        else
        {
            SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
                        String[] wordTokens = tokenizer.tokenize(removeStopWords(original));

            for (String entry : wordTokens)
            {
                System.out.println(entry);
            }
        }
        
        return translation.toString();
//        System.exit(0);
/**
        Map<String, Integer> probabilities = new HashMap<>();
        StringBuilder tx = new StringBuilder();

        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
//        String[] wordTokens = tokenizer.tokenize(removeStopWords(original));
        String[] wordTokens = tokenizer.tokenize(original);

        // Go through the tokens one by one
//        for (Map.Entry<String, String> entry : tokens.entrySet())
//        for (Keyword keyWord : keyWords)
        for (String entry : wordTokens)
        {
//            String key = entry.getKey().trim();
//            String key = keyWord.getText().trim();

            String key = entry;

            if (removeStopWords(key).isEmpty())
            {
                continue;
            }

            Lemmatizer lemmatizer = new Lemmatizer();
//            System.out.println(lemmatizer.lemmatize(key, tokens.get(entry)));

            System.out.println("=================" + key + "=================");

            for (Map.Entry<String, String> data : mongoData.entrySet())
            {
                if (data.getKey().toLowerCase().equalsIgnoreCase(key))
                {
//                    System.out.print("\nExact match: ");
//                    System.out.println(data.getValue());
//                    return data.getValue();

//                    probabilities.put(data.getValue(), 100);
                    tx.append(data.getValue());
                    tx.append(" ");
                }

//                String str = "today is tuesday";
//return str.matches(".*?\\bt\\b.*?"); // returns "false"
//
//String str = "today is t uesday";
//return str.matches(".*?\\bt\\b.*?"); // returns "true"
//              else if (data.getKey().toLowerCase().contains(original.toLowerCase()))
                else if (data.getKey().toLowerCase().matches(".*?\\b" + original.toLowerCase() + "\\b.*?"))
                {
//                    System.out.println("Probable match!");
//                    System.out.println(data.getValue());
//                    System.out.print("*");
                    tx.append("______");
                    tx.append(" ");
                }
//                else if (data.getKey().toLowerCase().contains(key.toLowerCase()))
                else if (data.getKey().toLowerCase().matches(".*?\\b" + key.toLowerCase() + "\\b.*?"))
                {
//                    System.out.println("Probable match!");
//                    System.out.println(data.getValue());
//                    System.out.print("|");
//                    tx.append("*");
//                    tx.append(" ");
                }
            }
        }

        if (tx.toString().isEmpty())
        {
            for (String token : wordTokens)
            {
                if (retries >= 5)
                {
                    tx.append("______");
                    tx.append(" ");
                }
                else
                {
                    System.out.println(token);
                    tx.append(translate(token));
                    tx.append(" ");
                    retries++;
//                    System.out.println("***************************RETRIES: " + retries);
                }
            }
        }

        return tx.toString();
        * 
        * **/
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

    private HashMap<String, String> fetchFromMongoDB(String original)
    {
        Document projection = new Document("score", new Document("$meta", "textScore"));

        HashMap<String, String> data = new HashMap<>();

        try (
                MongoCursor<Document> cursor = db.getCollection("wikipedia")
                .find(
                        new Document("$text",
                                new Document("$search", String.format("%s", original))
                                .append("$language", "en")
                                .append("$caseSensitive", false)
                        )
                )
                .projection(projection)
                .sort(projection)
                .limit(0)
                .iterator())
        {
            while (cursor.hasNext())
            {
                Document document = cursor.next();

//                System.out.println(Chunker.getSpanTypes(document.getString("en")));
                String english = document.getString("en");
                String swahili = document.getString("sw");
                String title = document.getString("title");
                String kichwa = document.getString("kichwa");

                data.put(title, kichwa);
                data.put(english, swahili);
            }
        }

        return data;
    }
}
