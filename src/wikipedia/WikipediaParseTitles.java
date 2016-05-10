/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wikipedia;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import nlp.SentenceDetector;
import org.bson.Document;

/**
 * Fetches the first paragraphs of titles in Wikipedia and populates
 * a MongoDB instance with the data
 * 
 * Warning: This file may take a long while to run to completion.
 * 
 * @author arthur
 */
public class WikipediaParseTitles
{

    private String wikiTextEnglish;
    private String wikiTextSwahili;
    private String[] wikiSentences;
    private DataFetcher wikiFetcher;
    private SentenceDetector sentenceDetector;

    /**
     * Fetch the Wikipedia titles from the dumped text file
     * @throws Exception 
     */
    public void fetchWikis() throws Exception
    {
        wikiFetcher = new DataFetcher();
        sentenceDetector = new SentenceDetector();

        File file = new File("lib/wikipedia-parallel-titles-master/Titles.txt");

        Map<String, String> titlesMap = new HashMap<>();

        FileReader fileReader = new FileReader(file);

        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;

        while ((line = bufferedReader.readLine()) != null)
        {
            String[] titlesArray = line.split("\\|\\|\\|");

            titlesMap.put(titlesArray[1].trim(), titlesArray[0].trim());
        }

        fileReader.close();

        titlesMap.forEach((englishTitle, swahiliTitle)
                -> 
                {
                    System.out.println(englishTitle + " -> " + swahiliTitle);

                    wikiTextEnglish = wikiFetcher.fetchData("en", englishTitle);
                    wikiTextSwahili = wikiFetcher.fetchData("sw", swahiliTitle);

                    MongoClient mongoClient = new MongoClient();
                    MongoDatabase db = mongoClient.getDatabase("corpus");

                    db.getCollection("wikipedia").insertOne(
                            new Document()
                            .append("en", wikiTextEnglish)
                            .append("sw", wikiTextSwahili));

//                    for (String sentence : sentenceDetector.detectSentences(wikiTextEnglish))
//                    {
//                        System.out.println(sentence);
//                    }
//                    for (String sentence : sentenceDetector.detectSentences(wikiTextSwahili))
//                    {
//                        System.out.println(sentence);
//                    }

                    System.out.println(englishTitle + " -> " + swahiliTitle);

//                    db.test.insert({ "en" : "I'm on time, not late or delayed" })
        });
    }

    public static void main(String[] args) throws Exception
    {
        WikipediaParseTitles wpt = new WikipediaParseTitles();
        wpt.fetchWikis();
    }
}
