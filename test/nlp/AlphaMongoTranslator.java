/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Keyword;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.collections4.CollectionUtils;

import org.bson.Document;

public class AlphaMongoTranslator extends TranslatorLogger
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
        String english = "The candle is bright";
        
        AlphaMongoTranslator t = new AlphaMongoTranslator();

        System.out.println(t.translate(english));
        
        System.exit(0);

        Map<String, String> chunks = Chunker.getSpanTypes(english);
        System.out.println(chunks);

        for (Map.Entry<String, String> entry : chunks.entrySet())
        {
            String key = entry.getKey().toLowerCase().trim();
            String value = entry.getValue();

            log(Level.INFO, key);
            System.out.println(t.translate(key, value));
        }

    }

    public String translate(String original)
    {
        log(Level.INFO, String.format("Translating \"%s\"", new Object[]
        {
            original
        }));

        StringBuilder translation = new StringBuilder();

        // Sometimes the original word is just a title in Wikipedia
        TitleMatcher titleMatcher = new TitleMatcher();

        if ((titleMatcher.translate(original.trim()).replaceAll("\\[", "").replaceAll("\\]", "").length() > 0))
        {
            return (titleMatcher.translate(original.trim()));
        }

        Document projection = new Document("score", new Document("$meta", "textScore"));

        // Get the key words
        
        AlchemyLanguage service = new AlchemyLanguage();
        service.setApiKey("e24397903a386ad615e7922ed5907557e76bb336");

        Map<String, Object> params = new HashMap<>();
        params.put(AlchemyLanguage.TEXT, original);
        
        List<Keyword> keyWords = service.getKeywords(params).execute().getKeywords();
        
        for (Keyword keyWord: keyWords)
        {
            original = "\"" + keyWord.getText() + "\" " + original;
        }

        System.out.println(original);
        
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
                
                translation.append(swahili);
                translation.append("\n\n");
            }
        }

        return translation.toString();
    }

    public String translate(String original, String partOfSpeech)
    {
        log(Level.INFO, String.format("Translating %s as %s", new Object[]
        {
            original, partOfSpeech
        }));

        String translation = "";

        // Sometimes the original word is just a title in Wikipedia
        TitleMatcher titleMatcher = new TitleMatcher();

        if ((titleMatcher.translate(original.trim()).replaceAll("\\[", "").replaceAll("\\]", "").length() > 0))
        {
            return (titleMatcher.translate(original.trim()));
        }

        Document projection = new Document("score", new Document("$meta", "textScore"));

        // db.wikipedia.find({$text: {$search: "Please give me a glass of Water", 
        // $language: "en", $caseSensitive: true}}, {score: {$meta: "textScore"}})
        // .sort({score:{$meta:"textScore"}}).pretty().limit(0);
        FindIterable<Document> iterable = db.getCollection("wikipedia")
                .find(
                        new Document("$text", new Document("$search", String.format("%s", original))
                        )
                //                        .append("language", "en")
                )
                .projection(projection)
                .sort(projection)
                .limit(0);

        StringBuilder englishWords = new StringBuilder();
        StringBuilder swahiliWords = new StringBuilder();

        List originalAsArray = Arrays.asList(original.toLowerCase().trim());

        log(Level.INFO, "Original as array: " + originalAsArray.toString());

        iterable.forEach(new Block<Document>()
        {
            @Override
            public void apply(final Document document)
            {

                String english = document.getString("en");
                String swahili = document.getString("sw");

                Map<String, String> chunks = (Chunker.getSpanTypes(english));

                for (Map.Entry<String, String> entry : chunks.entrySet())
                {
                    String key = entry.getKey().toLowerCase().trim();
                    Object value = entry.getValue();

                    //If any of the words in the original as an array
                    //are contained in the key as an array
                    List keyAsArray = Arrays.asList(key);

                    if (CollectionUtils.containsAny(originalAsArray, keyAsArray))
                    {
//                        System.out.println(original + "****" + key);

                        englishWords.append(english);
                        swahiliWords.append(swahili);

                        if (!swahili.isEmpty())
                        {
                            System.out.println(Chunker.getSpanTypes(english));
                            System.out.println(swahili);
                        }

                        relationship.put(swahili, english);

                    }
                    else
                    {
//                        System.out.print("*");
                    }
                }

            }
        });

        ArrayList<String> sentences = new ArrayList<>(
                Arrays.asList(
                        SentenceDetector.detectSentences(englishWords.toString()
                        )
                )
        );
        ArrayList<String> sentensi = new ArrayList<>(
                Arrays.asList(
                        SentenceDetector.detectSentences(swahiliWords.toString()
                        )
                )
        );
        for (String sentence : sentences)
        {

            if (sentence.contains(original)
                    || sentensi.contains(original))
            {

                if (sentence.contains(original))
                {
                    // Get the index of this string in the array
                    int indexInArray = sentences.indexOf(sentence);

//                        System.out.println(Chunker.chunk(sentensi.get(indexInArray)));
                    return (sentensi.get(indexInArray));
                }
                else
                {
                    // Get the index of this string in the array

                    // Get the index of this string in the array
                    int indexInArray = sentensi.indexOf(sentence);

//                        System.out.println(Chunker.chunk(sentences.get(indexInArray)));
                    return (sentences.get(indexInArray));
                }

            }
        }

        Map enFreq = ChunkFrequency.getFrequencies(englishWords.toString());
        Map swFreq = ChunkFrequency.getFrequencies(swahiliWords.toString());

        return translation;
    }

}
