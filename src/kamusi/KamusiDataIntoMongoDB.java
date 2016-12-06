/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kamusi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import nlp.TranslatorLogger;
import org.bson.Document;

/**
 * Fetches the first paragraphs of titles in Wikipedia and populates a MongoDB
 * instance with the data
 *
 * Warning: This file may take a long while to run to completion.
 *
 * @author arthur
 */
public class KamusiDataIntoMongoDB extends TranslatorLogger
{

    private final String DATABASE = "jdbc:sqlite:kamusiproject.db";
    private final String USERNAME = System.getProperty("database_username");
    private final String PASSWORD = System.getProperty("database_password");

    private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;

    private final MongoClient mongoClient = new MongoClient();
    private final MongoDatabase db = mongoClient.getDatabase("corpus");

    /**
     * Fetch the Wikipedia titles from the dumped text file
     *
     */
    public void fetchWikis()
    {

        try
        {
            Class.forName("org.sqlite.JDBC").newInstance();

            String query = "SELECT * FROM dict";
            connection = DriverManager.getConnection(DATABASE, USERNAME, PASSWORD);
            statement = connection.prepareStatement(query);

            resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                System.out.println(resultSet.getString("SwahiliWord"));

                db.getCollection("kamusi")
                        .insertOne(
                                new Document()
                                .append("SwahiliSortBy", resultSet.getString("SwahiliSortBy"))
                                .append("EnglishSortBy", resultSet.getString("EnglishSortBy"))
                                .append("SwahiliWord", resultSet.getString("SwahiliWord"))
                                .append("EnglishWord", resultSet.getString("EnglishWord"))
                                .append("SwahiliPlural", resultSet.getString("SwahiliPlural"))
                                .append("EnglishPlural", resultSet.getString("EnglishPlural"))
                                .append("SwahiliDefinition", resultSet.getString("SwahiliDefinition"))
                                .append("SwahiliExample", resultSet.getString("SwahiliExample"))
                                .append("EnglishExample", resultSet.getString("EnglishExample"))
                        );
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
//            log(Level.SEVERE, ex.getMessage());
        }
        finally
        {
            mongoClient.close();
        }
    }

    public static void main(String[] args) throws Exception
    {
        KamusiDataIntoMongoDB wpt = new KamusiDataIntoMongoDB();
        wpt.fetchWikis();
    }
}
