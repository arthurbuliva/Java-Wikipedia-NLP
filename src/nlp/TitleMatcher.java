/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import static nlp.TranslatorLogger.log;
import org.bson.Document;

/**
 *
 * @author arthur
 */
public class TitleMatcher
{

    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final HashMap<String, String> relationship;

    public TitleMatcher()
    {
        relationship = new HashMap<>();
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase("corpus");
    }

    public String translate(String word)
    {
        log(Level.INFO, String.format("Searching for title: \"%s\"", new Object[]
        {
            word
        }));

        ArrayList< String> data = new ArrayList<>();

        try (
                MongoCursor<Document> cursor = db.getCollection("wikipedia")
                .find(
                        new Document("title",
                                new Document("$regex", String.format("\\b%s\\b", word))
                                .append("$options", "i")
                        )
                )
                .iterator())
        {
            while (cursor.hasNext())
            {
                Document document = cursor.next();
                
                String title = document.getString("title");
                String kichwa = document.getString("kichwa");
                
                if(title.trim().equalsIgnoreCase(word.trim()))
                {
                    data.add(kichwa);
                }
            }
        }

        return data.toString();
    }
}
