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
import nlp.ChunkFrequency;
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
                new Document("$text", new Document("$search", "\"chekechea\""))
        );

        StringBuilder englishWords = new StringBuilder();
        StringBuilder swahiliWords = new StringBuilder();

        iterable.forEach(new Block<Document>()
        {
            @Override
            public void apply(final Document document)
            {

                String english = document.getString("en");
                String swahili = document.getString("sw");

                englishWords.append(english);
                swahiliWords.append(swahili);

                relationship.put(english, swahili);
            }
        });

        mongoClient.close();

        try
        {
            System.out.println(ChunkFrequency.getFrequencies(englishWords.toString()));
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println(ChunkFrequency.getFrequencies(swahiliWords.toString()));
            
            

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
