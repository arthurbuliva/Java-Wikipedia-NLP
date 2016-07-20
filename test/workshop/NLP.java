/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workshop;

import nlp.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import wikipedia.WikipediaDataFetcher;

/**
 *
 * @author Arthur
 */
public class NLP
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {

        /**
         * Algorithm:
         *
         * 1. After the dump process has been completed, we get list of articles
         * which have been paired by language, for example:
         *
         * Central Bank of Kenya ||| Benki Kuu ya Kenya
         *
         * 2. What we need is to fetch each of the articles, grouped into EN and
         * SW
         *
         * 3. Then add each into the MongoDB instance
         */
        final String SEPARATOR = "\\|\\|\\|";
        
        String[] languagePairs =
        {
            "en", "sw"
        };

        String titles = "Central Bank of Kenya ||| Benki Kuu ya Kenya";

        String[] titlesArray = titles.split(SEPARATOR);

        HashMap<String, String> hash = new HashMap<>();

        for (int i = 0; i < titlesArray.length; i++)
        {
            hash.put(languagePairs[i], titlesArray[i].trim());
        }
        
        System.out.println(hash);

        System.exit(0);

        WikipediaDataFetcher wiki = new WikipediaDataFetcher();
        String data = wiki.fetchData("en", "Central Bank of Kenya");

        // Run the model against the data
        String[] sentences = new SentenceDetector().detectSentences(data);

        for (String sentence : sentences)
        {
            System.out.println(sentence);
            // TODO: Load these sentences as parallel texts onto a database
        }
    }

}
