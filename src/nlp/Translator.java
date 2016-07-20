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

import org.bson.Document;

/**
 *
 * @author arthur
 */
public class Translator
{

    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final String DATABASE = "corpus";

    public Translator()
    {
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase(DATABASE);
    }

    public void translate(String word, boolean exactMatch)
    {

        // We need to fing the root of the word first
        FindIterable<Document> iterable = db.getCollection("wikipedia").find(
                new Document("$text", new Document("$search", exactMatch ? String.format("\"%s\"", word) : word))
        );

        StringBuilder englishWords;
        StringBuilder swahiliWords;

        englishWords = new StringBuilder();
        swahiliWords = new StringBuilder();

        iterable.forEach(new Block<Document>()
        {
            @Override
            public void apply(final Document document)
            {
                englishWords.append(document.getString("en"));
                swahiliWords.append(document.getString("sw"));
            }
        });

        mongoClient.close();

        SentenceDetector sentenceDetector = new SentenceDetector();

        String[] paragraph = sentenceDetector.detectSentences(englishWords.toString());
        String[] aya = sentenceDetector.detectSentences(swahiliWords.toString());

        for (String sentences : paragraph)
        {
            System.out.println(sentences);
        }
        for (String sentensi : aya)
        {
            System.out.println(sentensi);
        }

    }

    public static void main(String[] args)
    {
        Translator translator = new Translator();
        translator.translate("red blood cells", true);
    }
}
