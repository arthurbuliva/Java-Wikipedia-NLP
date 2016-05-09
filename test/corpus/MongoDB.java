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
        MongoDatabase db = mongoClient.getDatabase("test");

        FindIterable<Document> iterable = db.getCollection("restaurants").find(
                new Document("borough", "Manhattan"));

        iterable.forEach(new Block<Document>()
        {
            @Override
            public void apply(final Document document)
            {
                System.out.println(document);
            }
        });
        
        
        
//> db.test.drop()
//> db.test.insert({ "t" : "I'm on time, not late or delayed" })
//> db.test.insert({ "t" : "I'm either late or delayed" })
//> db.test.insert({ "t" : "Time flies like a banana" })
//> db.test.ensureIndex({ "t" : "text" })
//
//> db.test.find({ "$text" : { "$search" : "time late delay" } }, { "_id" : 0 })
//{ "t" : "I'm on time, not late or delayed" }
//{ "t" : "Time flies like a banana" }
//{ "t" : "I'm either late or delayed" }
//
//> db.test.find({ "$text" : { "$search" : "late delay" } }, { "_id" : 0 })
//{ "t" : "I'm on time, not late or delayed" }
//{ "t" : "I'm either late or delayed" }
//
//> db.test.find({ "$text" : { "$search" : "late delay \"on time\"" } }, { "_id" : 0 })
//{ "t" : "I'm on time, not late or delayed" }


    }

}
