/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

/**
 *
 * @author Arthur
 */
public class SentenceDetector
{

    private String[] sentences;
    private InputStream modelIn;

    public String[] detectSentences(String paragraph)
    {
        try
        {
            // Load the model that we want to use
            modelIn = new FileInputStream("lib/apache-opennlp-1.6.0/models/en-sent.bin");

            SentenceModel model = new SentenceModel(modelIn);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);

            // Run the model against the data
            sentences = sentenceDetector.sentDetect(paragraph);

            return sentences;
        }
        catch (IOException ex)
        {
            return new String[]
            {
                ex.getMessage()
            };
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
