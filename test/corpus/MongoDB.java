/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import nlp.SentenceDetector;
import nlp.Chunker;

import org.bson.Document;

/**
 *
 * @author arthur
 */
public class MongoDB
{

    public static void main(String[] args) throws Exception
    {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("corpus");
        HashMap<String, String> relationship = new HashMap<>();

        // This is how we insert a record into the db:
        //db.getCollection("test").insertOne(
        //        new Document()
        //        .append("en", "\"Nakumatt\" is an abbreviation for Nakuru Mattress.[1]")
        //        .append("sw", "Nakumatt ni mnyororo wa maduka nchini Kenya.\n"
        //                + "Ina maduka 18 kote nchini Kenya [1] na inaajiri watu 3,200.\n"
        //                + "Ni mipango ya kupanua maduka yake mpaka nchini Uganda, Rwanda na nchi nyingine za Afrika Mashariki.\n"
        //                + "Nakumatt ni kampuni ya Kenya inayomilikiwa na familia na Atul Shah Hotnet Ltd.[2] [3]"));
        // This is how we created an index for the text:
        //
        // db.wikipedia.createIndex({ "en" : "text", "sw" : "text" })
        //
        // This is how we restore from the dump:
        //
        // mongorestore --db corpus --noIndexRestore --drop __db/dump/corpus/
        //
        // db.wikipedia.find({$text: {$search: "Msimu wa mvua"}}).pretty();
        //
        // db.wikipedia.find({$text: {$search: "\"Jamhuri ya Kenya\""}}).pretty();
        //
        FindIterable<Document> iterable = db.getCollection("wikipedia").find(
                new Document("$text", new Document("$search", "\"Kusini mashariki\""))
        );

        StringBuilder englishWords = new StringBuilder();
        StringBuilder swahiliWords = new StringBuilder();

        iterable.forEach(new Block<Document>()
        {
            @Override
            public void apply(final Document document)
            {
//                System.out.println(document.getString("en"));
//                System.out.println(document.getString("sw"));

                String english = document.getString("en");
                String swahili = document.getString("sw");

                englishWords.append(english);
                swahiliWords.append(swahiliWords);

                relationship.put(english, swahili);

                try
                {
                    ArrayList englishChunks = Chunker.chunk(english);
                    ArrayList swahiliChunks = Chunker.chunk(swahili);

                    System.out.print(englishChunks.size());
                    System.out.print(" => ");
                    System.out.println(englishChunks);
                    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
                    System.out.print(swahiliChunks.size());
                    System.out.print(" => ");
                    System.out.println(swahiliChunks);
                    
                    
                    System.out.println(Chunker.getSpanTypesFromChunks(english));
                    System.out.println(Chunker.getSpanTypesFromChunks(swahili));
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        });

        mongoClient.close();

        System.exit(0);

        for (Map.Entry<String, String> entry : relationship.entrySet())
        {
            String key = entry.getKey();
            Object value = entry.getValue();

            System.out.println(key);
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println(value);
            System.out.println("-----------------------------------------------");
        }

        SentenceDetector sentence = new SentenceDetector();

        String[] paragraph = sentence.detectSentences(englishWords.toString());
        String[] aya = sentence.detectSentences(swahiliWords.toString());

        for (String sent : paragraph)
        {
            System.out.println(sent);
        }
        for (String sente : aya)
        {
            System.out.println(sente);
        }

    }
}
