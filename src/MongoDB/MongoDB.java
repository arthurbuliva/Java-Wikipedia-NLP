/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MongoDB;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.HashMap;
import org.bson.Document;

/**
 *
 * @author arthur
 */
public class MongoDB
{

    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final HashMap<String, String> relationship;

    public MongoDB()
    {
        relationship = new HashMap<>();
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase("corpus");
    }

    public HashMap<String, String> fetchFromMongoDB(String original)
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
                
                String english = document.getString("en").replaceAll("\\(.*?\\) ?", "");
                String swahili = document.getString("sw").replaceAll("\\(.*?\\) ?", "");
                String title = document.getString("title");
                String kichwa = document.getString("kichwa");

//                data.put(title, kichwa);
                data.put(english, swahili);
                
            }
        }
        
//        mongoClient.close();

        return data;
    }
}
