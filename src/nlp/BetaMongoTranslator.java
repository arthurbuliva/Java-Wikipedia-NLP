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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import opennlp.tools.tokenize.SimpleTokenizer;

import org.bson.Document;

public class BetaMongoTranslator extends TranslatorLogger implements EnglishStopWords, SwahiliStopWords
{

    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final HashMap<String, String> relationship;
    int retries = 1;

    public BetaMongoTranslator()
    {
        relationship = new HashMap<>();
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase("corpus");

    }

    public static void main(String[] args)
    {
//        String english = "The red elephant had a headache at 12 o'clock";
        String english = "The red elephant had a headache at noon";

        BetaMongoTranslator t = new BetaMongoTranslator();

//        if (t.translate(english).isEmpty())
//        {
//            SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
//            String[] wordTokens = tokenizer.tokenize(english);
//            
//            for(String token: wordTokens)
//            {
//                System.out.println(t.translate(token));
//            }
//        }
//        else
//        {
        System.out.println(t.translate(english));
//        }
    }

    public String translate(String original)
    {
        log(Level.INFO, String.format("Translating \"%s\"", new Object[]
        {
            original
        }));

        ArrayList<String> swahiliTranslation = new ArrayList<>();
        ArrayList<String> englishTranslation = new ArrayList<>();

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

        Map<String, String> tokens = Chunker.getSpanTypes(removeStopWords(original));

//        String sentenceTokens[] = WhitespaceTokenizer.INSTANCE.tokenize(original);
//        System.out.println(Arrays.toString(sentenceTokens));
        System.out.println("Tokens: " + tokens);

        String parsedKeyWords = original;

        for (Keyword keyWord : keyWords)
        {
            parsedKeyWords += " \"" + keyWord.getText().trim() + "\"";
        }

        System.out.println("Searching MongoDB: " + parsedKeyWords);

        HashMap<String, String> mongoData = fetchFromMongoDB(parsedKeyWords);

        Map<String, Integer> probabilities = new HashMap<>();
        StringBuilder tx = new StringBuilder();

        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] wordTokens = tokenizer.tokenize(removeStopWords(original));

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
                    System.out.print("\nExact match: ");
                    System.out.println(data.getValue());
//                    return data.getValue();

//                    probabilities.put(data.getValue(), 100);
                    tx.append(data.getValue());
                    tx.append(" ");
                }
                else if (data.getKey().toLowerCase().contains(original.toLowerCase()))
                {
                    System.out.println("Probable match!");
                    System.out.println(data.getValue());
//                    System.out.print("*");
                    tx.append("______");
                    tx.append(" ");
                }
                else if (data.getKey().toLowerCase().contains(key.toLowerCase()))
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