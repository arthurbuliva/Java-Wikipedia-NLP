/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import opennlp.tools.tokenize.WhitespaceTokenizer;
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
        String english = "I don’t think we’ve met before. My name is Mr. Obama.";

        AlphaMongoTranslator t = new AlphaMongoTranslator();

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

    public String translate(String original, String partOfSpeech)
    {
        log(Level.INFO, String.format("Translating %s as %s", new Object[]{original, partOfSpeech}));

        String translation = "";

        // Sometimes the original word is just a title in Wikipedia
        TitleMatcher titleMatcher = new TitleMatcher();

        if ((titleMatcher.translate(original.trim()).replaceAll("\\[", "").replaceAll("\\]", "").length() > 0))
        {
            return (titleMatcher.translate(original.trim()));
        }

        Document projection = new Document("score", new Document("$meta", "textScore"));

        FindIterable<Document> iterable = db.getCollection("wikipedia")
                .find(
                        new Document("$text", new Document("$search", String.format("%s", original))
                        )
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
