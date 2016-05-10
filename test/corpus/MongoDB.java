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

        // This is how we insert a record into the db:
        
        //db.getCollection("test").insertOne(
        //        new Document()
        //        .append("en", "\"Nakumatt\" is an abbreviation for Nakuru Mattress.[1]")
        //        .append("sw", "Nakumatt ni mnyororo wa maduka nchini Kenya.\n"
        //                + "Ina maduka 18 kote nchini Kenya [1] na inaajiri watu 3,200.\n"
        //                + "Ni mipango ya kupanua maduka yake mpaka nchini Uganda, Rwanda na nchi nyingine za Afrika Mashariki.\n"
        //                + "Nakumatt ni kampuni ya Kenya inayomilikiwa na familia na Atul Shah Hotnet Ltd.[2] [3]"));


        // This is how we created an index for the text:
        // db.wikipedia.createIndex({ "en" : "text", "sw" : "text" })

        FindIterable<Document> iterable = db.getCollection("wikipedia").find(
                new Document("$text", new Document("$search", "ukimwi"))        
        );
        
        iterable.forEach(new Block<Document>()
        {
            @Override
            public void apply(final Document document)
            {
                System.out.println(document);
            }
        });
    }
}
