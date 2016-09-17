/**
 * MongoFetcher
 *
 * @author arthur
 *
 * Makes an English - Swahili corpus from Wikipedia entries
 */
package workshop;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nlp.SentenceDetector;
import nlp.TranslatorLogger;

import org.bson.Document;

public class MongoFetcher extends TranslatorLogger
{

    private final MongoClient MONGO_CLIENT;
    private final MongoDatabase MONGO_DB;
    private final String SWAHILI_CORPUS_FILE = "sw.wikipedia.txt";
    private final String ENGLISH_CORPUS_FILE = "en.wikipedia.txt";

    private FileWriter ENGLISH_STREAM;
    private BufferedWriter ENGLISH_WRITER;

    private FileWriter SWAHILI_STREAM;
    private BufferedWriter SWAHILI_WRITER;

    public MongoFetcher()
    {
        MONGO_CLIENT = new MongoClient();
        MONGO_DB = MONGO_CLIENT.getDatabase("corpus");

        try
        {
            ENGLISH_STREAM = new FileWriter(ENGLISH_CORPUS_FILE, true);
            ENGLISH_WRITER = new BufferedWriter(ENGLISH_STREAM);

            SWAHILI_STREAM = new FileWriter(SWAHILI_CORPUS_FILE, true);
            SWAHILI_WRITER = new BufferedWriter(SWAHILI_STREAM);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        MongoFetcher fetcher = new MongoFetcher();
        fetcher.fetchAll();
    }

    public void fetchAll()
    {

        Document projection = new Document("score", new Document("$meta", "textScore"));

        FindIterable<Document> iterable = MONGO_DB.getCollection("wikipedia")
                .find()
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

//                System.out.println(
//                        english
//                        //                            SentenceDetector.detectSentences(english)[0]
//                        + " => "
//                        + swahili);
////                            + SentenceDetector.detectSentences(swahili)[0]);
                try
                {
                    if (swahili.trim().length() == 0 || english.trim().length() == 0)
                    {
                        throw new IOException("Blank value Exception");
                    }

                    ENGLISH_WRITER.write(english);
//                        ENGLISH_WRITER.write(SentenceDetector.detectSentences(english)[0]);
                    ENGLISH_WRITER.newLine();

//                        SWAHILI_WRITER.write(SentenceDetector.detectSentences(swahili)[0]);
                    SWAHILI_WRITER.write(swahili);
                    SWAHILI_WRITER.newLine();
                }
                catch (IOException ex)
                {
                    log(ex.getMessage(), Level.SEVERE);
                }
            }
        });

        try
        {
            ENGLISH_WRITER.close();
            SWAHILI_WRITER.close();
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
        }
    }

}
