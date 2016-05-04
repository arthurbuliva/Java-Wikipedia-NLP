/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
        DataFetcher wiki = new DataFetcher();
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
