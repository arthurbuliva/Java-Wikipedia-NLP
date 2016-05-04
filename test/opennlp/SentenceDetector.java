/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opennlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import wikipedia.DataFetcher;

/**
 *
 * @author Arthur
 */
public class SentenceDetector
{

    public static void main(String[] args) throws Exception
    {
        // Load the model that we want to use
        InputStream modelIn = new FileInputStream("lib/apache-opennlp-1.6.0/models/en-sent.bin");

        try
        {
            SentenceModel model = new SentenceModel(modelIn);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);

            // Fetch the data from Wikipedia
            DataFetcher wiki = new DataFetcher();
            String data = wiki.fetchData("en", "Central Bank of Kenya");

            // Run the model against the data
            String sentences[] = sentenceDetector.sentDetect(data);

            for (String sentence : sentences)
            {
                System.out.println(sentence);
                // TODO: Load these sentences as parallel texts onto a database
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (modelIn != null)
            {
                try
                {
                    modelIn.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }
}
