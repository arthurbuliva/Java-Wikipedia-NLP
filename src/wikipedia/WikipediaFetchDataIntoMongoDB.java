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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import nlp.SentenceDetector;
import org.bson.Document;

/**
 * Fetches the first paragraphs of titles in Wikipedia and populates a MongoDB
 * instance with the data
 *
 * Warning: This file may take a long while to run to completion.
 *
 * @author arthur
 */
public class WikipediaFetchDataIntoMongoDB
{

    private String wikiTextEnglish;
    private String wikiTextSwahili;
    private String[] wikiSentences;
    private WikipediaDataFetcher wikiFetcher;
    private long numberOfLines;
    private long currentLine = 0;
    private final MongoClient mongoClient = new MongoClient();
    private final MongoDatabase db = mongoClient.getDatabase("corpus");

    /**
     * Fetch the Wikipedia titles from the dumped text file
     *
     * @throws Exception
     */
    public void fetchWikis()
    {
        wikiFetcher = new WikipediaDataFetcher();

        try
        {

            File file = new File("lib/wikipedia-parallel-titles-master/Titles.txt");

            Map<String, String> titlesMap = new LinkedHashMap<>();

            FileReader fileReader = new FileReader(file);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            while ((line = bufferedReader.readLine()) != null)
            {
                String[] titlesArray = line.split("\\|\\|\\|");

                titlesMap.put(titlesArray[1].trim(), titlesArray[0].trim());
            }

            fileReader.close();

            try (Stream<String> lines = Files.lines(file.toPath(), Charset.defaultCharset()))
            {
                numberOfLines = lines.count();
            }

            titlesMap.forEach((englishTitle, swahiliTitle)
                    -> 
                    {
                        currentLine++;
                        
                        System.out.print("Record " + currentLine + " of " + numberOfLines + "\t");
                        System.out.print((currentLine * 100)/numberOfLines);
                        System.out.println("%");

                        wikiTextEnglish = wikiFetcher.fetchData("en", englishTitle);
                        wikiTextSwahili = wikiFetcher.fetchData("sw", swahiliTitle);

                        List englishArray = new ArrayList<>();
                        List swahiliArray = new ArrayList<>();

                        englishArray.addAll(Arrays.asList(SentenceDetector.detectSentences(wikiTextEnglish)));
                        swahiliArray.addAll(Arrays.asList(SentenceDetector.detectSentences(wikiTextSwahili)));

                        db.getCollection("wikipedia")
                                .insertOne(
                                        new Document()
                                        .append("title", englishTitle)
                                        .append("en", wikiTextEnglish)
                                        .append("kichwa", swahiliTitle)
                                        .append("sw", wikiTextSwahili)
                                //                                        .append("en", englishArray)
                                //                                        .append("sw", swahiliArray)
                                );
            });

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            mongoClient.close();
        }
    }

    public static void main(String[] args) throws Exception
    {
        WikipediaFetchDataIntoMongoDB wpt = new WikipediaFetchDataIntoMongoDB();
        wpt.fetchWikis();
    }
}
