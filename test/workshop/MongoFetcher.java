/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import nlp.ChunkFrequency;
import nlp.Chunker;
import nlp.EntityFinder;
import nlp.SentenceDetector;
import nlp.TitleMatcher;

import org.bson.Document;

/**
 * MongoDB Quick Guide
 *
 * @author arthur
 *
 * This is how we insert a record into the db:
 *
 * db.getCollection("test").insertOne( new Document() .append("en",
 * "\"Nakumatt\" is an abbreviation for Nakuru Mattress.[1]") .append("sw",
 * "Nakumatt ni mnyororo wa maduka nchini Kenya.\n" + "Ina maduka 18 kote nchini
 * Kenya [1] na inaajiri watu 3,200.\n" + "Ni mipango ya kupanua maduka yake
 * mpaka nchini Uganda, Rwanda na nchi nyingine za Afrika Mashariki.\n" +
 * "Nakumatt ni kampuni ya Kenya inayomilikiwa na familia na Atul Shah Hotnet
 * Ltd.[2] [3]")); This is how we created an index for the text:
 *
 * db.wikipedia.createIndex({ "en" : "text", "sw" : "text" }, {
 * default_language: "english" });
 *
 * This is how we dump data from MongoDB:
 *
 * mongodump --collection wikipedia --db corpus --out __db/dump
 *
 * This is how we restore from the dump:
 *
 * mongorestore --db corpus --noIndexRestore --drop __db/dump/corpus/
 *
 * Search without index ++++++++++++++++++++ DBQuery.shellBatchSize = 300
 * db.wikipedia.find({"sw": /Adelaide wa Italia/}).pretty();
 * db.wikipedia.find().sort({_id:-1}).pretty().limit(1);
 *
 * Search with index ++++++++++++++++++++ db.wikipedia.find({$text: {$search:
 * "Msimu wa mvua"}}).pretty(); db.wikipedia.find({$text: {$search: "\"Jamhuri
 * ya Kenya\""}}).pretty();
 */
public class MongoFetcher
{

    private MongoClient mongoClient;
    private MongoDatabase db;

    public MongoFetcher()
    {
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase("corpus");
    }

    public static void main(String[] args)
    {
        MongoFetcher fetcher = new MongoFetcher();
        fetcher.fetchAll();

    }

    public void fetchAll()
    {

        Document projection = new Document("score", new Document("$meta", "textScore"));

        FindIterable<Document> iterable = db.getCollection("wikipedia")
                .find( //                        new Document("$text", new Document("$search", exactMatch ? String.format("\"%s\"", original) : original)
                //                        )
                )
                .projection(projection)
                .sort(projection)
                .limit(0);

        iterable.forEach(new Block<Document>()
        {
            @Override
            public void apply(final Document document)
            {

                String english = document.getString("en");
                String swahili = document.getString("sw");

                if (english.equals("v")
                        || english.startsWith("org.jsoup.HttpStatusException: HTTP error fetching URL. Status=404")
                        || swahili.equals("v")
                        || swahili.startsWith("org.jsoup.HttpStatusException: HTTP error fetching URL. Status=404")
                        || english.isEmpty()
                        || swahili.isEmpty()
                        || swahili.startsWith("Lango la Historia | Lango la Biografia | Karibuni | Orodha ya Miaka"))
                {
                    //Do Nothing
                }
                else
                {
                    System.out.println(
                            SentenceDetector.detectSentences(english)[0]
                            + " => "
                            + SentenceDetector.detectSentences(swahili)[0]);
                }

            }
        });

    }

}
