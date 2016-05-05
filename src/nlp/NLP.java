/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.IOException;
import wikipedia.DataFetcher;

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
         * 2. What we need is to fetch each of the articles, grouped into EN and SW
         * 
         * 3. Then add each into the MongoDB instance
         */
        
        DataFetcher wiki = new DataFetcher();
        String dataEnglish = wiki.fetchData("en", "Navy");
        String dataSwahili = wiki.fetchData("sw", "Jeshi la wanamaji");

        // Run the model against the data
        String[] englishSentences = new SentenceDetector().detectSentences(dataEnglish);
        String[] swahiliSentences = new SentenceDetector().detectSentences(dataSwahili);

        for (String english : englishSentences)
        {
            System.out.println(english);
            // TODO: Load these sentences as parallel texts onto a database
        }
        for (String swahili : swahiliSentences)
        {
            System.out.println(swahili);
            // TODO: Load these sentences as parallel texts onto a database
        }
    }

}
